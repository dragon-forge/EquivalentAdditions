package org.zeith.equivadds.proxy;

import net.minecraftforge.eventbus.api.IEventBus;
import org.zeith.equivadds.init.ContainerTypesEA;

public class CommonProxyEA
{
	public void construct(IEventBus modBus)
	{
		ContainerTypesEA.CONTAINER_TYPES.register(modBus);
	}
}