package org.zeith.equivadds.compat.ae2;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.client.StorageCellModels;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.storage.StorageCells;
import appeng.parts.automation.StackWorldBehaviors;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.*;
import org.zeith.api.registry.RegistryMapping;
import org.zeith.equivadds.EquivalentAdditions;
import org.zeith.equivadds.compat.CompatEA;
import org.zeith.equivadds.compat.ae2.client.CompatAE2Client;
import org.zeith.equivadds.compat.ae2.init.ItemsEAAE2;
import org.zeith.equivadds.compat.ae2.item.cell.EmcCellHandler;
import org.zeith.equivadds.compat.ae2.me.*;
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
	
	public void register(RegisterEvent event)
	{
		IForgeRegistry<?> reg = event.getForgeRegistry();
		if(reg == null) reg = RegistryMapping.getRegistryByType(RegistryMapping.getSuperType(event.getRegistryKey()));
		
		RegistryAdapter.register(event, reg, ItemsEAAE2.class, EquivalentAdditions.MOD_ID, "ae2/");
		
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
	}
}