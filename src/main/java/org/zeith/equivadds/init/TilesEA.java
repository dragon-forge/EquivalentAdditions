package org.zeith.equivadds.init;

import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.equivadds.tiles.TileEMCProxy;
import org.zeith.equivadds.tiles.collectors.*;
import org.zeith.equivadds.tiles.relays.*;
import org.zeith.hammerlib.annotations.*;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.core.adapter.TagAdapter;

@SimplyRegister
public interface TilesEA
{
	@RegistryName("emc_proxy")
	BlockEntityType<TileEMCProxy> EMC_PROXY = BlockAPI.createBlockEntityType(TileEMCProxy::new, BlocksEA.EMC_PROXY);
	
	@RegistryName("collector_mk4")
	BlockEntityType<TileCollectorMK4> COLLECTOR_MK4 = BlockAPI.createBlockEntityType(TileCollectorMK4::new, BlocksEA.COLLECTOR_MK4);
	
	@RegistryName("collector_mk5")
	BlockEntityType<TileCollectorMK5> COLLECTOR_MK5 = BlockAPI.createBlockEntityType(TileCollectorMK5::new, BlocksEA.COLLECTOR_MK5);
	
	@RegistryName("collector_mk6")
	BlockEntityType<TileCollectorMK6> COLLECTOR_MK6 = BlockAPI.createBlockEntityType(TileCollectorMK6::new, BlocksEA.COLLECTOR_MK6);
	
	@RegistryName("collector_mk7")
	BlockEntityType<TileCollectorMK7> COLLECTOR_MK7 = BlockAPI.createBlockEntityType(TileCollectorMK7::new, BlocksEA.COLLECTOR_MK7);
	
	@RegistryName("relay_mk4")
	BlockEntityType<TileRelayMK4> RELAY_MK4 = BlockAPI.createBlockEntityType(TileRelayMK4::new, BlocksEA.RELAY_MK4);
	
	@RegistryName("relay_mk5")
	BlockEntityType<TileRelayMK5> RELAY_MK5 = BlockAPI.createBlockEntityType(TileRelayMK5::new, BlocksEA.RELAY_MK5);
	
	@RegistryName("relay_mk6")
	BlockEntityType<TileRelayMK6> RELAY_MK6 = BlockAPI.createBlockEntityType(TileRelayMK6::new, BlocksEA.RELAY_MK6);
	
	@RegistryName("relay_mk7")
	BlockEntityType<TileRelayMK7> RELAY_MK7 = BlockAPI.createBlockEntityType(TileRelayMK7::new, BlocksEA.RELAY_MK7);
	
	static @Setup void blocklist()
	{
		// EMC Proxies should NEVER be overclocked, otherwise may clog network channel with packets.
		TagAdapter.bind(PETags.BlockEntities.BLACKLIST_TIME_WATCH,
				EMC_PROXY
		);
	}
}