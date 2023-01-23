package org.zeith.equivadds.compat.ae2.client;

import appeng.client.render.crafting.AssemblerAnimationStatus;
import appeng.client.render.effects.ParticleTypes;
import appeng.core.AppEng;
import appeng.core.AppEngClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;
import org.zeith.equivadds.compat.ae2.tile.TileEmcSynthesisChamber;

public class TESREmcSynthesisChamber
		implements BlockEntityRenderer<TileEmcSynthesisChamber>
{
	
	public static final ResourceLocation LIGHTS_MODEL = AppEng.makeId("block/molecular_assembler_lights");
	
	private final RandomSource particleRandom = RandomSource.create();
	
	public TESREmcSynthesisChamber(BlockEntityRendererProvider.Context context)
	{
	}
	
	@Override
	public void render(TileEmcSynthesisChamber chamber, float partialTicks, PoseStack ms,
					   MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		
		AssemblerAnimationStatus status = chamber.getAnimationStatus();
		if(status != null)
		{
			if(!Minecraft.getInstance().isPaused())
			{
				if(status.isExpired())
				{
					chamber.setAnimationStatus(null);
				}
				
				status.setAccumulatedTicks(status.getAccumulatedTicks() + partialTicks);
				status.setTicksUntilParticles(status.getTicksUntilParticles() - partialTicks);
			}
			
			renderStatus(chamber, ms, bufferIn, combinedLightIn, status);
		}
		
		if(chamber.isPowered())
		{
			renderPowerLight(ms, bufferIn, combinedLightIn, combinedOverlayIn);
		}
	}
	
	private void renderPowerLight(PoseStack ms, MultiBufferSource bufferIn, int combinedLightIn,
								  int combinedOverlayIn)
	{
		Minecraft minecraft = Minecraft.getInstance();
		BakedModel lightsModel = minecraft.getModelManager().getModel(LIGHTS_MODEL);
		// tripwire layer has the shader-property we're looking for:
		// alpha testing
		// translucency
		VertexConsumer buffer = bufferIn.getBuffer(RenderType.tripwire());
		
		// certainly doesn't use alpha testing, making it look like it will not work.
		minecraft.getBlockRenderer().getModelRenderer().renderModel(ms.last(), buffer, null,
				lightsModel, 1, 1, 1, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, null);
	}
	
	private void renderStatus(TileEmcSynthesisChamber chamber, PoseStack ms,
							  MultiBufferSource bufferIn, int combinedLightIn, AssemblerAnimationStatus status)
	{
		double centerX = chamber.getBlockPos().getX() + 0.5f;
		double centerY = chamber.getBlockPos().getY() + 0.5f;
		double centerZ = chamber.getBlockPos().getZ() + 0.5f;
		
		// Spawn crafting FX that fly towards the block's center
		Minecraft minecraft = Minecraft.getInstance();
		if(status.getTicksUntilParticles() <= 0)
		{
			status.setTicksUntilParticles(4);
			
			if(AppEngClient.instance().shouldAddParticles(particleRandom))
			{
				for(int x = 0; x < (int) Math.ceil(status.getSpeed() / 5.0); x++)
				{
					minecraft.particleEngine.createParticle(ParticleTypes.CRAFTING, centerX, centerY, centerZ, 0, 0, 0);
				}
			}
		}
		
		ItemStack is = status.getIs();
		
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		ms.pushPose();
		ms.translate(0.5, 0.5, 0.5); // Translate to center of block
		
		if(!(is.getItem() instanceof BlockItem))
		{
			ms.translate(0, -0.3f, 0);
		} else
		{
			ms.translate(0, -0.2f, 0);
		}
		
		itemRenderer.renderStatic(is, ItemTransforms.TransformType.GROUND, combinedLightIn,
				OverlayTexture.NO_OVERLAY, ms, bufferIn, 0);
		ms.popPose();
	}
}