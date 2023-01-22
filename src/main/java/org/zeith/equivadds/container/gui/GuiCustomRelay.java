package org.zeith.equivadds.container.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.gui.PEContainerScreen;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.zeith.equivadds.container.ContainerCustomRelay;

public class GuiCustomRelay<CONTAINER extends ContainerCustomRelay>
		extends PEContainerScreen<CONTAINER>
{
	private final ResourceLocation texture;
	private final int emcX;
	private final int emcY;
	private final int vOffset;
	private final int emcBarShift;
	private final int shiftX;
	private final int shiftY;
	
	protected GuiCustomRelay(CONTAINER container, Inventory invPlayer, Component title, ResourceLocation texture, int emcX, int emcY, int vOffset,
							 int emcBarShift, int shiftX, int shiftY)
	{
		super(container, invPlayer, title);
		this.texture = texture;
		this.emcX = emcX;
		this.emcY = emcY;
		this.vOffset = vOffset;
		this.emcBarShift = emcBarShift;
		this.shiftX = shiftX;
		this.shiftY = shiftY;
	}
	
	@Override
	protected void renderLabels(@NotNull PoseStack matrix, int x, int y)
	{
		this.font.draw(matrix, title, titleLabelX, titleLabelY, 0x404040);
		//Don't render inventory as we don't have space
		this.font.draw(matrix, Constants.EMC_FORMATTER.format(menu.emc.get()), emcX, emcY, 0x404040);
	}
	
	@Override
	protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int x, int y)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, this.texture);
		
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		//Emc bar progress
		int progress = (int) ((double) menu.emc.get() / menu.relay.getMaximumEmc() * Constants.MAX_CONDENSER_PROGRESS);
		blit(matrix, leftPos + emcBarShift, topPos + 6, 30, vOffset, progress, 10);
		
		//Klein start bar progress. Max is 30.
		progress = (int) (menu.getKleinChargeProgress() * 30);
		blit(matrix, leftPos + 116 + shiftX, topPos + 67 + shiftY, 0, vOffset, progress, 10);
		
		//Burn Slot bar progress. Max is 30.
		progress = (int) (menu.getInputBurnProgress() * 30);
		blit(matrix, leftPos + 64 + shiftX, topPos + 67 + shiftY, 0, vOffset, progress, 10);
	}
	
	public static class Baseline
			extends GuiCustomRelay<ContainerCustomRelay.Baseline>
	{
		private static final ResourceLocation MK3_TEXTURE = PECore.rl("textures/gui/relay3.png");
		
		public Baseline(ContainerCustomRelay.Baseline container, Inventory invPlayer, Component title)
		{
			super(container, invPlayer, title, MK3_TEXTURE, 125, 39, 195, 105, 37, 15);
			this.imageWidth = 212;
			this.imageHeight = 194;
			this.titleLabelX = 38;
		}
	}
}