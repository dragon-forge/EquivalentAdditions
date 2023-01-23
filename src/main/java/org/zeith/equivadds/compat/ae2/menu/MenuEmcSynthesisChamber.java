package org.zeith.equivadds.compat.ae2.menu;

import appeng.api.config.SecurityPermissions;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.OutputSlot;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.equivadds.compat.ae2.client.GuiEmcSynthesisChamber;
import org.zeith.equivadds.compat.ae2.tile.TileEmcSynthesisChamber;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.api.inv.IScreenContainer;

public class MenuEmcSynthesisChamber
		extends UpgradeableMenu<TileEmcSynthesisChamber>
		implements IProgressProvider, IScreenContainer
{
	private static final int MAX_CRAFT_PROGRESS = 100;
	private final TileEmcSynthesisChamber emcSynthesisChamber;
	@GuiSync(4)
	public int craftProgress = 0;
	
	public MenuEmcSynthesisChamber(int id, Inventory playerInv, TileEmcSynthesisChamber be)
	{
		super(ContainerAPI.TILE_CONTAINER, id, playerInv, be);
		this.emcSynthesisChamber = be;
	}
	
	@Override
	protected void setupConfig()
	{
		var mac = this.getHost().getSubInventory(TileEmcSynthesisChamber.INV_MAIN);
		
		this.addSlot(new OutputSlot(mac, 9, null), SlotSemantics.MACHINE_OUTPUT);
	}
	
	@Override
	public void broadcastChanges()
	{
		this.verifyPermissions(SecurityPermissions.BUILD, false);
		
		this.craftProgress = this.emcSynthesisChamber.getCraftingProgress();
		
		this.standardDetectAndSendChanges();
	}
	
	@Override
	public int getCurrentProgress()
	{
		return this.craftProgress;
	}
	
	@Override
	public int getMaxProgress()
	{
		return MAX_CRAFT_PROGRESS;
	}
	
	@Override
	public void onSlotChange(Slot s)
	{
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		try
		{
			return new GuiEmcSynthesisChamber(this, inv, label);
		} catch(Throwable e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
