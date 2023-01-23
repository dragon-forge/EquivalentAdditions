package org.zeith.equivadds.init;

import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import org.zeith.equivadds.items.tools.*;
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
		e.shapeless().result(prevTierFuelItem, 4).addAll(PEItems.PHILOSOPHERS_STONE, fuelItem).register();
		e.shapeless().result(fuelItem).add(PEItems.PHILOSOPHERS_STONE).addAll(fuels).register();
		e.shapeless().result(fuelItem, 9).add(fuelBlock).register();
		e.shaped().result(fuelBlock).shape("fff", "fff", "fff").map('f', fuelItem).register();
	}
	
	private static void addConduit(RegisterRecipesEvent e, ItemLike conduit, ItemLike fuel, Object matter)
	{
		e.shaped().result(conduit, 8)
				.shape("ggg", "fmf", "ggg")
				.map('g', TagsHL.Items.STORAGE_BLOCKS_GLOWSTONE)
				.map('f', fuel)
				.map('m', matter)
				.register();
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
		
		addConduit(e, BlocksEA.CONDUIT_MK1, PEItems.ALCHEMICAL_COAL, Tags.Items.STORAGE_BLOCKS_DIAMOND);
		addConduit(e, BlocksEA.CONDUIT_MK2, PEItems.MOBIUS_FUEL, PEItems.DARK_MATTER);
		addConduit(e, BlocksEA.CONDUIT_MK3, PEItems.AETERNALIS_FUEL, PEItems.RED_MATTER);
		addConduit(e, BlocksEA.CONDUIT_MK4, ItemsEA.ZEITH_FUEL, ItemsEA.BLUE_MATTER);
		addConduit(e, BlocksEA.CONDUIT_MK5, ItemsEA.MYSTERIUM_FUEL, ItemsEA.PURPLE_MATTER);
		addConduit(e, BlocksEA.CONDUIT_MK6, ItemsEA.CITRINIUM_FUEL, ItemsEA.ORANGE_MATTER);
		addConduit(e, BlocksEA.CONDUIT_MK7, ItemsEA.VERDANITE_FUEL, ItemsEA.GREEN_MATTER);
		
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
		
		BEMCFlowersEA.MK1.registerRecipes(e, Items.DIAMOND_BLOCK, PEBlocks.COLLECTOR, PEBlocks.RELAY);
		BEMCFlowersEA.MK2.registerRecipes(e, PEItems.DARK_MATTER, PEBlocks.COLLECTOR_MK2, PEBlocks.RELAY_MK2);
		BEMCFlowersEA.MK3.registerRecipes(e, PEItems.RED_MATTER, PEBlocks.COLLECTOR_MK3, PEBlocks.RELAY_MK3);
		BEMCFlowersEA.MK4.registerRecipes(e, ItemsEA.BLUE_MATTER, BlocksEA.COLLECTOR_MK4, BlocksEA.RELAY_MK4);
		BEMCFlowersEA.MK5.registerRecipes(e, ItemsEA.PURPLE_MATTER, BlocksEA.COLLECTOR_MK5, BlocksEA.RELAY_MK5);
		BEMCFlowersEA.MK6.registerRecipes(e, ItemsEA.ORANGE_MATTER, BlocksEA.COLLECTOR_MK6, BlocksEA.RELAY_MK6);
		BEMCFlowersEA.MK7.registerRecipes(e, ItemsEA.GREEN_MATTER, BlocksEA.COLLECTOR_MK7, BlocksEA.RELAY_MK7);
		
		
		// This part of monstrocity makes tier upgrading a simple two-liner.
		new MatterToolRecipes(new Up<>(ItemsEA.BLUE_MATTER, PEItems.RED_MATTER),
				new Up<>(ToolsEA.BLUE_MATTER_PICKAXE, PEItems.RED_MATTER_PICKAXE),
				new Up<>(ToolsEA.BLUE_MATTER_AXE, PEItems.RED_MATTER_AXE),
				new Up<>(ToolsEA.BLUE_MATTER_SHOVEL, PEItems.RED_MATTER_SHOVEL),
				new Up<>(ToolsEA.BLUE_MATTER_SWORD, PEItems.RED_MATTER_SWORD),
				new Up<>(ToolsEA.BLUE_MATTER_HOE, PEItems.RED_MATTER_HOE),
				new Up<>(ToolsEA.BLUE_MATTER_SHEARS, PEItems.RED_MATTER_SHEARS),
				new Up<>(ToolsEA.BLUE_MATTER_HAMMER, PEItems.RED_MATTER_HAMMER),
				new Up<>(ToolsEA.BLUE_MATTER_KATAR, PEItems.RED_MATTER_KATAR),
				new Up<>(ToolsEA.BLUE_MATTER_MORNING_STAR, PEItems.RED_MATTER_MORNING_STAR)
		)
				.register(e) // blue matter
				
				.upgrade(ItemsEA.PURPLE_MATTER, ToolsEA.PURPLE_MATTER_PICKAXE, ToolsEA.PURPLE_MATTER_AXE, ToolsEA.PURPLE_MATTER_SHOVEL, ToolsEA.PURPLE_MATTER_SWORD, ToolsEA.PURPLE_MATTER_HOE, ToolsEA.PURPLE_MATTER_SHEARS, ToolsEA.PURPLE_MATTER_HAMMER, ToolsEA.PURPLE_MATTER_KATAR, ToolsEA.PURPLE_MATTER_MORNING_STAR)
				.register(e) // purple matter
				
				.upgrade(ItemsEA.ORANGE_MATTER, ToolsEA.ORANGE_MATTER_PICKAXE, ToolsEA.ORANGE_MATTER_AXE, ToolsEA.ORANGE_MATTER_SHOVEL, ToolsEA.ORANGE_MATTER_SWORD, ToolsEA.ORANGE_MATTER_HOE, ToolsEA.ORANGE_MATTER_SHEARS, ToolsEA.ORANGE_MATTER_HAMMER, ToolsEA.ORANGE_MATTER_KATAR, ToolsEA.ORANGE_MATTER_MORNING_STAR)
				.register(e) // orange matter
				
				.upgrade(ItemsEA.GREEN_MATTER, ToolsEA.GREEN_MATTER_PICKAXE, ToolsEA.GREEN_MATTER_AXE, ToolsEA.GREEN_MATTER_SHOVEL, ToolsEA.GREEN_MATTER_SWORD, ToolsEA.GREEN_MATTER_HOE, ToolsEA.GREEN_MATTER_SHEARS, ToolsEA.GREEN_MATTER_HAMMER, ToolsEA.GREEN_MATTER_KATAR, ToolsEA.GREEN_MATTER_MORNING_STAR)
				.register(e) // green matter
		;
	}
	
	public record Up<T extends ItemLike>(T upgraded, ItemLike prev)
	{
		public Up<T> up(T next)
		{
			return new Up<>(next, upgraded);
		}
	}
	
	public record MatterToolRecipes(Up<ItemLike> matter, Up<ItemPickaxeEA> pickaxe, Up<ItemAxeEA> axe, Up<ItemShovelEA> shovel, Up<ItemSwordEA> sword, Up<ItemHoeEA> hoe, Up<ItemShearsEA> shears, Up<ItemHammerEA> hammer,
									Up<ItemKatarEA> katar, Up<ItemMorningStarEA> morningStar)
	{
		public MatterToolRecipes upgrade(ItemLike nextMatter, ItemPickaxeEA nextPickaxe, ItemAxeEA nextAxe, ItemShovelEA nextShovel, ItemSwordEA nextSword, ItemHoeEA nextHoe, ItemShearsEA nextShears, ItemHammerEA nextHammer, ItemKatarEA nextKatar, ItemMorningStarEA nextMorningStar)
		{
			return new MatterToolRecipes(matter.up(nextMatter), pickaxe.up(nextPickaxe), axe.up(nextAxe), shovel.up(nextShovel), sword.up(nextSword), hoe.up(nextHoe), shears.up(nextShears), hammer.up(nextHammer), katar.up(nextKatar), morningStar.up(nextMorningStar));
		}
		
		public MatterToolRecipes register(RegisterRecipesEvent e)
		{
			e.shaped().result(pickaxe.upgraded())
					.shape("bbb", " p ", " m ")
					.map('b', matter.upgraded())
					.map('p', pickaxe.prev())
					.map('m', matter.prev())
					.register();
			
			e.shaped().result(shovel.upgraded())
					.shape("b", "p", "m")
					.map('b', matter.upgraded())
					.map('p', shovel.prev())
					.map('m', matter.prev())
					.register();
			
			e.shaped().result(hoe.upgraded())
					.shape("bb", " p", " m")
					.map('b', matter.upgraded())
					.map('p', hoe.prev())
					.map('m', matter.prev())
					.register();
			
			e.shaped().result(axe.upgraded())
					.shape("bb", "bp", " m")
					.map('b', matter.upgraded())
					.map('p', axe.prev())
					.map('m', matter.prev())
					.register();
			
			e.shaped().result(sword.upgraded())
					.shape("b", "b", "p")
					.map('b', matter.upgraded())
					.map('p', sword.prev())
					.register();
			
			e.shaped().result(shears.upgraded())
					.shape(" b", "p ")
					.map('b', matter.upgraded())
					.map('p', shears.prev())
					.register();
			
			e.shaped().result(hammer.upgraded())
					.shape("bmb", " p ", " m ")
					.map('b', matter.upgraded())
					.map('p', hammer.prev())
					.map('m', matter.prev())
					.register();
			
			e.shaped().result(katar.upgraded())
					.shape("saw", "hbb", "bbb")
					.map('s', shears.upgraded())
					.map('a', axe.upgraded())
					.map('w', sword.upgraded())
					.map('h', hoe.upgraded())
					.map('b', matter.upgraded())
					.register();
			
			e.shaped().result(morningStar.upgraded())
					.shape("hps", "bbb", "bbb")
					.map('h', hammer.upgraded())
					.map('p', pickaxe.upgraded())
					.map('s', shovel.upgraded())
					.map('b', matter.upgraded())
					.register();
			
			return this;
		}
	}
}