package org.zeith.equivadds.container;

import moze_intel.projecte.gameObjs.block_entities.CollectorMK3BlockEntity;
import moze_intel.projecte.gameObjs.container.PEContainer;
import moze_intel.projecte.gameObjs.container.slots.*;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.zeith.equivadds.init.ContainerTypesEA;
import org.zeith.equivadds.tiles.collectors.TileCustomCollector;

public class ContainerCustomCollector
		extends PEContainer
{
	public final TileCustomCollector collector;
	public final DataSlot sunLevel;
	public final PEContainer.BoxedLong emc;
	private final DataSlot kleinChargeProgress;
	private final DataSlot fuelProgress;
	public final PEContainer.BoxedLong kleinEmc;
	
	public final ContainerLevelAccess levelAccess;
	
	protected ContainerCustomCollector(ContainerTypeRegistryObject<? extends ContainerCustomCollector> type, int windowId, Inventory playerInv, TileCustomCollector collector)
	{
		super(type, windowId, playerInv);
		this.levelAccess = ContainerLevelAccess.create(collector.getLevel(), collector.getBlockPos());
		this.sunLevel = DataSlot.standalone();
		this.emc = new PEContainer.BoxedLong();
		this.kleinChargeProgress = DataSlot.standalone();
		this.fuelProgress = DataSlot.standalone();
		this.kleinEmc = new PEContainer.BoxedLong();
		this.longFields.add(this.emc);
		this.addDataSlot(this.sunLevel);
		this.addDataSlot(this.kleinChargeProgress);
		this.addDataSlot(this.fuelProgress);
		this.longFields.add(this.kleinEmc);
		this.collector = collector;
		this.initSlots();
	}
	
	void initSlots()
	{
		IItemHandler aux = this.collector.getAux();
		IItemHandler main = this.collector.getInput();
		this.addSlot(new ValidatedSlot(aux, 0, 124, 58, SlotPredicates.COLLECTOR_INV));
		int counter = 0;
		
		for(int i = 1; i >= 0; --i)
		{
			for(int j = 3; j >= 0; --j)
			{
				this.addSlot(new ValidatedSlot(main, counter++, 20 + i * 18, 8 + j * 18, SlotPredicates.COLLECTOR_INV));
			}
		}
		
		this.addSlot(new ValidatedSlot(aux, 1, 124, 13, SlotPredicates.ALWAYS_FALSE));
		this.addSlot(new SlotGhost(aux, 2, 153, 36, SlotPredicates.COLLECTOR_LOCK));
		
		this.addPlayerInventory(8, 84);
	}
	
	@Override
	public void clicked(int slotID, int button, @NotNull ClickType flag, @NotNull Player player)
	{
		Slot slot = this.tryGetSlot(slotID);
		if(slot instanceof SlotGhost && !slot.getItem().isEmpty())
		{
			slot.set(ItemStack.EMPTY);
		} else
		{
			super.clicked(slotID, button, flag, player);
		}
		
	}
	
	@Override
	protected void broadcastPE(boolean all)
	{
		this.emc.set(this.collector.getStoredEmc());
		this.sunLevel.set(this.collector.getSunLevel());
		this.kleinChargeProgress.set((int) (this.collector.getItemChargeProportion() * 8000.0));
		this.fuelProgress.set((int) (this.collector.getFuelProgress() * 8000.0));
		this.kleinEmc.set(this.collector.getItemCharge());
		super.broadcastPE(all);
	}
	
	@Override
	public boolean stillValid(@NotNull Player player)
	{
		return stillValid(levelAccess, player, this.collector.getBlockState().getBlock());
	}
	
	public double getKleinChargeProgress()
	{
		return (double) this.kleinChargeProgress.get() / 8000.0;
	}
	
	public double getFuelProgress()
	{
		return (double) this.fuelProgress.get() / 8000.0;
	}
	
	public static class Baseline
			extends ContainerCustomCollector
	{
		
		public Baseline(int windowId, Inventory playerInv, TileCustomCollector collector)
		{
			super(ContainerTypesEA.COLLECTOR_CONTAINER, windowId, playerInv, collector);
		}
		
		@Override
		void initSlots()
		{
			IItemHandler aux = collector.getAux();
			IItemHandler main = collector.getInput();
			
			//Klein Star Slot
			this.addSlot(new ValidatedSlot(aux, CollectorMK3BlockEntity.UPGRADING_SLOT, 158, 58, SlotPredicates.COLLECTOR_INV));
			int counter = 0;
			//Fuel Upgrade Slot
			for(int i = 3; i >= 0; i--)
			{
				for(int j = 3; j >= 0; j--)
				{
					this.addSlot(new ValidatedSlot(main, counter++, 18 + i * 18, 8 + j * 18, SlotPredicates.COLLECTOR_INV));
				}
			}
			//Upgrade Result
			this.addSlot(new ValidatedSlot(aux, CollectorMK3BlockEntity.UPGRADE_SLOT, 158, 13, SlotPredicates.ALWAYS_FALSE));
			//Upgrade Target
			this.addSlot(new SlotGhost(aux, CollectorMK3BlockEntity.LOCK_SLOT, 187, 36, SlotPredicates.COLLECTOR_LOCK));
			addPlayerInventory(30, 84);
		}
	}
}