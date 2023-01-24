package org.zeith.equivadds.compat.ae2.tile;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.ticking.*;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.upgrades.*;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkInvBlockEntity;
import appeng.capabilities.Capabilities;
import appeng.client.render.crafting.AssemblerAnimationStatus;
import appeng.core.AELog;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.core.localization.Tooltips;
import appeng.util.inv.*;
import appeng.util.inv.filter.IAEItemFilter;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.zeith.equivadds.EquivalentAdditions;
import org.zeith.equivadds.compat.ae2.crafting.pattern.AEEmcSynthesisPattern;
import org.zeith.equivadds.compat.ae2.init.BlocksEAAE2;
import org.zeith.equivadds.compat.ae2.init.ItemsEAAE2;
import org.zeith.equivadds.compat.ae2.me.EMCKey;
import org.zeith.equivadds.compat.ae2.net.PacketAssemblerAnimation;
import org.zeith.equivadds.util.EMCStorage;
import org.zeith.hammerlib.net.HLTargetPoint;
import org.zeith.hammerlib.net.Network;

import javax.annotation.Nullable;
import java.util.List;

public class TileEmcSynthesisChamber
		extends AENetworkInvBlockEntity
		implements IUpgradeableObject, IGridTickable, ICraftingMachine, IPowerChannelState
{
	public static final ResourceLocation INV_MAIN = EquivalentAdditions.id("emc_synthesis_chamber");
	
	private final AppEngInternalInventory gridInv = new AppEngInternalInventory(this, 1, 1);
	private final InternalInventory internalInv = new CombinedInternalInventory(this.gridInv);
	private final EMCStorage emcInv = new EMCStorage();
	private final InternalInventory gridInvExt = new FilteredInternalInventory(this.gridInv, new CraftingGridFilter());
	
	private final IUpgradeInventory upgrades;
	private boolean isPowered = false;
	private Direction pushDirection = null;
	private ItemStack myPattern = ItemStack.EMPTY;
	private AEEmcSynthesisPattern myPlan = null;
	private double progress = 0;
	private boolean isAwake = false;
	private boolean forcePlan = false;
	private boolean reboot = true;
	
	@OnlyIn(Dist.CLIENT)
	private AssemblerAnimationStatus animationStatus;
	
	public TileEmcSynthesisChamber(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
	{
		super(type, pos, blockState);
		
		this.getMainNode()
				.setIdlePowerUsage(0.0)
				.addService(IGridTickable.class, this);
		
		this.upgrades = UpgradeInventories.forMachine(BlocksEAAE2.EMC_SYNTHESIS_CHAMBER, getUpgradeSlots(),
				this::saveChanges);
	}
	
	private int getUpgradeSlots()
	{
		return 5;
	}
	
	@Nullable
	@Override
	public PatternContainerGroup getCraftingMachineInfo()
	{
		Component name;
		if(hasCustomInventoryName())
		{
			name = getCustomInventoryName();
		} else
		{
			name = BlocksEAAE2.EMC_SYNTHESIS_CHAMBER.asItem().getDescription();
		}
		var icon = AEItemKey.of(BlocksEAAE2.EMC_SYNTHESIS_CHAMBER);
		
		// List installed upgrades as the tooltip to differentiate assemblers by upgrade count
		List<Component> tooltip;
		var accelerationCards = getInstalledUpgrades(AEItems.SPEED_CARD);
		if(accelerationCards == 0)
		{
			tooltip = List.of();
		} else
		{
			tooltip = List.of(
					GuiText.CompatibleUpgrade.text(
							Tooltips.of(AEItems.SPEED_CARD.asItem().getDescription()),
							Tooltips.ofUnformattedNumber(accelerationCards)));
		}
		
		return new PatternContainerGroup(icon, name, tooltip);
	}
	
	@Override
	public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] table,
							   Direction where)
	{
		if(this.myPattern.isEmpty())
		{
			// Only accept our own crafting patterns!
			if(patternDetails instanceof AEEmcSynthesisPattern pattern)
			{
				// We only support fluid and item stacks
				
				this.forcePlan = true;
				this.myPlan = pattern;
				this.pushDirection = where;
				
				this.fillGrid(table, pattern);
				
				this.updateSleepiness();
				this.saveChanges();
				return true;
			}
		}
		return false;
	}
	
	private void fillGrid(KeyCounter[] table, AEEmcSynthesisPattern adapter)
	{
		emcInv.insertEmc(Math.min(adapter.getEmc(), table[0].get(EMCKey.KEY)), IEmcStorage.EmcAction.EXECUTE);
	}
	
	private void updateSleepiness()
	{
		final boolean wasEnabled = this.isAwake;
		this.isAwake = this.myPlan != null && this.hasMats() || this.canPush();
		if(wasEnabled != this.isAwake)
		{
			getMainNode().ifPresent((grid, node) ->
			{
				if(this.isAwake)
				{
					grid.getTickManager().wakeDevice(node);
				} else
				{
					grid.getTickManager().sleepDevice(node);
				}
			});
		}
	}
	
	private boolean canPush()
	{
		return !this.gridInv.getStackInSlot(0).isEmpty();
	}
	
	private boolean hasMats()
	{
		if(this.myPlan == null) return false;
		return emcInv.getStoredEmc() >= myPlan.getEmc();
	}
	
	@Override
	public boolean acceptsPlans()
	{
		return myPlan == null;
	}
	
	@Override
	protected boolean readFromStream(FriendlyByteBuf data)
	{
		final boolean c = super.readFromStream(data);
		final boolean oldPower = this.isPowered;
		this.isPowered = data.readBoolean();
		return this.isPowered != oldPower || c;
	}
	
	@Override
	protected void writeToStream(FriendlyByteBuf data)
	{
		super.writeToStream(data);
		data.writeBoolean(this.isPowered);
	}
	
	@Override
	public void saveAdditional(CompoundTag data)
	{
		super.saveAdditional(data);
		if(this.forcePlan)
		{
			// If the plan is null it means the pattern previously loaded from NBT hasn't been decoded yet
			var pattern = myPlan != null ? myPlan.getDefinition().toStack() : myPattern;
			if(!pattern.isEmpty())
			{
				var compound = new CompoundTag();
				pattern.save(compound);
				data.put("myPlan", compound);
				data.putInt("pushDirection", this.pushDirection.ordinal());
			}
		}
		
		this.upgrades.writeToNBT(data, "upgrades");
		data.put("emc", emcInv.serializeNBT());
	}
	
	@Override
	public void loadTag(CompoundTag data)
	{
		super.loadTag(data);
		
		// Reset current state back to defaults
		this.forcePlan = false;
		this.myPattern = ItemStack.EMPTY;
		this.myPlan = null;
		
		if(data.contains("myPlan"))
		{
			var pattern = ItemStack.of(data.getCompound("myPlan"));
			if(!pattern.isEmpty())
			{
				this.forcePlan = true;
				this.myPattern = pattern;
				this.myPlan = ItemsEAAE2.EMC_SYNTHESIS_PATTERN.decode(pattern, level, true);
				this.pushDirection = Direction.values()[data.getInt("pushDirection")];
			}
		}
		
		this.upgrades.readFromNBT(data, "upgrades");
		emcInv.deserializeNBT(data.getCompound("emc"));
		
		this.recalculatePlan();
	}
	
	private void recalculatePlan()
	{
		this.reboot = true;
		
		if(this.forcePlan)
		{
			// If we're in forced mode, and myPattern is not empty, but the plan is null,
			// this indicates that we received an encoded pattern from NBT data, but
			// didn't have a chance to decode it yet
			if(getLevel() != null && myPlan == null)
			{
				if(!myPattern.isEmpty())
				{
					if(PatternDetailsHelper.decodePattern(myPattern, getLevel(),
							false) instanceof AEEmcSynthesisPattern supportedPlan)
					{
						this.myPlan = supportedPlan;
					}
				}
				
				// Reset myPattern, so it will accept another job once this one finishes
				this.myPattern = ItemStack.EMPTY;
				
				// If the plan is still null, reset back to non-forced mode
				if(myPlan == null)
				{
					AELog.warn("Unable to restore auto-crafting pattern after load: %s", myPattern.getTag());
					this.forcePlan = false;
				}
			}
			
			return;
		}
		
		this.updateSleepiness();
	}
	
	@Override
	public AECableType getCableConnectionType(Direction dir)
	{
		return AECableType.COVERED;
	}
	
	@Override
	public InternalInventory getSubInventory(ResourceLocation id)
	{
		if(id.equals(ISegmentedInventory.UPGRADES))
		{
			return this.upgrades;
		} else if(id.equals(INV_MAIN))
		{
			return this.internalInv;
		}
		
		return super.getSubInventory(id);
	}
	
	@Override
	public InternalInventory getInternalInventory()
	{
		return this.internalInv;
	}
	
	@Override
	protected InternalInventory getExposedInventoryForSide(Direction side)
	{
		return this.gridInvExt;
	}
	
	@Override
	public void onChangeInventory(InternalInventory inv, int slot)
	{
		if(inv == this.gridInv)
		{
			this.recalculatePlan();
		}
	}
	
	public int getCraftingProgress()
	{
		return (int) this.progress;
	}
	
	@Override
	public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops)
	{
		super.addAdditionalDrops(level, pos, drops);
		
		for(var upgrade : upgrades)
		{
			drops.add(upgrade);
		}
	}
	
	@Override
	public TickingRequest getTickingRequest(IGridNode node)
	{
		this.recalculatePlan();
		this.updateSleepiness();
		return new TickingRequest(1, 1, !this.isAwake, false);
	}
	
	@Override
	public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall)
	{
		if(!this.gridInv.getStackInSlot(0).isEmpty())
		{
			this.pushOut(this.gridInv.getStackInSlot(0));
			
			// did it eject?
			if(this.gridInv.getStackInSlot(0).isEmpty())
			{
				this.saveChanges();
			}
			
			this.updateSleepiness();
			this.progress = 0;
			return this.isAwake ? TickRateModulation.IDLE : TickRateModulation.SLEEP;
		}
		
		if(this.myPlan == null)
		{
			this.updateSleepiness();
			return TickRateModulation.SLEEP;
		}
		
		if(this.reboot)
		{
			ticksSinceLastCall = 1;
		}
		
		if(!this.isAwake)
		{
			return TickRateModulation.SLEEP;
		}
		
		this.reboot = false;
		int speed = 10;
		switch(this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD))
		{
			case 0 -> this.progress += this.userPower(ticksSinceLastCall, speed = 10, 1.0);
			case 1 -> this.progress += this.userPower(ticksSinceLastCall, speed = 13, 1.3);
			case 2 -> this.progress += this.userPower(ticksSinceLastCall, speed = 17, 1.7);
			case 3 -> this.progress += this.userPower(ticksSinceLastCall, speed = 20, 2.0);
			case 4 -> this.progress += this.userPower(ticksSinceLastCall, speed = 25, 2.5);
			case 5 -> this.progress += this.userPower(ticksSinceLastCall, speed = 50, 5.0);
		}
		
		if(this.progress >= 100)
		{
			emcInv.extractEmc(myPlan.getEmc(), IEmcStorage.EmcAction.EXECUTE);
			
			this.progress = 0;
			final ItemStack output = this.myPlan.assemble(this.getLevel());
			if(!output.isEmpty())
			{
				this.pushOut(output.copy());
				
				if(emcInv.getStoredEmc() < this.myPlan.getEmc())
				{
					this.forcePlan = false;
					this.myPlan = null;
					this.pushDirection = null;
				}
				
				var item = AEItemKey.of(output);
				if(item != null)
					Network.sendToArea(new HLTargetPoint(worldPosition, 32, level), new PacketAssemblerAnimation(this.worldPosition, (byte) speed, item));
				
				this.saveChanges();
				this.updateSleepiness();
				return this.isAwake ? TickRateModulation.IDLE : TickRateModulation.SLEEP;
			}
		}
		
		return TickRateModulation.FASTER;
	}
	
	private int userPower(int ticksPassed, int bonusValue, double acceleratorTax)
	{
		var grid = getMainNode().getGrid();
		if(grid != null)
		{
			return (int) (grid.getEnergyService().extractAEPower(ticksPassed * bonusValue * acceleratorTax,
					Actionable.MODULATE, PowerMultiplier.CONFIG) / acceleratorTax);
		} else
		{
			return 0;
		}
	}
	
	private void pushOut(ItemStack output)
	{
		if(this.pushDirection == null)
		{
			for(Direction d : Direction.values())
			{
				output = this.pushTo(output, d);
			}
		} else
		{
			output = this.pushTo(output, this.pushDirection);
		}
		
		if(output.isEmpty() && this.forcePlan)
		{
			this.forcePlan = false;
			this.recalculatePlan();
		}
		
		this.gridInv.setItemDirect(0, output);
	}
	
	private ItemStack pushTo(ItemStack output, Direction d)
	{
		if(output.isEmpty())
		{
			return output;
		}
		
		final BlockEntity te = this.getLevel().getBlockEntity(this.worldPosition.relative(d));
		
		if(te == null)
		{
			return output;
		}
		
		var adaptor = InternalInventory.wrapExternal(te, d.getOpposite());
		if(adaptor == null)
		{
			return output;
		}
		
		final int size = output.getCount();
		output = adaptor.addItems(output);
		final int newSize = output.isEmpty() ? 0 : output.getCount();
		
		if(size != newSize)
		{
			this.saveChanges();
		}
		
		return output;
	}
	
	@Override
	public void onMainNodeStateChanged(IGridNodeListener.State reason)
	{
		if(reason != IGridNodeListener.State.GRID_BOOT)
		{
			boolean newState = false;
			
			var grid = getMainNode().getGrid();
			if(grid != null)
			{
				newState = this.getMainNode().isPowered() && grid.getEnergyService().extractAEPower(1,
						Actionable.SIMULATE, PowerMultiplier.CONFIG) > 0.0001;
			}
			
			if(newState != this.isPowered)
			{
				this.isPowered = newState;
				this.markForUpdate();
			}
		}
	}
	
	@Override
	public boolean isPowered()
	{
		return this.isPowered;
	}
	
	@Override
	public boolean isActive()
	{
		return this.isPowered;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void setAnimationStatus(@Nullable AssemblerAnimationStatus status)
	{
		this.animationStatus = status;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Nullable
	public AssemblerAnimationStatus getAnimationStatus()
	{
		return this.animationStatus;
	}
	
	@Override
	public IUpgradeInventory getUpgrades()
	{
		return upgrades;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing)
	{
		if(Capabilities.CRAFTING_MACHINE == capability)
		{
			return Capabilities.CRAFTING_MACHINE.orEmpty(capability, LazyOptional.of(() -> this));
		}
		
		return super.getCapability(capability, facing);
	}
	
	@Nullable
	public AEEmcSynthesisPattern getCurrentPattern()
	{
		if(isClientSide())
		{
			return null;
		} else
		{
			return myPlan;
		}
	}
	
	private static class CraftingGridFilter
			implements IAEItemFilter
	{
		@Override
		public boolean allowExtract(InternalInventory inv, int slot, int amount)
		{
			return slot == 0;
		}
		
		@Override
		public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack)
		{
			return false;
		}
	}
}