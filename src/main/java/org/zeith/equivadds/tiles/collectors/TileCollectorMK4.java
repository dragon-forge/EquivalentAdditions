package org.zeith.equivadds.tiles.collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.equivadds.init.EnumCollectorTiersEA;

public class TileCollectorMK4
		extends TileCustomCollector
{
	public TileCollectorMK4(BlockPos pos, BlockState state)
	{
		super(EnumCollectorTiersEA.MK4, pos, state);
	}
	
	@Override
	protected int getInvSize()
	{
		return 16;
	}
}