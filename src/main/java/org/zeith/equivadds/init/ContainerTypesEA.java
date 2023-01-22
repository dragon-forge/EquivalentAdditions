package org.zeith.equivadds.init;

import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import org.zeith.equivadds.EquivalentAdditions;
import org.zeith.equivadds.container.ContainerCustomCollector;
import org.zeith.equivadds.container.ContainerCustomRelay;
import org.zeith.equivadds.tiles.collectors.TileCustomCollector;
import org.zeith.equivadds.tiles.relays.TileCustomRelay;

public class ContainerTypesEA
{
	
	public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(EquivalentAdditions.MOD_ID);
	
	public static final ContainerTypeRegistryObject<ContainerCustomCollector.Baseline> COLLECTOR_CONTAINER = CONTAINER_TYPES.register(() -> "collector", TileCustomCollector.class, ContainerCustomCollector.Baseline::new);
	public static final ContainerTypeRegistryObject<ContainerCustomRelay.Baseline> RELAY_CONTAINER = CONTAINER_TYPES.register(() -> "relay", TileCustomRelay.class, ContainerCustomRelay.Baseline::new);
}