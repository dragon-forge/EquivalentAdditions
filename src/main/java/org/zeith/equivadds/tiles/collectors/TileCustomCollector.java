package org.zeith.equivadds.tiles.collectors;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.capability.managing.*;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.block_entities.*;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.items.*;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.zeith.equivadds.container.ContainerCustomCollector;
import org.zeith.equivadds.init.EnumCollectorTiersEA;
import org.zeith.equivadds.tiles.relays.TileCustomRelay;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.mcf.NormalizedTicker;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class TileCustomCollector
		extends CapabilityEmcBlockEntity
		implements MenuProvider
{
	private final ItemStackHandler input;
	private final StackHandler auxSlots;
	private final CombinedInvWrapper toSort;
	public static final int UPGRADING_SLOT = 0;
	public static final int UPGRADE_SLOT = 1;
	public static final int LOCK_SLOT = 2;
	private final long emcGen;
	private boolean hasChargeableItem;
	private boolean hasFuel;
	private double unprocessedEMC;
	private boolean needsCompacting;
	
	public TileCustomCollector(EnumCollectorTiersEA tier, BlockPos pos, BlockState state)
	{
		super(tier.getTileType(), pos, state, tier.getStorage());
		
		this.input = new EmcBlockEntity.StackHandler(this.getInvSize())
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				super.onContentsChanged(slot);
				TileCustomCollector.this.needsCompacting = true;
			}
		};
		
		this.auxSlots = new StackHandler(3)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				super.onContentsChanged(slot);
				if(slot == 0) TileCustomCollector.this.needsCompacting = true;
			}
		};
		
		this.toSort = new CombinedInvWrapper(
				new RangedWrapper(this.auxSlots, 0, 1),
				this.input
		);
		
		this.needsCompacting = true;
		this.emcGen = tier.getGenRate();
		this.itemHandlerResolver = new TileCustomCollector.CollectorItemHandlerProvider();
	}
	
	public static void tickServer(Level level, BlockPos pos, BlockState state, TileCustomCollector self)
	{
		self.serverTicker.tick(level);
	}
	
	public final NormalizedTicker serverTicker = NormalizedTicker.create(this::serverTick);
	
	public void serverTick(int suppressed)
	{
		if(needsCompacting)
		{
			ItemHelper.compactInventory(toSort);
			needsCompacting = false;
		}
		
		checkFuelOrKlein();
		updateEmc(suppressed);
		rotateUpgraded();
		updateComparators();
	}
	
	@Override
	protected boolean canAcceptEmc()
	{
		return this.hasFuel || this.hasChargeableItem;
	}
	
	public IItemHandler getInput()
	{
		return this.input;
	}
	
	public IItemHandler getAux()
	{
		return this.auxSlots;
	}
	
	protected int getInvSize()
	{
		return 8;
	}
	
	private ItemStack getUpgraded()
	{
		return this.auxSlots.getStackInSlot(1);
	}
	
	private ItemStack getLock()
	{
		return this.auxSlots.getStackInSlot(2);
	}
	
	private ItemStack getUpgrading()
	{
		return this.auxSlots.getStackInSlot(0);
	}
	
	public void clearLocked()
	{
		this.auxSlots.setStackInSlot(2, ItemStack.EMPTY);
	}
	
	@Override
	protected boolean emcAffectsComparators()
	{
		return true;
	}
	
	private void rotateUpgraded()
	{
		ItemStack upgraded = this.getUpgraded();
		if(!upgraded.isEmpty() && (this.getLock().isEmpty() || upgraded.getItem() != this.getLock().getItem() || upgraded.getCount() >= upgraded.getMaxStackSize()))
			this.auxSlots.setStackInSlot(1, ItemHandlerHelper.insertItemStacked(this.input, upgraded.copy(), false));
	}
	
	private void checkFuelOrKlein()
	{
		ItemStack upgrading = this.getUpgrading();
		if(!upgrading.isEmpty())
		{
			Optional<IItemEmcHolder> emcHolder = upgrading.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
			if(emcHolder.isPresent())
			{
				if(emcHolder.get().getNeededEmc(upgrading) > 0L)
				{
					this.hasChargeableItem = true;
					this.hasFuel = false;
				} else
				{
					this.hasChargeableItem = false;
				}
			} else
			{
				this.hasFuel = true;
				this.hasChargeableItem = false;
			}
		} else
		{
			this.hasFuel = false;
			this.hasChargeableItem = false;
		}
	}
	
	private void updateEmc(int suppressed)
	{
		if(!this.hasMaxedEmc())
		{
			this.unprocessedEMC += (float) this.emcGen * ((float) this.getSunLevel() / 320.0F) * suppressed;
			if(this.unprocessedEMC >= 1.0)
			{
				this.unprocessedEMC -= (double) this.forceInsertEmc((long) this.unprocessedEMC, EmcAction.EXECUTE);
			}
			
			this.markDirty(false);
		}
		
		if(this.getStoredEmc() > 0L)
		{
			ItemStack upgrading = this.getUpgrading();
			if(this.hasChargeableItem)
			{
				for(int i = 0; i < suppressed; ++i)
					upgrading.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).ifPresent((emcHolder) ->
					{
						long actualInserted = emcHolder.insertEmc(upgrading, Math.min(this.getStoredEmc(), this.emcGen), EmcAction.EXECUTE);
						this.forceExtractEmc(actualInserted, EmcAction.EXECUTE);
					});
			} else if(this.hasFuel)
			{
				for(int i = 0; i < suppressed; ++i)
				{
					if(FuelMapper.getFuelUpgrade(upgrading).isEmpty())
					{
						this.auxSlots.setStackInSlot(0, ItemStack.EMPTY);
					}
					
					ItemStack result = this.getLock().isEmpty() ? FuelMapper.getFuelUpgrade(upgrading) : this.getLock().copy();
					long upgradeCost = EMCHelper.getEmcValue(result) - EMCHelper.getEmcValue(upgrading);
					if(upgradeCost >= 0L && this.getStoredEmc() >= upgradeCost)
					{
						ItemStack upgrade = this.getUpgraded();
						if(this.getUpgraded().isEmpty())
						{
							this.forceExtractEmc(upgradeCost, EmcAction.EXECUTE);
							this.auxSlots.setStackInSlot(1, result);
							upgrading.shrink(1);
						} else if(result.getItem() == upgrade.getItem() && upgrade.getCount() < upgrade.getMaxStackSize())
						{
							this.forceExtractEmc(upgradeCost, EmcAction.EXECUTE);
							this.getUpgraded().grow(1);
							upgrading.shrink(1);
							this.auxSlots.onContentsChanged(1);
						}
					}
				}
			} else
			{
				long toSend = this.getStoredEmc() < this.emcGen ? this.getStoredEmc() : this.emcGen;
				this.sendToAllAcceptors(toSend, suppressed);
				this.sendRelayBonus();
			}
		}
		
	}
	
	public long getEmcToNextGoal()
	{
		ItemStack lock = this.getLock();
		ItemStack upgrading = this.getUpgrading();
		long targetEmc;
		if(lock.isEmpty())
		{
			targetEmc = EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(upgrading));
		} else
		{
			targetEmc = EMCHelper.getEmcValue(lock);
		}
		
		return Math.max(targetEmc - EMCHelper.getEmcValue(upgrading), 0L);
	}
	
	public long getItemCharge()
	{
		ItemStack upgrading = this.getUpgrading();
		return !upgrading.isEmpty() ? upgrading.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).map((emcHolder) ->
		{
			return emcHolder.getStoredEmc(upgrading);
		}).orElse(-1L) : -1L;
	}
	
	public double getItemChargeProportion()
	{
		ItemStack upgrading = this.getUpgrading();
		long charge = this.getItemCharge();
		if(!upgrading.isEmpty() && charge > 0L)
		{
			Optional<IItemEmcHolder> emcHolder = upgrading.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
			if(emcHolder.isPresent())
			{
				long max = emcHolder.get().getMaximumEmc(upgrading);
				return charge >= max ? 1.0 : (double) charge / (double) max;
			} else
			{
				return -1.0;
			}
		} else
		{
			return -1.0;
		}
	}
	
	@Range(from = 0, to = Long.MAX_VALUE)
	protected long sendToAllAcceptors(long emc, int suppressed)
	{
		if(level == null || !canProvideEmc())
		{
			//If we cannot provide emc then just return
			return 0;
		}
		emc = Math.min(getEmcExtractLimit() * suppressed, emc);
		long sentEmc = 0;
		List<IEmcStorage> targets = new ArrayList<>();
		for(Direction dir : Direction.values())
		{
			BlockPos neighboringPos = worldPosition.relative(dir);
			//Make sure the neighboring block is loaded as if we are on a chunk border on the edge of loaded chunks this may not be the case
			if(level.isLoaded(neighboringPos))
			{
				BlockEntity neighboringBE = WorldHelper.getBlockEntity(level, neighboringPos);
				if(neighboringBE != null)
				{
					neighboringBE.getCapability(PECapabilities.EMC_STORAGE_CAPABILITY, dir.getOpposite()).ifPresent(theirEmcStorage ->
					{
						if(!isRelay() || !theirEmcStorage.isRelay())
						{
							//If they are both relays don't add the pairing so as to prevent thrashing
							if(theirEmcStorage.insertEmc(1, EmcAction.SIMULATE) > 0)
							{
								//If they would be wiling to accept any Emc then we consider them to be an "acceptor"
								targets.add(theirEmcStorage);
							}
						}
					});
				}
			}
		}
		
		if(!targets.isEmpty())
		{
			long emcPer = emc / targets.size();
			for(IEmcStorage target : targets)
			{
				long emcCanProvide = extractEmc(emcPer, EmcAction.SIMULATE);
				long acceptedEmc = target.insertEmc(emcCanProvide, EmcAction.EXECUTE);
				extractEmc(acceptedEmc, EmcAction.EXECUTE);
				sentEmc += acceptedEmc;
			}
		}
		return sentEmc;
	}
	
	public int getSunLevel()
	{
		return this.level.dimensionType().ultraWarm() ? 16 : this.level.getMaxLocalRawBrightness(this.worldPosition.above()) + 1;
	}
	
	public double getFuelProgress()
	{
		if(!this.getUpgrading().isEmpty() && FuelMapper.isStackFuel(this.getUpgrading()))
		{
			long reqEmc;
			if(!this.getLock().isEmpty())
			{
				reqEmc = EMCHelper.getEmcValue(this.getLock()) - EMCHelper.getEmcValue(this.getUpgrading());
				if(reqEmc < 0L)
				{
					return 0.0;
				}
			} else
			{
				if(FuelMapper.getFuelUpgrade(this.getUpgrading()).isEmpty())
				{
					this.auxSlots.setStackInSlot(0, ItemStack.EMPTY);
					return 0.0;
				}
				
				reqEmc = EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(this.getUpgrading())) - EMCHelper.getEmcValue(this.getUpgrading());
			}
			
			return this.getStoredEmc() >= reqEmc ? 1.0 : (double) this.getStoredEmc() / (double) reqEmc;
		} else
		{
			return 0.0;
		}
	}
	
	@Override
	public void load(@Nonnull CompoundTag nbt)
	{
		super.load(nbt);
		this.input.deserializeNBT(nbt.getCompound("Input"));
		this.auxSlots.deserializeNBT(nbt.getCompound("AuxSlots"));
		this.unprocessedEMC = nbt.getDouble("UnprocessedEMC");
	}
	
	@Override
	protected void saveAdditional(@Nonnull CompoundTag tag)
	{
		super.saveAdditional(tag);
		tag.put("Input", this.input.serializeNBT());
		tag.put("AuxSlots", this.auxSlots.serializeNBT());
		tag.putDouble("UnprocessedEMC", this.unprocessedEMC);
	}
	
	private void sendRelayBonus()
	{
		for(Direction dir : Direction.values())
		{
			var be = this.level.getBlockEntity(this.worldPosition.relative(dir));
			
			RelayMK1BlockEntity relay = Cast.cast(be, RelayMK1BlockEntity.class);
			if(relay != null) relay.addBonus();
			
			TileCustomRelay cr = Cast.cast(be, TileCustomRelay.class);
			if(cr != null) cr.addBonus();
		}
	}
	
	@Override
	public @Nonnull AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player playerIn)
	{
		return new ContainerCustomCollector.Baseline(windowId, playerInventory, this);
	}
	
	@Override
	public @Nonnull Component getDisplayName()
	{
		return getBlockState().getBlock().getName();
	}
	
	private class CollectorItemHandlerProvider
			extends SidedItemHandlerResolver
	{
		private final ICapabilityResolver<IItemHandler> automationAuxSlots;
		private final ICapabilityResolver<IItemHandler> automationInput;
		private final ICapabilityResolver<IItemHandler> joined;
		
		protected CollectorItemHandlerProvider()
		{
			NonNullLazy<IItemHandler> automationInput = NonNullLazy.of(() ->
			{
				return new WrappedItemHandler(TileCustomCollector.this.input, WrappedItemHandler.WriteMode.IN)
				{
					public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
					{
						return SlotPredicates.COLLECTOR_INV.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
					}
				};
			});
			NonNullLazy<IItemHandler> automationAuxSlots = NonNullLazy.of(() ->
			{
				return new WrappedItemHandler(TileCustomCollector.this.auxSlots, WrappedItemHandler.WriteMode.OUT)
				{
					public @Nonnull ItemStack extractItem(int slot, int count, boolean simulate)
					{
						return slot == 1 ? super.extractItem(slot, count, simulate) : ItemStack.EMPTY;
					}
				};
			});
			this.automationInput = BasicCapabilityResolver.getBasicItemHandlerResolver(automationInput);
			this.automationAuxSlots = BasicCapabilityResolver.getBasicItemHandlerResolver(automationAuxSlots);
			this.joined = BasicCapabilityResolver.getBasicItemHandlerResolver(() ->
			{
				return new CombinedInvWrapper((IItemHandlerModifiable) automationInput.get(),
						(IItemHandlerModifiable) automationAuxSlots.get());
			});
		}
		
		@Override
		protected ICapabilityResolver<IItemHandler> getResolver(@Nullable Direction side)
		{
			if(side == null)
			{
				return this.joined;
			} else
			{
				return side.getAxis().isVertical() ? this.automationAuxSlots : this.automationInput;
			}
		}
		
		@Override
		public void invalidateAll()
		{
			this.joined.invalidateAll();
			this.automationInput.invalidateAll();
			this.automationAuxSlots.invalidateAll();
		}
	}
	
	protected class StackHandler
			extends EmcBlockEntity.StackHandler
	{
		protected StackHandler(int size)
		{
			super(size);
		}
		
		@Override
		protected void onContentsChanged(int slot)
		{
			super.onContentsChanged(slot);
		}
	}
}