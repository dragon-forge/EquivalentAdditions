package org.zeith.equivadds.blocks.conduit.base.traversable;

import java.util.Stack;
import java.util.UUID;

public class TraversablePath<T>
		extends Stack<ITraversable<T>>
{
	public final long seed = UUID.randomUUID().getLeastSignificantBits();
	
	public EndpointData endpoint;
	
	public TraversablePath(EndpointData endpoint)
	{
		this.endpoint = endpoint;
	}
	
	public static <T> TraversablePath<T> of(Stack<ITraversable<T>> stack, EndpointData endpoint)
	{
		var path = new TraversablePath<T>(endpoint);
		path.addAll(stack);
		return path;
	}
}