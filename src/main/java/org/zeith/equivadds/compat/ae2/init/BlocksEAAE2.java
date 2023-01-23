package org.zeith.equivadds.compat.ae2.init;

import appeng.core.definitions.AEBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.zeith.equivadds.compat.ae2.block.BlockEmcSynthesisChamber;
import org.zeith.equivadds.compat.ae2.client.TESREmcSynthesisChamber;
import org.zeith.equivadds.compat.ae2.tile.TileEmcSynthesisChamber;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.client.TileRenderer;
import org.zeith.hammerlib.api.forge.BlockAPI;

public interface BlocksEAAE2
{
	@RegistryName("emc_synthesis_chamber")
	BlockEmcSynthesisChamber EMC_SYNTHESIS_CHAMBER = new BlockEmcSynthesisChamber(BlockBehaviour.Properties.copy(AEBlocks.MOLECULAR_ASSEMBLER.block()));
	
	@RegistryName("emc_synthesis_chamber")
	@TileRenderer(TESREmcSynthesisChamber.class)
	BlockEntityType<TileEmcSynthesisChamber> EMC_SYNTHESIS_CHAMBER_TYPE = BlockAPI.createBlockEntityType(TileEmcSynthesisChamber::new, EMC_SYNTHESIS_CHAMBER);
}