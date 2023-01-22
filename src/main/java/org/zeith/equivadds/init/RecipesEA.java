package org.zeith.equivadds.init;

import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.annotations.ProvideRecipes;
import org.zeith.hammerlib.api.IRecipeProvider;
import org.zeith.hammerlib.core.init.TagsHL;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;

import java.util.Arrays;

@ProvideRecipes
public class RecipesEA
		implements IRecipeProvider
{
	private void addFuel(RegisterRecipesEvent e, ItemLike fuelItem, ItemLike fuelBlock, ItemLike prevTierFuelItem)
	{
		Object[] fuels = new Object[4];
		Arrays.fill(fuels, prevTierFuelItem);
		e.shapeless().result(fuelItem).addAll(fuels).add(PEItems.PHILOSOPHERS_STONE).register();
		e.shapeless().result(fuelItem, 9).add(fuelBlock).register();
		e.shaped().result(fuelBlock).shape("fff", "fff", "fff").map('f', fuelItem).register();
	}
	
	private void addMatter(RegisterRecipesEvent e, ItemLike matter, ItemLike matterBlock,
						   ItemLike collector, ItemLike relay,
						   ItemLike prevCollector, ItemLike prevRelay)
	{
		e.shapeless().result(matter, 4).add(matterBlock).register();
		
		e.shaped().result(matterBlock)
				.shape("mm", "mm")
				.map('m', matter)
				.register();
		
		e.shaped().result(collector)
				.shape("gmg", "gcg", "ggg")
				.map('g', TagsHL.Items.STORAGE_BLOCKS_GLOWSTONE)
				.map('c', prevCollector)
				.map('m', matter)
				.register();
		
		e.shaped().result(relay)
				.shape("gmg", "gcg", "ggg")
				.map('g', Tags.Items.OBSIDIAN)
				.map('c', prevRelay)
				.map('m', matter)
				.register();
	}
	
	
	@Override
	public void provideRecipes(RegisterRecipesEvent e)
	{
		addFuel(e, ItemsEA.ZEITH_FUEL, BlocksEA.ZEITH_FUEL_BLOCK, PEItems.AETERNALIS_FUEL);
		addFuel(e, ItemsEA.MYSTERIUM_FUEL, BlocksEA.MYSTERIUM_FUEL_BLOCK, ItemsEA.ZEITH_FUEL);
		addFuel(e, ItemsEA.CITRINIUM_FUEL, BlocksEA.CITRINIUM_FUEL_BLOCK, ItemsEA.MYSTERIUM_FUEL);
		addFuel(e, ItemsEA.VERDANITE_FUEL, BlocksEA.VERDANITE_FUEL_BLOCK, ItemsEA.CITRINIUM_FUEL);
		
		addMatter(e, ItemsEA.BLUE_MATTER, BlocksEA.BLUE_MATTER_BLOCK, BlocksEA.COLLECTOR_MK4, BlocksEA.RELAY_MK4, PEBlocks.COLLECTOR_MK3, PEBlocks.RELAY_MK3);
		addMatter(e, ItemsEA.PURPLE_MATTER, BlocksEA.PURPLE_MATTER_BLOCK, BlocksEA.COLLECTOR_MK5, BlocksEA.RELAY_MK5, BlocksEA.COLLECTOR_MK4, BlocksEA.RELAY_MK4);
		addMatter(e, ItemsEA.ORANGE_MATTER, BlocksEA.ORANGE_MATTER_BLOCK, BlocksEA.COLLECTOR_MK6, BlocksEA.RELAY_MK6, BlocksEA.COLLECTOR_MK5, BlocksEA.RELAY_MK5);
		addMatter(e, ItemsEA.GREEN_MATTER, BlocksEA.GREEN_MATTER_BLOCK, BlocksEA.COLLECTOR_MK7, BlocksEA.RELAY_MK7, BlocksEA.COLLECTOR_MK6, BlocksEA.RELAY_MK6);
		
		e.shaped().result(ItemsEA.BLUE_MATTER)
				.shape("fff", "mmm", "fff")
				.map('f', ItemsEA.ZEITH_FUEL)
				.map('m', PEItems.RED_MATTER)
				.register();
		e.shaped().result(ItemsEA.BLUE_MATTER)
				.shape("fmf", "fmf", "fmf")
				.map('f', ItemsEA.ZEITH_FUEL)
				.map('m', PEItems.RED_MATTER)
				.register();
		
		e.shaped().result(ItemsEA.PURPLE_MATTER)
				.shape("fmf", "mfm", "fmf")
				.map('f', ItemsEA.MYSTERIUM_FUEL)
				.map('m', ItemsEA.BLUE_MATTER)
				.register();
		
		e.shaped().result(ItemsEA.ORANGE_MATTER)
				.shape("fmf", "mfm", "fmf")
				.map('f', ItemsEA.CITRINIUM_FUEL)
				.map('m', ItemsEA.PURPLE_MATTER)
				.register();
		
		e.shaped().result(ItemsEA.GREEN_MATTER)
				.shape("fmf", "mfm", "fmf")
				.map('m', ItemsEA.VERDANITE_FUEL)
				.map('f', ItemsEA.ORANGE_MATTER)
				.register();
		
		
		e.shaped().result(BlocksEA.EMC_PROXY)
				.shape("bkb", "kmk", "bkb")
				.map('b', PEBlocks.DARK_MATTER)
				.map('k', PEItems.KLEIN_STAR_SPHERE)
				.map('m', BlocksEA.BLUE_MATTER_BLOCK)
				.register();
		
		BEMCFlowersEA.MK1.registerRecipes(e, PEBlocks.COLLECTOR, PEBlocks.RELAY);
		BEMCFlowersEA.MK2.registerRecipes(e, PEBlocks.COLLECTOR_MK2, PEBlocks.RELAY_MK2);
		BEMCFlowersEA.MK3.registerRecipes(e, PEBlocks.COLLECTOR_MK3, PEBlocks.RELAY_MK3);
		BEMCFlowersEA.MK4.registerRecipes(e, BlocksEA.COLLECTOR_MK4, BlocksEA.RELAY_MK4);
		BEMCFlowersEA.MK5.registerRecipes(e, BlocksEA.COLLECTOR_MK5, BlocksEA.RELAY_MK5);
		BEMCFlowersEA.MK6.registerRecipes(e, BlocksEA.COLLECTOR_MK6, BlocksEA.RELAY_MK6);
		BEMCFlowersEA.MK7.registerRecipes(e, BlocksEA.COLLECTOR_MK7, BlocksEA.RELAY_MK7);
	}
}