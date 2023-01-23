package org.zeith.equivadds.api;

import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.zeith.equivadds.blocks.BlockEMCFlower;
import org.zeith.equivadds.init.EnumCollectorTiersEA;
import org.zeith.equivadds.init.EnumRelayTiersEA;
import org.zeith.equivadds.tiles.TileEMCFlower;
import org.zeith.hammerlib.api.fml.ICustomRegistrar;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.core.init.TagsHL;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;

import java.util.function.Supplier;

public class EmcFlower
		implements ICustomRegistrar
{
	public final String name;
	public final Item collectorArray, relayArray;
	public final BlockEMCFlower flowerBlock;
	public final BlockEntityType<TileEMCFlower> flowerTile;
	
	public EmcFlower(String name, FlowerProperties props, Supplier<Item> newMaterial)
	{
		this.name = name;
		this.collectorArray = newMaterial.get();
		this.relayArray = newMaterial.get();
		this.flowerBlock = new BlockEMCFlower(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3F).lightLevel(state -> 15), props, this::tile);
		this.flowerTile = BlockAPI.createBlockEntityType((pos, state) -> new TileEMCFlower(tile(), props, pos, state), flowerBlock);
	}
	
	public BlockEMCFlower block()
	{
		return flowerBlock;
	}
	
	public BlockEntityType<? extends TileEMCFlower> tile()
	{
		return flowerTile;
	}
	
	@Override
	public void performRegister(RegisterEvent event, ResourceLocation id)
	{
		var key = event.getRegistryKey();
		
		if(key.equals(ForgeRegistries.Keys.BLOCKS))
		{
			ForgeRegistries.BLOCKS.register(id, flowerBlock);
		}
		
		if(key.equals(ForgeRegistries.Keys.ITEMS))
		{
			ForgeRegistries.ITEMS.register(new ResourceLocation(id.getNamespace(), "relay_array/" + name), relayArray);
			ForgeRegistries.ITEMS.register(new ResourceLocation(id.getNamespace(), "collector_array/" + name), collectorArray);
			ForgeRegistries.ITEMS.register(id, flowerBlock.createBlockItem());
		}
		
		if(key.equals(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES))
		{
			ForgeRegistries.BLOCK_ENTITY_TYPES.register(id, flowerTile);
		}
	}
	
	public void registerRecipes(RegisterRecipesEvent e, ItemLike matter, ItemLike collector, ItemLike relay)
	{
		e.shaped().result(collectorArray)
				.shape("ccc", " g ")
				.map('c', collector)
				.map('g', TagsHL.Items.STORAGE_BLOCKS_GLOWSTONE)
				.register();
		
		e.shaped().result(relayArray)
				.shape("ccc", " g ")
				.map('c', relay)
				.map('g', Tags.Items.OBSIDIAN)
				.register();
		
		e.shaped().result(flowerBlock)
				.shape("ccc", "rdr", "ccc")
				.map('c', collectorArray)
				.map('r', relayArray)
				.map('d', matter)
				.register();
	}
	
	public record FlowerProperties(long genRate, long collectorStorage, long relayStorage)
	{
		public FlowerProperties(long genRate, long collectorStorage, long relayStorage)
		{
			// We have 18 collectors per flower!
			this.genRate = genRate * 18L;
			this.collectorStorage = collectorStorage * 18L;
			this.relayStorage = relayStorage * 6L;
		}
		
		public static FlowerProperties ofVanilla(EnumCollectorTier collector, EnumRelayTier relay)
		{
			return new FlowerProperties(collector.getGenRate(), collector.getStorage(), relay.getStorage());
		}
		
		public static FlowerProperties ofCustom(EnumCollectorTiersEA collector, EnumRelayTiersEA relay)
		{
			return new FlowerProperties(collector.getGenRate(), collector.getStorage(), relay.getStorage());
		}
		
		public long storage()
		{
			return collectorStorage + relayStorage;
		}
	}
}