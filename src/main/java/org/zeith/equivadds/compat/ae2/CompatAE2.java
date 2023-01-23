package org.zeith.equivadds.compat.ae2;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.client.StorageCellModels;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.parts.automation.StackWorldBehaviors;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.*;
import org.zeith.api.registry.RegistryMapping;
import org.zeith.equivadds.EquivalentAdditions;
import org.zeith.equivadds.compat.CompatEA;
import org.zeith.equivadds.compat.ae2.client.CompatAE2Client;
import org.zeith.equivadds.compat.ae2.init.BlocksEAAE2;
import org.zeith.equivadds.compat.ae2.init.ItemsEAAE2;
import org.zeith.equivadds.compat.ae2.item.cell.EmcCellHandler;
import org.zeith.equivadds.compat.ae2.me.*;
import org.zeith.equivadds.compat.ae2.tile.TileEmcSynthesisChamber;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.compat.base.BaseCompat;
import org.zeith.hammerlib.core.adapter.RegistryAdapter;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;

import java.util.Locale;

@BaseCompat.LoadCompat(
		modid = "ae2",
		compatType = CompatEA.class
)
public class CompatAE2
		extends CompatEA
{
	@Override
	public void setup(IEventBus bus)
	{
		bus.addListener(this::register);
		bus.addListener(this::commonSetup);
		
		StackWorldBehaviors.registerImportStrategy(EMCKeyType.TYPE, EmcImportStrategy::new);
		StackWorldBehaviors.registerExportStrategy(EMCKeyType.TYPE, EmcExportStrategy::new);
		StackWorldBehaviors.registerExternalStorageStrategy(EMCKeyType.TYPE, EmcExternalStorageStrategy::new);
		ContainerItemStrategy.register(EMCKeyType.TYPE, EMCKey.class, new EmcContainerStrategy());
		GenericSlotCapacities.register(EMCKeyType.TYPE, Long.MAX_VALUE);
		StorageCells.addCellHandler(EmcCellHandler.INSTANCE);
		
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () ->
		{
			return CompatAE2Client::init;
		});
		
		HammerLib.EVENT_BUS.addListener(this::recipes);
	}
	
	public void commonSetup(FMLCommonSetupEvent e)
	{
		var entityClass = TileEmcSynthesisChamber.class;
		
		BlockEntityTicker<TileEmcSynthesisChamber> serverTicker = null;
		if(ServerTickingBlockEntity.class.isAssignableFrom(entityClass))
			serverTicker = (level, pos, state, entity) -> ((ServerTickingBlockEntity) entity).serverTick();
		
		BlockEntityTicker<TileEmcSynthesisChamber> clientTicker = null;
		if(ClientTickingBlockEntity.class.isAssignableFrom(entityClass))
			clientTicker = (level, pos, state, entity) -> ((ClientTickingBlockEntity) entity).clientTick();
		
		BlocksEAAE2.EMC_SYNTHESIS_CHAMBER.setBlockEntity(entityClass, BlocksEAAE2.EMC_SYNTHESIS_CHAMBER_TYPE, clientTicker, serverTicker);
		
		Upgrades.add(AEItems.SPEED_CARD, BlocksEAAE2.EMC_SYNTHESIS_CHAMBER, 5);
	}
	
	public void register(RegisterEvent event)
	{
		IForgeRegistry<?> reg = event.getForgeRegistry();
		if(reg == null) reg = RegistryMapping.getRegistryByType(RegistryMapping.getSuperType(event.getRegistryKey()));
		
		RegistryAdapter.register(event, reg, ItemsEAAE2.class, EquivalentAdditions.MOD_ID, "ae2/");
		RegistryAdapter.register(event, reg, BlocksEAAE2.class, EquivalentAdditions.MOD_ID, "ae2/");
		
		var key = event.getRegistryKey();
		
		if(key.equals(ForgeRegistries.Keys.BLOCKS))
		{
			AEKeyTypes.register(EMCKeyType.TYPE);
		}
		
		if(key.equals(ForgeRegistries.Keys.ITEMS))
		{
			for(var tier : ItemsEAAE2.Tier.values())
			{
				var cell = ItemsEAAE2.get(tier);
				
				var id = EquivalentAdditions.id("block/ae2/drive/cells/" + tier.name().toLowerCase(Locale.ROOT).substring(1));
				StorageCellModels.registerModel(cell, id);
			}
		}
	}
	
	public void recipes(RegisterRecipesEvent e)
	{
		e.shaped().result(ItemsEAAE2.EMC_CELL_HOUSING)
				.shape("gkg", "k k", "mmm")
				.map('g', ForgeRegistries.ITEMS.getValue(new ResourceLocation("ae2", "quartz_glass")))
				.map('k', PEItems.KLEIN_STAR_SPHERE)
				.map('m', PEItems.DARK_MATTER)
				.register();
		
		for(var tier : ItemsEAAE2.Tier.values())
		{
			var cell = ItemsEAAE2.get(tier);
			
			e.shaped().result(cell)
					.shape("gkg", "kCk", "mmm")
					.map('g', ForgeRegistries.ITEMS.getValue(new ResourceLocation("ae2", "quartz_glass")))
					.map('k', PEItems.KLEIN_STAR_SPHERE)
					.map('C', cell.getCoreItem())
					.map('m', PEItems.DARK_MATTER)
					.register();
			
			e.shapeless().result(cell)
					.addAll(ItemsEAAE2.EMC_CELL_HOUSING, cell.getCoreItem())
					.register();
		}
		
		e.shaped().result(BlocksEAAE2.EMC_SYNTHESIS_CHAMBER, 4)
				.shape("mam", "ata", "mam")
				.map('m', PEItems.DARK_MATTER)
				.map('a', AEBlocks.MOLECULAR_ASSEMBLER)
				.map('t', PEBlocks.TRANSMUTATION_TABLE)
				.register();
		e.shaped().result(BlocksEAAE2.EMC_SYNTHESIS_CHAMBER, 4)
				.shape("mam", "ata", "mam")
				.map('a', PEItems.DARK_MATTER)
				.map('m', AEBlocks.MOLECULAR_ASSEMBLER)
				.map('t', PEBlocks.TRANSMUTATION_TABLE)
				.register();
	}
}