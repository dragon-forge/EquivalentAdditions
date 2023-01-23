package org.zeith.equivadds.blocks.conduit.base.traversable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.*;

public class TraversableHelper
{
	private static ThreadLocal<Random> RNG = ThreadLocal.withInitial(Random::new);
	
	public static <T> Optional<TraversablePath<T>> findClosestPath(ITraversable<T> start, Direction from, T contents, BlockPos desiredEndpoint)
	{
		var rng = RNG.get();
		
		return findAllPaths(start, from, contents)
				.stream()
				.max(Comparator.<TraversablePath<T>>
								comparingInt(p -> Objects.equals(p.endpoint.getActualPosition(), desiredEndpoint) ? Integer.MAX_VALUE : Integer.MIN_VALUE) // First we compare by endpoint
						.thenComparingInt(p -> p.endpoint.priority()) // Then compare by priority
						.thenComparingInt(p -> -p.size()) // Then, if the priority is the same, compare by the inverse length of the path (the shortest path will be preferred)
						.thenComparingInt(p -> // Then compare by hash, this makes a bit of random-ness if previous two fail.
						{
							rng.setSeed(p.seed);
							return rng.nextInt();
						})
				);
	}
	
	public static <T> Optional<TraversablePath<T>> findClosestPath(ITraversable<T> start, Direction from, T contents)
	{
		var rng = RNG.get();
		
		return findAllPaths(start, from, contents)
				.stream()
				.max(Comparator.<TraversablePath<T>>
								comparingInt(p -> p.endpoint.priority()) // First we compare by priority
						.thenComparingInt(p -> -p.size()) // Then, if the priority is the same, compare by the inverse length of the path (the shortest path will be preferred)
						.thenComparingInt(p -> // Then compare by hash, this makes a bit of random-ness if previous two fail.
						{
							rng.setSeed(p.seed);
							return rng.nextInt();
						})
				);
	}
	
	public static <T> List<TraversablePath<T>> findAllPaths(ITraversable<T> start, Direction from, T contents)
	{
		List<TraversablePath<T>> listOfPaths = new ArrayList<>();
		
		Stack<ITraversable<T>> currentBranch = new Stack<>();
		currentBranch.push(start);
		
		collectPaths(listOfPaths, currentBranch, from, contents);
		
		// De-duplicate paths by using EndpointDistinct, effectively fixing all loops and branches that lead to the same endpoint.
		return listOfPaths.stream()
				.sorted(Comparator.comparingInt(TraversablePath::size))
				.map(EndpointDistinct::new)
				.distinct()
				.map(EndpointDistinct::path)
				.toList();
	}
	
	public static <T> List<ITraversable<T>> allTraversables(ITraversable<T> start, T contents, boolean includeStart)
	{
		List<ITraversable<T>> lst = new ArrayList<>();
		lst.add(start);
		for(int i = 0; i < lst.size(); ++i)
			lst.get(i).allNeighbors(contents)
					.filter(t -> !lst.contains(t))
					.forEach(lst::add);
		if(!includeStart)
			lst.remove(0);
		return lst;
	}
	
	private static <T> void collectPaths(List<TraversablePath<T>> path, Stack<ITraversable<T>> branch, Direction from, T contents)
	{
		var currentPart = branch.peek();
		
		if(branch.size() > 1)
			currentPart.getEndpoints(contents)
					.forEach(endpoint -> path.add(TraversablePath.of(branch, endpoint)));
		else
			currentPart.getEndpoints(contents)
					.stream()
					.filter(endpoint -> endpoint.dir() != from) // for same-block endpoints, we should ignore the facing from where the item originates
					.forEach(endpoint -> path.add(TraversablePath.of(branch, endpoint)));
		
		currentPart.allNeighbors(contents)
				.filter(elem -> !branch.contains(elem))
				.forEach(elem ->
				{
					branch.push(elem);
					try
					{
						collectPaths(path, branch, from, contents);
					} catch(StackOverflowError ignored)
					{
					}
					branch.pop();
				});
	}
}