package org.zeith.equivadds.tiles.relays;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.equivadds.init.EnumRelayTiersEA;

public class TileRelayMK7
		extends TileCustomRelay
{
	
	public TileRelayMK7(BlockPos pos, BlockState state)
	{
		super(EnumRelayTiersEA.MK7, pos, state, 21);
	}
	
	@Override
	protected double getBonusToAdd()
	{
		return 1;
	}
}