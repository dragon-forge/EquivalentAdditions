package org.zeith.equivadds.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.*;

import java.util.Map;

/**
 * An interface for blocks that can be logged with multiple different fluids.
 * <p>
 * These blocks will have a boolean property for each fluid that they can be logged with,
 * and the fluid state of the block will be determined by the fluid properties that are set to true.
 */
public interface IMultiFluidLoggableBlock
		extends SimpleWaterloggedBlock
{
	/**
	 * Gets a map of fluid to boolean property representing the fluids that this block can be logged with and
	 * the corresponding property for each fluid.
	 *
	 * @return A map of fluid to boolean property.
	 */
	Map<FlowingFluid, BooleanProperty> getFluidLoggableProperties();
	
	/**
	 * Gets the block state with all fluid properties set to false.
	 *
	 * @param state
	 * 		The current block state.
	 *
	 * @return The block state with all fluid properties set to false.
	 */
	default BlockState getStateWithNoFluids(BlockState state)
	{
		for(var entry : getFluidLoggableProperties().entrySet())
			state = state.setValue(entry.getValue(), false);
		return state;
	}
	
	/**
	 * Updates the block state with the correct shape for the fluids logged in it.
	 *
	 * @param accessor
	 * 		The level accessor.
	 * @param pos
	 * 		The position of the block.
	 * @param state
	 * 		The current block state.
	 *
	 * @return The updated block state.
	 */
	default BlockState updateFluidLoggedShape(LevelAccessor accessor, BlockPos pos, BlockState state)
	{
		for(var entry : getFluidLoggableProperties().entrySet())
			if(state.getValue(entry.getValue()))
				accessor.scheduleTick(pos, entry.getKey(), entry.getKey().getTickDelay(accessor));
		return state;
	}
	
	/**
	 * Gets the block state with the correct fluid properties set for placement.
	 *
	 * @param ctx
	 * 		The block placement context.
	 * @param def
	 * 		The default block state.
	 *
	 * @return The block state with the correct fluid properties set.
	 */
	default BlockState getFluidLoggedStateForPlacement(BlockPlaceContext ctx, BlockState def)
	{
		var accessor = ctx.getLevel();
		var pos = ctx.getClickedPos();
		
		for(var entry : getFluidLoggableProperties().entrySet())
			def = def.setValue(entry.getValue(), accessor.getFluidState(pos).getType() == entry.getKey());
		
		return def;
	}
	
	/**
	 * Determines if the given fluid can be placed in this block.
	 *
	 * @param getter
	 * 		The block getter.
	 * @param pos
	 * 		The position of the block.
	 * @param state
	 * 		The current block state.
	 * @param fluid
	 * 		The fluid to place.
	 *
	 * @return {@code true} if the fluid can be placed in this block, {@code false} otherwise.
	 */
	@Override
	default boolean canPlaceLiquid(BlockGetter getter, BlockPos pos, BlockState state, Fluid fluid)
	{
		var props = getFluidLoggableProperties();
		if(props.values().stream().anyMatch(state::getValue))
			return false;
		return fluid instanceof FlowingFluid && props.containsKey(fluid);
	}
	
	/**
	 * Gets the fluid state of the block.
	 *
	 * @param state
	 * 		The current block state.
	 *
	 * @return The fluid state of the block.
	 */
	default FluidState getFluidLoggedState(BlockState state)
	{
		for(var entry : getFluidLoggableProperties().entrySet())
			if(state.getValue(entry.getValue()))
				return entry.getKey().getSource(false);
		
		return Fluids.EMPTY.defaultFluidState();
	}
	
	/**
	 * Places the given fluid in this block.
	 *
	 * @param level
	 * 		The level.
	 * @param pos
	 * 		The position of the block.
	 * @param state
	 * 		The current block state.
	 * @param fluid
	 * 		The fluid to place.
	 *
	 * @return {@code true} if the fluid was placed, {@code false} otherwise.
	 */
	@Override
	default boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid)
	{
		var props = getFluidLoggableProperties();
		
		if(props.values().stream().anyMatch(state::getValue))
			return false;
		
		for(var prop : props.entrySet())
		{
			if(!state.getValue(prop.getValue()) && fluid.getType() == prop.getKey())
			{
				if(!level.isClientSide())
				{
					level.setBlock(pos, state.setValue(prop.getValue(), true), 3);
					level.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(level));
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Picks up the block and returns it as an {@link ItemStack}. If the block is logged with a fluid,
	 * the fluid will be drained from the block and the block will be returned as an empty bucket.
	 *
	 * @param level
	 * 		The level containing the block.
	 * @param pos
	 * 		The position of the block.
	 * @param state
	 * 		The state of the block.
	 *
	 * @return The {@link ItemStack} representing the picked up liquid.
	 */
	@Override
	default ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state)
	{
		for(var prop : getFluidLoggableProperties().entrySet())
		{
			if(state.getValue(prop.getValue()))
			{
				var bucket = prop.getKey().getBucket();
				if(bucket == Items.AIR) continue;
				level.setBlock(pos, state.setValue(prop.getValue(), false), 3);
				if(!state.canSurvive(level, pos))
					level.destroyBlock(pos, true);
				return new ItemStack(bucket);
			}
		}
		
		return ItemStack.EMPTY;
	}
}