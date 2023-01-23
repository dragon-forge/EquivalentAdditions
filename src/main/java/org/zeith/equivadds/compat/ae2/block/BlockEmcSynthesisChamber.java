package org.zeith.equivadds.compat.ae2.block;

import appeng.block.AEBaseEntityBlock;
import appeng.util.InteractionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import org.zeith.equivadds.compat.ae2.tile.TileEmcSynthesisChamber;
import org.zeith.equivadds.init.ItemsEA;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.hammerlib.util.java.Cast;

import java.util.List;

public class BlockEmcSynthesisChamber
		extends AEBaseEntityBlock<TileEmcSynthesisChamber>
		implements ICustomBlockItem
{
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	public BlockEmcSynthesisChamber(BlockBehaviour.Properties props)
	{
		super(props);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
		TagAdapter.bind(BlockTags.MINEABLE_WITH_PICKAXE, this);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		return List.of(new ItemStack(this));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(POWERED);
	}
	
	@Override
	protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileEmcSynthesisChamber be)
	{
		return currentState.setValue(POWERED, be.isPowered());
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player p, InteractionHand hand,
								 BlockHitResult hit)
	{
		final TileEmcSynthesisChamber tg = this.getBlockEntity(level, pos);
		if(tg != null && !InteractionUtil.isInAlternateUseMode(p))
		{
			if(!level.isClientSide())
				ContainerAPI.openContainerTile(p, Cast.cast(tg));
			
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		
		return super.use(state, level, pos, p, hand, hit);
	}
	
	@Override
	public BlockItem createBlockItem()
	{
		return new BlockItem(this, ItemsEA.newProperties());
	}
}