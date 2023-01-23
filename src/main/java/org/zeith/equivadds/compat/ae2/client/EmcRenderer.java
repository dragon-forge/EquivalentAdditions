package org.zeith.equivadds.compat.ae2.client;

import appeng.api.client.IAEStackRenderHandler;
import appeng.client.gui.style.Blitter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.zeith.equivadds.EquivalentAdditions;
import org.zeith.equivadds.compat.ae2.me.EMCKey;
import org.zeith.equivadds.compat.ae2.me.EMCKeyType;
import org.zeith.hammerlib.client.utils.RenderUtils;

public class EmcRenderer
		implements IAEStackRenderHandler<EMCKey>
{
	public static final ResourceLocation EMC_SPRITE = EquivalentAdditions.id("item/ae2/emc");
	
	@Override
	public void drawInGui(Minecraft minecraft, PoseStack poseStack, int x, int y, int zIndex, EMCKey emcKey)
	{
		Blitter.sprite(RenderUtils.getMainSprite(EMC_SPRITE))
				.blending(false)
				.dest(x, y, 16, 16)
				.blit(poseStack, 100 + zIndex);
	}
	
	@Override
	public void drawOnBlockFace(PoseStack poseStack, MultiBufferSource buffers, EMCKey what, float scale, int combinedLight)
	{
		var sprite = RenderUtils.getMainSprite(EMC_SPRITE);
		
		poseStack.pushPose();
		poseStack.translate(0, 0, 0.01f);
		
		var buffer = buffers.getBuffer(RenderType.solid());
		
		scale -= 0.05f;
		
		var x0 = -scale / 2;
		var y0 = scale / 2;
		var x1 = scale / 2;
		var y1 = -scale / 2;
		
		var transform = poseStack.last().pose();
		
		buffer.vertex(transform, x0, y1, 0)
				.color(-1)
				.uv(sprite.getU0(), sprite.getV1())
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(combinedLight)
				.normal(0, 0, 1)
				.endVertex();
		
		buffer.vertex(transform, x1, y1, 0)
				.color(-1)
				.uv(sprite.getU1(), sprite.getV1())
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(combinedLight)
				.normal(0, 0, 1)
				.endVertex();
		
		buffer.vertex(transform, x1, y0, 0)
				.color(-1)
				.uv(sprite.getU1(), sprite.getV0())
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(combinedLight)
				.normal(0, 0, 1)
				.endVertex();
		
		buffer.vertex(transform, x0, y0, 0)
				.color(-1)
				.uv(sprite.getU0(), sprite.getV0())
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(combinedLight)
				.normal(0, 0, 1)
				.endVertex();
		
		poseStack.popPose();
	}
	
	@Override
	public Component getDisplayName(EMCKey stack)
	{
		return EMCKeyType.EMC;
	}
}