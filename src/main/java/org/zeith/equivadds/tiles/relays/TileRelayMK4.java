package org.zeith.equivadds.tiles.relays;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.equivadds.init.EnumRelayTiersEA;

public class TileRelayMK4
		extends TileCustomRelay
{
	
	public TileRelayMK4(BlockPos pos, BlockState state)
	{
		super(EnumRelayTiersEA.MK4, pos, state, 21);
	}
	
	@Override
	protected double getBonusToAdd()
	{
		return 0.5;
	}
	
	@Override
	public int getExtraBurnTimes()
	{
		return 1;
	}
}