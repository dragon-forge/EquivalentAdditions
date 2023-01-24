package org.zeith.equivadds.compat.ae2.client.gui;

import appeng.core.localization.ButtonToolTips;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.zeith.equivadds.EquivalentAdditions;
import org.zeith.equivadds.compat.ae2.container.ContainerEmcPatternEncoder;
import org.zeith.equivadds.util.ButtonHelper;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;

public class GuiEmcPatternEncoder
		extends ScreenWTFMojang<ContainerEmcPatternEncoder>
{
	protected ResourceLocation texture = EquivalentAdditions.id("textures/gui/ae2/emc_pattern_encoder.png");
	
	public GuiEmcPatternEncoder(ContainerEmcPatternEncoder container, Inventory playerInv, Component name)
	{
		super(container, playerInv, name);
		imageWidth = 176;
		imageHeight = 197;
		
		inventoryLabelY = this.imageHeight - 94;
	}
	
	Button encodeBtn;
	
	@Override
	protected void init()
	{
		super.init();
		
		encodeBtn = addRenderableWidget(new ImageButton(leftPos + 116, topPos + 43, 16, 16,
				176, 0, 16, texture, 256, 256,
				this::encode, ButtonHelper.tooltip(this, Component.translatable(ButtonToolTips.EncodeDescription.getTranslationKey())), Component.translatable(ButtonToolTips.Encode.getTranslationKey())
		));
		
		encodeBtn.active = menu.tile.encode(IFluidHandler.FluidAction.SIMULATE);
	}
	
	@Override
	protected void containerTick()
	{
		super.containerTick();
		encodeBtn.active = menu.tile.encode(IFluidHandler.FluidAction.SIMULATE);
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(texture);
		RenderUtils.drawTexturedModalRect(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
	
	public void encode(Button button)
	{
		clickMenuButton(1);
	}
}