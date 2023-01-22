package org.zeith.equivadds.init;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.zeith.equivadds.blocks.*;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;

@SimplyRegister
public interface BlocksEA
{
	@RegistryName("emc_proxy")
	BlockEMCProxy EMC_PROXY = new BlockEMCProxy(BlockBehaviour.Properties.of(Material.STONE).strength(3F));
	
	@RegistryName("collector_mk4")
	BlockCustomCollector COLLECTOR_MK4 = new BlockCustomCollector(EnumCollectorTiersEA.MK4, BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops()
			.strength(0.3F, 0.9F).lightLevel(state -> 15));
	
	@RegistryName("collector_mk5")
	BlockCustomCollector COLLECTOR_MK5 = new BlockCustomCollector(EnumCollectorTiersEA.MK5, BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops()
			.strength(0.3F, 0.9F).lightLevel(state -> 15));
	
	@RegistryName("collector_mk6")
	BlockCustomCollector COLLECTOR_MK6 = new BlockCustomCollector(EnumCollectorTiersEA.MK6, BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops()
			.strength(0.3F, 0.9F).lightLevel(state -> 15));
	
	@RegistryName("collector_mk7")
	BlockCustomCollector COLLECTOR_MK7 = new BlockCustomCollector(EnumCollectorTiersEA.MK7, BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops()
			.strength(0.3F, 0.9F).lightLevel(state -> 15));
	
	@RegistryName("relay_mk4")
	BlockCustomRelay RELAY_MK4 = new BlockCustomRelay(EnumRelayTiersEA.MK4, BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops()
			.strength(10, 30).lightLevel(state -> 15));
	
	@RegistryName("relay_mk5")
	BlockCustomRelay RELAY_MK5 = new BlockCustomRelay(EnumRelayTiersEA.MK5, BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops()
			.strength(10, 30).lightLevel(state -> 15));
	
	@RegistryName("relay_mk6")
	BlockCustomRelay RELAY_MK6 = new BlockCustomRelay(EnumRelayTiersEA.MK6, BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops()
			.strength(10, 30).lightLevel(state -> 15));
	
	@RegistryName("relay_mk7")
	BlockCustomRelay RELAY_MK7 = new BlockCustomRelay(EnumRelayTiersEA.MK7, BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops()
			.strength(10, 30).lightLevel(state -> 15));
	
	@RegistryName("zeitheron_fuel_block")
	BlockFuel ZEITH_FUEL_BLOCK = new BlockFuel(BlockBehaviour.Properties.of(Material.STONE).strength(3F), () -> ItemsEA.ZEITH_FUEL);
	
	@RegistryName("mysterium_fuel_block")
	BlockFuel MYSTERIUM_FUEL_BLOCK = new BlockFuel(BlockBehaviour.Properties.of(Material.STONE).strength(3F), () -> ItemsEA.MYSTERIUM_FUEL);
	
	@RegistryName("citrinium_fuel_block")
	BlockFuel CITRINIUM_FUEL_BLOCK = new BlockFuel(BlockBehaviour.Properties.of(Material.STONE).strength(3F), () -> ItemsEA.CITRINIUM_FUEL);
	
	@RegistryName("verdanite_fuel_block")
	BlockFuel VERDANITE_FUEL_BLOCK = new BlockFuel(BlockBehaviour.Properties.of(Material.STONE).strength(3F), () -> ItemsEA.VERDANITE_FUEL);
	
	@RegistryName("blue_matter_block")
	SimpleBlockEA BLUE_MATTER_BLOCK = new SimpleBlockEA(BlockBehaviour.Properties.of(Material.STONE).strength(3F).requiresCorrectToolForDrops(), BlockHarvestAdapter.MineableType.PICKAXE, EnumMatterTypesEA.BLUE_MATTER);
	
	@RegistryName("purple_matter_block")
	SimpleBlockEA PURPLE_MATTER_BLOCK = new SimpleBlockEA(BlockBehaviour.Properties.of(Material.STONE).strength(3F).requiresCorrectToolForDrops(), BlockHarvestAdapter.MineableType.PICKAXE, EnumMatterTypesEA.PURPLE_MATTER);
	
	@RegistryName("orange_matter_block")
	SimpleBlockEA ORANGE_MATTER_BLOCK = new SimpleBlockEA(BlockBehaviour.Properties.of(Material.STONE).strength(3F).requiresCorrectToolForDrops(), BlockHarvestAdapter.MineableType.PICKAXE, EnumMatterTypesEA.ORANGE_MATTER);
	
	@RegistryName("green_matter_block")
	SimpleBlockEA GREEN_MATTER_BLOCK = new SimpleBlockEA(BlockBehaviour.Properties.of(Material.STONE).strength(3F).requiresCorrectToolForDrops(), BlockHarvestAdapter.MineableType.PICKAXE, EnumMatterTypesEA.GREEN_MATTER);
}