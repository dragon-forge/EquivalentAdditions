package org.zeith.equivadds.blocks.conduit.base.traversable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public interface ITraversable<T>
{
	Optional<? extends ITraversable<T>> getRelativeTraversable(Direction side, T contents);
	
	default Stream<ITraversable<T>> allNeighbors(T contents)
	{
		return Arrays.stream(EndpointData.DIRECTIONS)
				.map(dir -> getRelativeTraversable(dir, contents))
				.flatMap(Optional::stream);
	}
	
	// Higher priority will make this traversable preferred.
	List<EndpointData> getEndpoints(T contents);
	
	BlockPos getPosition();
	
	@Nullable
	default Direction getTo(ITraversable<T> other)
	{
		return Direction.fromNormal(other.getPosition().subtract(getPosition()));
	}
	
	@Nullable
	default Direction getFrom(ITraversable<T> other)
	{
		return Direction.fromNormal(getPosition().subtract(other.getPosition()));
	}
}