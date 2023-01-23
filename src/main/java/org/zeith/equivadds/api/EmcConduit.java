package org.zeith.equivadds.api;

import moze_intel.projecte.gameObjs.EnumCollectorTier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import org.zeith.api.registry.RegistryMapping;
import org.zeith.equivadds.blocks.conduit.BlockConduit;
import org.zeith.equivadds.blocks.conduit.TileEmcConduit;
import org.zeith.equivadds.init.EnumCollectorTiersEA;
import org.zeith.hammerlib.api.fml.ICustomRegistrar;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.util.java.Cast;

public class EmcConduit
		implements ICustomRegistrar, ItemLike
{
	public final BlockConduit block;
	public final BlockEntityType<TileEmcConduit> tile;
	
	public EmcConduit(ConduitProperties props)
	{
		this.block = new BlockConduit(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().lightLevel(state -> 10), props, () -> EmcConduit.this.tile);
		this.tile = BlockAPI.createBlockEntityType((pos, state) -> new TileEmcConduit(EmcConduit.this.tile, props, pos, state), this.block);
	}
	
	@Override
	public void performRegister(RegisterEvent event, ResourceLocation id)
	{
		IForgeRegistry<?> reg = event.getForgeRegistry();
		var superType = RegistryMapping.getSuperType(event.getRegistryKey());
		if(superType == null) return;
		if(reg == null) reg = RegistryMapping.getRegistryByType(superType);
		
		if(Item.class.equals(superType))
		{
			reg.register(id, Cast.cast(block.createBlockItem()));
		}
		
		if(Block.class.equals(superType))
		{
			reg.register(id, Cast.cast(block));
		}
		
		if(BlockEntityType.class.equals(superType))
		{
			reg.register(id, Cast.cast(tile));
		}
	}
	
	@Override
	public Item asItem()
	{
		return block.asItem();
	}
	
	public record ConduitProperties(long transfer)
	{
		public static ConduitProperties ofVanilla(EnumCollectorTier collector)
		{
			return new ConduitProperties(collector.getStorage() / 2);
		}
		
		public static ConduitProperties ofCustom(EnumCollectorTiersEA collector)
		{
			return new ConduitProperties(collector.getStorage() / 2);
		}
	}
}