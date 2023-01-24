package org.zeith.equivadds.compat.ae2;

import net.minecraftforge.eventbus.api.IEventBus;

import java.util.function.Supplier;

@FunctionalInterface
public interface IInitializer
{
	static Supplier<IInitializer> dummy()
	{
		return () -> (bus) ->
		{
		};
	}
	
	void init(IEventBus modBus);
}