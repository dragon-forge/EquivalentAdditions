package org.zeith.equivadds.util.voxels;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Class for caching VoxelShapes for blocks.
 * Provides methods for resetting the cache and generating VoxelShapes for specific BlockStates.
 */
public class VoxelShapeCache
{
	/**
	 * Map for storing cached VoxelShapes.
	 */
	private final Map<BlockState, VoxelShape> cache = new ConcurrentHashMap<>();
	
	/**
	 * Function for generating a VoxelShape for a specific BlockState.
	 */
	private final Function<BlockState, VoxelShape> generator;
	
	/**
	 * Constructs a new VoxelShapeCache with the provided generator function.
	 *
	 * @param block
	 * 		block to generate VoxelShapes for
	 * @param generator
	 * 		function for generating VoxelShapes for specific BlockStates
	 */
	public VoxelShapeCache(Block block, BiFunction<BlockState, VoxelShapeCacheBuilder, VoxelShape> generator)
	{
		this.generator = state -> generator.apply(state, VoxelShapeCacheBuilder.INSTANCE);
		build(block);
	}
	
	/**
	 * Clears the cache of all stored VoxelShapes.
	 */
	public void reset()
	{
		cache.clear();
	}
	
	/**
	 * Generates VoxelShapes for all possible BlockStates of the provided block and stores them in the cache.
	 *
	 * @param block
	 * 		block to generate VoxelShapes for
	 */
	public void build(Block block)
	{
		for(BlockState possibleState : block.getStateDefinition().getPossibleStates())
			get(possibleState);
	}
	
	/**
	 * Returns the VoxelShape for the provided BlockState.
	 * If the VoxelShape is not in the cache, it will be generated using the generator function and stored in the cache before being returned.
	 *
	 * @param state
	 * 		BlockState to get the VoxelShape for
	 *
	 * @return VoxelShape for the provided BlockState
	 */
	public VoxelShape get(BlockState state)
	{
		return cache.computeIfAbsent(state, generator);
	}
}