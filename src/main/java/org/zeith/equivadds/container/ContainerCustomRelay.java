package org.zeith.equivadds.container;

import moze_intel.projecte.gameObjs.container.PEContainer;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.zeith.equivadds.init.ContainerTypesEA;
import org.zeith.equivadds.tiles.relays.TileCustomRelay;

public class ContainerCustomRelay
		extends PEContainer
{
	public final TileCustomRelay relay;
	private final DataSlot kleinChargeProgress = DataSlot.standalone();
	private final DataSlot inputBurnProgress = DataSlot.standalone();
	public final BoxedLong emc = new BoxedLong();
	
	public final ContainerLevelAccess levelAccess;
	
	protected ContainerCustomRelay(ContainerTypeRegistryObject<? extends ContainerCustomRelay> type, int windowId, Inventory playerInv, TileCustomRelay relay)
	{
		super(type, windowId, playerInv);
		this.levelAccess = ContainerLevelAccess.create(relay.getLevel(), relay.getBlockPos());
		this.longFields.add(emc);
		addDataSlot(kleinChargeProgress);
		addDataSlot(inputBurnProgress);
		this.relay = relay;
		initSlots();
	}
	
	void initSlots()
	{
		IItemHandler input = relay.getInput();
		IItemHandler output = relay.getOutput();
		//Klein Star charge slot
		this.addSlot(new ValidatedSlot(output, 0, 127, 43, SlotPredicates.EMC_HOLDER));
		//Burning slot
		this.addSlot(new ValidatedSlot(input, 0, 67, 43, SlotPredicates.RELAY_INV));
		int counter = 1;
		//Main Relay inventory
		for(int i = 1; i >= 0; i--)
		{
			for(int j = 2; j >= 0; j--)
			{
				this.addSlot(new ValidatedSlot(input, counter++, 27 + i * 18, 17 + j * 18, SlotPredicates.RELAY_INV));
			}
		}
		addPlayerInventory(8, 95);
	}
	
	@Override
	protected void broadcastPE(boolean all)
	{
		emc.set(relay.getStoredEmc());
		kleinChargeProgress.set((int) (relay.getItemChargeProportion() * 8000));
		inputBurnProgress.set((int) (relay.getInputBurnProportion() * 8000));
		super.broadcastPE(all);
	}
	
	@Override
	public boolean stillValid(@NotNull Player player)
	{
		return stillValid(levelAccess, player, this.relay.getBlockState().getBlock());
	}
	
	public double getKleinChargeProgress()
	{
		return kleinChargeProgress.get() / 8000.0;
	}
	
	public double getInputBurnProgress()
	{
		return inputBurnProgress.get() / 8000.0;
	}
	
	public static class Baseline
			extends ContainerCustomRelay
	{
		public Baseline(int windowId, Inventory playerInv, TileCustomRelay relay)
		{
			super(ContainerTypesEA.RELAY_CONTAINER, windowId, playerInv, relay);
		}
		
		@Override
		void initSlots()
		{
			IItemHandler input = relay.getInput();
			IItemHandler output = relay.getOutput();
			//Klein star charge
			this.addSlot(new ValidatedSlot(output, 0, 164, 58, SlotPredicates.EMC_HOLDER));
			//Burn slot
			this.addSlot(new ValidatedSlot(input, 0, 104, 58, SlotPredicates.RELAY_INV));
			int counter = 1;
			//Inventory buffer
			for(int i = 3; i >= 0; i--)
			{
				for(int j = 4; j >= 0; j--)
				{
					this.addSlot(new ValidatedSlot(input, counter++, 28 + i * 18, 18 + j * 18, SlotPredicates.RELAY_INV));
				}
			}
			addPlayerInventory(26, 113);
		}
	}
}