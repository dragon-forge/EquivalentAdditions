package org.zeith.equivadds.compat.ae2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.zeith.equivadds.blocks.SimpleBlockEA;
import org.zeith.equivadds.compat.ae2.init.BlocksEAAE2;
import org.zeith.equivadds.compat.ae2.tile.TileEmcPatternEncoder;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.util.java.Cast;

public class BlockEmcPatternEncoder
		extends SimpleBlockEA
		implements EntityBlock
{
	public BlockEmcPatternEncoder(Properties props)
	{
		super(props, BlockHarvestAdapter.MineableType.PICKAXE, null);
	}
	
	@Override
	@Deprecated
	public void onRemove(BlockState prevState, Level world, BlockPos pos, BlockState newState, boolean flag64)
	{
		if(!prevState.is(newState.getBlock()))
		{
			BlockEntity tileentity = world.getBlockEntity(pos);
			if(tileentity instanceof TileEmcPatternEncoder enc)
			{
				Containers.dropContents(world, pos, enc.items);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			
			super.onRemove(prevState, world, pos, newState, flag64);
		}
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return BlocksEAAE2.EMC_PATTERN_ENCODER_TYPE.create(pos, state);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult res)
	{
		ContainerAPI.openContainerTile(player, Cast.cast(level.getBlockEntity(pos), TileEmcPatternEncoder.class));
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}