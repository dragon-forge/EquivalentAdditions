package org.zeith.equivadds.tiles.relays;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.equivadds.init.EnumRelayTiersEA;

public class TileRelayMK5
		extends TileCustomRelay
{
	
	public TileRelayMK5(BlockPos pos, BlockState state)
	{
		super(EnumRelayTiersEA.MK5, pos, state, 21);
	}
	
	@Override
	protected double getBonusToAdd()
	{
		return 0.75;
	}
}