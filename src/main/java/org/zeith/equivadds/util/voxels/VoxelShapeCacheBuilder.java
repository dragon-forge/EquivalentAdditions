package org.zeith.equivadds.util.voxels;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Utility class for building VoxelShapes for blocks.
 * Provides a method for creating a box-shaped VoxelShape and a method for rotating a Vec3 around a pivot point.
 */
public class VoxelShapeCacheBuilder
{
	/**
	 * Static instance of the VoxelShapeCacheBuilder.
	 */
	public static final VoxelShapeCacheBuilder INSTANCE = new VoxelShapeCacheBuilder();
	
	/**
	 * Creates a box-shaped VoxelShape with the provided dimensions and rotation.
	 *
	 * @param rotation
	 * 		direction to rotate the VoxelShape
	 * @param x
	 * 		minimum x value of the VoxelShape
	 * @param y
	 * 		minimum y value of the VoxelShape
	 * @param z
	 * 		minimum z value of the VoxelShape
	 * @param x2
	 * 		maximum x value of the VoxelShape
	 * @param y2
	 * 		maximum y value of the VoxelShape
	 * @param z2
	 * 		maximum z value of the VoxelShape
	 *
	 * @return box-shaped VoxelShape with the provided dimensions and rotation
	 */
	public VoxelShape box(Direction rotation, double x, double y, double z, double x2, double y2, double z2)
	{
		Vec3 pivot = new Vec3(8, 8, 8);
		
		Vec3 a = rotateAround(pivot, new Vec3(x, y, z), rotation),
				b = rotateAround(pivot, new Vec3(x2, y2, z2), rotation);
		
		return Block.box(
				Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z),
				Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z)
		);
	}
	
	/**
	 * Rotates the provided Vec3 around the provided pivot point using the provided direction.
	 *
	 * @param pivot
	 * 		pivot point to rotate the Vec3 around
	 * @param pos
	 * 		Vec3 to be rotated
	 * @param rotation
	 * 		direction to rotate the Vec3
	 *
	 * @return rotated Vec3
	 */
	public Vec3 rotateAround(Vec3 pivot, Vec3 pos, Direction rotation)
	{
		return switch(rotation)
				{
					default -> pos;
					case WEST -> new Vec3(pivot.x + (pos.z - pivot.z), pos.y, pivot.z - (pos.x - pivot.x));
					case SOUTH -> new Vec3(pivot.x - (pos.x - pivot.x), pos.y, pivot.z - (pos.z - pivot.z));
					case EAST -> new Vec3(pivot.x - (pos.z - pivot.z), pos.y, pivot.z + (pos.x - pivot.x));
				};
	}
}