package org.zeith.equivadds.compat.ae2.client.gui;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.equivadds.EquivalentAdditions;
import org.zeith.equivadds.compat.ae2.container.ContainerEmcSynthesisChamber;
import org.zeith.equivadds.compat.ae2.util.StyleManagerEA;

public class GuiEmcSynthesisChamber
		extends UpgradeableScreen<ContainerEmcSynthesisChamber>
{
	private final ProgressBar pb;
	
	public GuiEmcSynthesisChamber(ContainerEmcSynthesisChamber menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title, style());
		
		this.pb = new ProgressBar(this.menu, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL);
		widgets.add("progressBar", this.pb);
	}
	
	@Override
	protected void updateBeforeRender()
	{
		super.updateBeforeRender();
		
		this.pb.setFullMsg(Component.literal(this.menu.getCurrentProgress() + "%"));
	}
	
	public static ScreenStyle style()
	{
		return StyleManagerEA.loadStyleDoc(EquivalentAdditions.id("ae2_screens/emc_synthesis_chamber.json"));
	}
}