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
import org.zeith.equivadds.container.ContainerCustomCollector;

public abstract class GuiCustomCollector<T extends ContainerCustomCollector>
		extends PEContainerScreen<T>
{
	public GuiCustomCollector(T container, Inventory invPlayer, Component title)
	{
		super(container, invPlayer, title);
	}
	
	protected abstract ResourceLocation getTexture();
	
	protected int getBonusXShift()
	{
		return 0;
	}
	
	protected int getTextureBonusXShift()
	{
		return 0;
	}
	
	@Override
	protected void renderLabels(@NotNull PoseStack matrix, int x, int y)
	{
		this.font.draw(matrix, Long.toString(this.menu.emc.get()), (float) (60 + this.getBonusXShift()), 32.0F, 4210752);
		long kleinCharge = this.menu.kleinEmc.get();
		if(kleinCharge > 0L)
			this.font.draw(matrix, Constants.EMC_FORMATTER.format(kleinCharge), (float) (60 + this.getBonusXShift()), 44.0F, 4210752);
	}
	
	@Override
	protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int x, int y)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, this.getTexture());
		this.blit(matrix, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		int progress = (int) ((double) this.menu.sunLevel.get() * 12.0 / 16.0);
		this.blit(matrix, this.leftPos + 126 + this.getBonusXShift(), this.topPos + 49 - progress, 177 + this.getTextureBonusXShift(), 13 - progress, 12, progress);
		this.blit(matrix, this.leftPos + 64 + this.getBonusXShift(), this.topPos + 18, 0, 166, (int) ((double) (this.menu).emc.get() / (double) (this.menu).collector.getMaximumEmc() * 48.0), 10);
		progress = (int) (this.menu.getKleinChargeProgress() * 48.0);
		this.blit(matrix, this.leftPos + 64 + this.getBonusXShift(), this.topPos + 58, 0, 166, progress, 10);
		progress = (int) (this.menu.getFuelProgress() * 24.0);
		this.blit(matrix, this.leftPos + 138 + this.getBonusXShift(), this.topPos + 55 - progress, 176 + this.getTextureBonusXShift(), 38 - progress, 10, progress + 1);
	}
	
	public static class Baseline
			extends GuiCustomCollector<ContainerCustomCollector.Baseline>
	{
		public Baseline(ContainerCustomCollector.Baseline container, Inventory invPlayer, Component title)
		{
			super(container, invPlayer, title);
			this.imageWidth = 218;
			this.imageHeight = 165;
		}
		
		@Override
		protected ResourceLocation getTexture()
		{
			return PECore.rl("textures/gui/collector3.png");
		}
		
		@Override
		protected int getBonusXShift()
		{
			return 34;
		}
		
		@Override
		protected int getTextureBonusXShift()
		{
			return 43;
		}
	}
}