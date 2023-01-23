package org.zeith.equivadds.tiles.relays;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.equivadds.init.EnumRelayTiersEA;

public class TileRelayMK6
		extends TileCustomRelay
{
	
	public TileRelayMK6(BlockPos pos, BlockState state)
	{
		super(EnumRelayTiersEA.MK6, pos, state, 21);
	}
	
	@Override
	protected double getBonusToAdd()
	{
		return 0.85;
	}
	
	@Override
	public int getExtraBurnTimes()
	{
		return 16;
	}
}