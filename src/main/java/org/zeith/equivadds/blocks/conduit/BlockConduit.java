package org.zeith.equivadds.blocks.conduit;

import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.equivadds.api.EmcConduit;
import org.zeith.equivadds.api.block.IMultiFluidLoggableBlock;
import org.zeith.equivadds.init.ItemsEA;
import org.zeith.equivadds.util.voxels.VoxelShapeCache;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.util.java.Cast;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BlockConduit
		extends Block
		implements EntityBlock, IMultiFluidLoggableBlock, ICustomBlockItem
{
	static final Direction[] DIRECTIONS = Direction.values();
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final Map<Direction, BooleanProperty> DIR2PROP = Map.of(
			Direction.UP, BlockStateProperties.UP,
			Direction.DOWN, BlockStateProperties.DOWN,
			Direction.NORTH, BlockStateProperties.NORTH,
			Direction.EAST, BlockStateProperties.EAST,
			Direction.SOUTH, BlockStateProperties.SOUTH,
			Direction.WEST, BlockStateProperties.WEST
	);
	
	public static final VoxelShape CORE_SHAPE = box(5, 5, 5, 11, 11, 11);
	
	public static final Map<Direction, VoxelShape> DIR2SHAPE = Map.of(
			Direction.UP, box(5, 11, 5, 11, 16, 11),
			Direction.DOWN, box(5, 0, 5, 11, 5, 11),
			Direction.NORTH, box(5, 5, 0, 11, 11, 5),
			Direction.EAST, box(11, 5, 5, 16, 11, 11),
			Direction.SOUTH, box(5, 5, 11, 11, 11, 16),
			Direction.WEST, box(0, 5, 5, 5, 11, 11)
	);
	
	public final EmcConduit.ConduitProperties conduit;
	public final Supplier<BlockEntityType<? extends TileEmcConduit>> tileType;
	
	public BlockConduit(Properties props, EmcConduit.ConduitProperties conduit, Supplier<BlockEntityType<? extends TileEmcConduit>> tileType)
	{
		super(props);
		
		this.conduit = conduit;
		this.tileType = tileType;
		
		shapeCache = new VoxelShapeCache(this, (state, $) -> Shapes.or(CORE_SHAPE,
				DIR2PROP.entrySet()
						.stream()
						.filter(dir -> state.getValue(dir.getValue()))
						.map(dir -> DIR2SHAPE.get(dir.getKey()))
						.toArray(VoxelShape[]::new)
		));
	}
	
	private final VoxelShapeCache shapeCache;
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext p_60558_)
	{
		return shapeCache.get(state);
	}
	
	@Override
	public TileEmcConduit newBlockEntity(BlockPos pos, BlockState state)
	{
		return tileType.get().create(pos, state);
	}
	
	public static final Map<FlowingFluid, BooleanProperty> FLUID_STATES = Map.of(
			Fluids.WATER, WATERLOGGED
	);
	
	@Override
	public Map<FlowingFluid, BooleanProperty> getFluidLoggableProperties()
	{
		return FLUID_STATES;
	}
	
	@Override
	public FluidState getFluidState(BlockState state)
	{
		return getFluidLoggedState(state);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		getFluidLoggableProperties().values().forEach(builder::add);
		DIR2PROP.values().forEach(builder::add);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltips, TooltipFlag flag)
	{
		if(ProjectEConfig.client.statToolTips.get())
		{
			tooltips.add(PELang.EMC_MAX_OUTPUT_RATE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, Constants.EMC_FORMATTER.format(conduit.transfer() / 20)));
		}
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction ignore0, BlockState ignore1, LevelAccessor accessor, BlockPos pos, BlockPos ignore2)
	{
		state = updateFluidLoggedShape(accessor, pos, state);
		
		var pipe = Cast.cast(accessor.getBlockEntity(pos), TileEmcConduit.class);
		if(pipe != null)
			for(Direction d : DIRECTIONS)
				state = state.setValue(DIR2PROP.get(d), pipe.doesConnectTo(d));
		
		return state;
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		var accessor = ctx.getLevel();
		var pos = ctx.getClickedPos();
		
		var state = getFluidLoggedStateForPlacement(ctx, defaultBlockState());
		
		var pipe = Cast.cast(accessor.getBlockEntity(pos), TileEmcConduit.class);
		if(pipe == null)
		{
			pipe = newBlockEntity(pos, state);
			pipe.setLevel(accessor);
		}
		
		for(Direction d : DIRECTIONS)
			state = state.setValue(DIR2PROP.get(d), pipe.doesConnectTo(d));
		
		return state;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
	
	@Override
	public boolean isCollisionShapeFullBlock(BlockState p_181242_, BlockGetter p_181243_, BlockPos p_181244_)
	{
		return false;
	}
	
	@Override
	public boolean isOcclusionShapeFullBlock(BlockState p_222959_, BlockGetter p_222960_, BlockPos p_222961_)
	{
		return false;
	}
	
	@Override
	public BlockItem createBlockItem()
	{
		return new BlockItem(this, ItemsEA.newProperties());
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
	{
		return BlockAPI.ticker();
	}
}