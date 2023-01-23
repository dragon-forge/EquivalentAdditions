package org.zeith.equivadds.blocks.conduit.base.traversable;

import java.util.Objects;

public class EndpointDistinct<T>
{
	public final TraversablePath<T> path;
	
	public EndpointDistinct(TraversablePath<T> path)
	{
		this.path = path;
	}
	
	public TraversablePath<T> path()
	{
		return path;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		EndpointDistinct<?> that = (EndpointDistinct<?>) o;
		return path.endpoint.sameBlock(that.path.endpoint);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(path);
	}
}
