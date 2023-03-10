package org.zeith.equivadds.tiles.relays;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.capability.managing.*;
import moze_intel.projecte.gameObjs.block_entities.*;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.EMCHelper;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.items.*;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.equivadds.api.IHasEmcPriority;
import org.zeith.equivadds.blocks.conduit.TileEmcConduit;
import org.zeith.equivadds.container.ContainerCustomRelay;
import org.zeith.equivadds.init.EnumRelayTiersEA;

import java.util.Optional;

public class TileCustomRelay
		extends CapabilityEmcBlockEntity
		implements MenuProvider, IHasEmcPriority
{
	private final CompactableStackHandler input;
	private final ItemStackHandler output = new StackHandler(1);
	private final long chargeRate;
	private double bonusEMC;
	
	public TileCustomRelay(EnumRelayTiersEA tier, BlockPos pos, BlockState state, int sizeInv)
	{
		super(tier.getTileType(), pos, state, tier.getStorage());
		this.chargeRate = tier.getChargeRate();
		input = new CompactableStackHandler(sizeInv)
		{
			@NotNull
			@Override
			public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
			{
				return SlotPredicates.RELAY_INV.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
			}
		};
		itemHandlerResolver = new RelayItemHandlerProvider();
	}
	
	@Override
	public boolean isRelay()
	{
		return true;
	}
	
	private ItemStack getCharging()
	{
		return output.getStackInSlot(0);
	}
	
	private ItemStack getBurn()
	{
		return input.getStackInSlot(0);
	}
	
	public IItemHandler getInput()
	{
		return input;
	}
	
	public IItemHandler getOutput()
	{
		return output;
	}
	
	@Override
	protected boolean emcAffectsComparators()
	{
		return true;
	}
	
	public int getExtraBurnTimes()
	{
		return 0;
	}
	
	public static void tickServer(Level level, BlockPos pos, BlockState state, TileCustomRelay relay)
	{
		relay.sendEmc();
		relay.input.compact();
		
		int burnIns = 1 + relay.getExtraBurnTimes();
		for(int i = 0; i < burnIns; ++i)
		{
			ItemStack stack = relay.getBurn();
			
			if(!stack.isEmpty())
			{
				Optional<IItemEmcHolder> holderCapability = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
				if(holderCapability.isPresent())
				{
					IItemEmcHolder emcHolder = holderCapability.get();
					long simulatedVal = relay.forceInsertEmc(emcHolder.extractEmc(stack, relay.chargeRate, EmcAction.SIMULATE), EmcAction.SIMULATE);
					if(simulatedVal > 0)
						relay.forceInsertEmc(emcHolder.extractEmc(stack, simulatedVal, EmcAction.EXECUTE), EmcAction.EXECUTE);
				} else
				{
					long emcVal = EMCHelper.getEmcSellValue(stack);
					if(emcVal > 0 && emcVal <= relay.getNeededEmc())
					{
						relay.forceInsertEmc(emcVal, EmcAction.EXECUTE);
						relay.getBurn().shrink(1);
						relay.input.onContentsChanged(0);
					}
				}
			}
		}
		
		ItemStack chargeable = relay.getCharging();
		if(!chargeable.isEmpty() && relay.getStoredEmc() > 0)
		{
			chargeable.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).ifPresent(emcHolder ->
			{
				long actualSent = emcHolder.insertEmc(chargeable, Math.min(relay.getStoredEmc(), relay.chargeRate), EmcAction.EXECUTE);
				relay.forceExtractEmc(actualSent, EmcAction.EXECUTE);
			});
		}
		relay.updateComparators();
	}
	
	private void sendEmc()
	{
		if(this.getStoredEmc() == 0)
		{
			return;
		}
		if(this.getStoredEmc() <= chargeRate)
		{
			this.sendToAllAcceptors(this.getStoredEmc());
		} else
		{
			this.sendToAllAcceptors(chargeRate);
		}
	}
	
	public double getItemChargeProportion()
	{
		ItemStack charging = getCharging();
		if(!charging.isEmpty())
		{
			Optional<IItemEmcHolder> holderCapability = charging.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
			if(holderCapability.isPresent())
			{
				IItemEmcHolder emcHolder = holderCapability.get();
				return (double) emcHolder.getStoredEmc(charging) / emcHolder.getMaximumEmc(charging);
			}
		}
		return 0;
	}
	
	public double getInputBurnProportion()
	{
		ItemStack burn = getBurn();
		if(burn.isEmpty())
		{
			return 0;
		}
		Optional<IItemEmcHolder> holderCapability = burn.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
		if(holderCapability.isPresent())
		{
			IItemEmcHolder emcHolder = holderCapability.get();
			return (double) emcHolder.getStoredEmc(burn) / emcHolder.getMaximumEmc(burn);
		}
		return burn.getCount() / (double) burn.getMaxStackSize();
	}
	
	@Override
	public void load(@NotNull CompoundTag nbt)
	{
		super.load(nbt);
		input.deserializeNBT(nbt.getCompound("Input"));
		output.deserializeNBT(nbt.getCompound("Output"));
		bonusEMC = nbt.getDouble("BonusEMC");
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		tag.put("Input", input.serializeNBT());
		tag.put("Output", output.serializeNBT());
		tag.putDouble("BonusEMC", bonusEMC);
	}
	
	protected double getBonusToAdd()
	{
		return 0.05;
	}
	
	public void addBonus()
	{
		bonusEMC += getBonusToAdd();
		if(bonusEMC >= 1)
		{
			long emcToInsert = (long) bonusEMC;
			forceInsertEmc(emcToInsert, EmcAction.EXECUTE);
			//Don't subtract the actual amount we managed to insert so that we do not continue to grow to
			// an infinite amount of "bonus" me if our buffer is full.
			bonusEMC -= emcToInsert;
		}
		markDirty(false);
	}
	
	@NotNull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player)
	{
		return new ContainerCustomRelay.Baseline(windowId, playerInventory, this);
	}
	
	@NotNull
	@Override
	public Component getDisplayName()
	{
		return getBlockState().getBlock().getName();
	}
	
	@Override
	public int getPriority(Direction from, TileEmcConduit conduit)
	{
		return 1;
	}
	
	private class RelayItemHandlerProvider
			extends SidedItemHandlerResolver
	{
		
		private final ICapabilityResolver<IItemHandler> automationOutput;
		private final ICapabilityResolver<IItemHandler> automationInput;
		private final ICapabilityResolver<IItemHandler> joined;
		
		protected RelayItemHandlerProvider()
		{
			NonNullLazy<IItemHandler> automationInput = NonNullLazy.of(() -> new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN));
			NonNullLazy<IItemHandler> automationOutput = NonNullLazy.of(() -> new WrappedItemHandler(output, WrappedItemHandler.WriteMode.IN_OUT)
			{
				@NotNull
				@Override
				public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
				{
					return SlotPredicates.EMC_HOLDER.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
				}
				
				@NotNull
				@Override
				public ItemStack extractItem(int slot, int amount, boolean simulate)
				{
					ItemStack stack = getStackInSlot(slot);
					if(!stack.isEmpty())
					{
						Optional<IItemEmcHolder> holderCapability = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
						if(holderCapability.isPresent())
						{
							IItemEmcHolder emcHolder = holderCapability.get();
							if(emcHolder.getNeededEmc(stack) == 0)
							{
								return super.extractItem(slot, amount, simulate);
							}
							return ItemStack.EMPTY;
						}
					}
					return super.extractItem(slot, amount, simulate);
				}
			});
			this.automationInput = BasicCapabilityResolver.getBasicItemHandlerResolver(automationInput);
			this.automationOutput = BasicCapabilityResolver.getBasicItemHandlerResolver(automationOutput);
			this.joined = BasicCapabilityResolver.getBasicItemHandlerResolver(() -> new CombinedInvWrapper((IItemHandlerModifiable) automationInput.get(),
					(IItemHandlerModifiable) automationOutput.get()));
		}
		
		@Override
		protected ICapabilityResolver<IItemHandler> getResolver(@Nullable Direction side)
		{
			if(side == null)
			{
				return joined;
			} else if(side.getAxis().isVertical())
			{
				return automationOutput;
			}
			return automationInput;
		}
		
		@Override
		public void invalidateAll()
		{
			joined.invalidateAll();
			automationInput.invalidateAll();
			automationOutput.invalidateAll();
		}
	}
	
	protected class CompactableStackHandler
			extends EmcBlockEntity.CompactableStackHandler
	{
		
		protected CompactableStackHandler(int size)
		{
			super(size);
		}
		
		@Override
		protected void onContentsChanged(int slot)
		{
			super.onContentsChanged(slot);
		}
	}
	
	protected class StackHandler
			extends ItemStackHandler
	{
		protected StackHandler(int size)
		{
			super(size);
		}
		
		@Override
		protected void onContentsChanged(int slot)
		{
			super.onContentsChanged(slot);
			setChanged();
		}
	}
}