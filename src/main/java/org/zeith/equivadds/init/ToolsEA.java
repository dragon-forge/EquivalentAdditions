package org.zeith.equivadds.init;

import net.minecraft.world.item.Item;
import org.zeith.equivadds.items.tools.*;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;

@SimplyRegister(prefix = "tools/")
public interface ToolsEA
{
	int BM_CHARGES = 4;
	int PM_CHARGES = 5;
	int OM_CHARGES = 6;
	int GM_CHARGES = 7;
	
	// BLUE MATTER //
	
	@RegistryName("bm/pickaxe")
	ItemPickaxeEA BLUE_MATTER_PICKAXE = new ItemPickaxeEA(EnumMatterTypesEA.BLUE_MATTER, BM_CHARGES, newToolProperties());
	
	@RegistryName("bm/axe")
	ItemAxeEA BLUE_MATTER_AXE = new ItemAxeEA(EnumMatterTypesEA.BLUE_MATTER, BM_CHARGES, newToolProperties());
	
	@RegistryName("bm/shovel")
	ItemShovelEA BLUE_MATTER_SHOVEL = new ItemShovelEA(EnumMatterTypesEA.BLUE_MATTER, BM_CHARGES, newToolProperties());
	
	@RegistryName("bm/sword")
	ItemSwordEA BLUE_MATTER_SWORD = new ItemSwordEA(EnumMatterTypesEA.BLUE_MATTER, BM_CHARGES, 16, newToolProperties());
	
	@RegistryName("bm/hoe")
	ItemHoeEA BLUE_MATTER_HOE = new ItemHoeEA(EnumMatterTypesEA.BLUE_MATTER, BM_CHARGES, newToolProperties());
	
	@RegistryName("bm/shears")
	ItemShearsEA BLUE_MATTER_SHEARS = new ItemShearsEA(EnumMatterTypesEA.BLUE_MATTER, BM_CHARGES, newToolProperties());
	
	@RegistryName("bm/hammer")
	ItemHammerEA BLUE_MATTER_HAMMER = new ItemHammerEA(EnumMatterTypesEA.BLUE_MATTER, BM_CHARGES, newToolProperties());
	
	@RegistryName("bm/katar")
	ItemKatarEA BLUE_MATTER_KATAR = new ItemKatarEA(EnumMatterTypesEA.BLUE_MATTER, BM_CHARGES, newToolProperties());
	
	@RegistryName("bm/morning_star")
	ItemMorningStarEA BLUE_MATTER_MORNING_STAR = new ItemMorningStarEA(EnumMatterTypesEA.BLUE_MATTER, BM_CHARGES, newToolProperties());
	
	// PURPLE MATTER //
	
	@RegistryName("pm/pickaxe")
	ItemPickaxeEA PURPLE_MATTER_PICKAXE = new ItemPickaxeEA(EnumMatterTypesEA.PURPLE_MATTER, PM_CHARGES, newToolProperties());
	
	@RegistryName("pm/axe")
	ItemAxeEA PURPLE_MATTER_AXE = new ItemAxeEA(EnumMatterTypesEA.PURPLE_MATTER, PM_CHARGES, newToolProperties());
	
	@RegistryName("pm/shovel")
	ItemShovelEA PURPLE_MATTER_SHOVEL = new ItemShovelEA(EnumMatterTypesEA.PURPLE_MATTER, PM_CHARGES, newToolProperties());
	
	@RegistryName("pm/sword")
	ItemSwordEA PURPLE_MATTER_SWORD = new ItemSwordEA(EnumMatterTypesEA.PURPLE_MATTER, PM_CHARGES, 20, newToolProperties());
	
	@RegistryName("pm/hoe")
	ItemHoeEA PURPLE_MATTER_HOE = new ItemHoeEA(EnumMatterTypesEA.PURPLE_MATTER, PM_CHARGES, newToolProperties());
	
	@RegistryName("pm/shears")
	ItemShearsEA PURPLE_MATTER_SHEARS = new ItemShearsEA(EnumMatterTypesEA.PURPLE_MATTER, PM_CHARGES, newToolProperties());
	
	@RegistryName("pm/hammer")
	ItemHammerEA PURPLE_MATTER_HAMMER = new ItemHammerEA(EnumMatterTypesEA.PURPLE_MATTER, PM_CHARGES, newToolProperties());
	
	@RegistryName("pm/katar")
	ItemKatarEA PURPLE_MATTER_KATAR = new ItemKatarEA(EnumMatterTypesEA.PURPLE_MATTER, PM_CHARGES, newToolProperties());
	
	@RegistryName("pm/morning_star")
	ItemMorningStarEA PURPLE_MATTER_MORNING_STAR = new ItemMorningStarEA(EnumMatterTypesEA.PURPLE_MATTER, PM_CHARGES, newToolProperties());
	
	// ORANGE MATTER //
	
	@RegistryName("om/pickaxe")
	ItemPickaxeEA ORANGE_MATTER_PICKAXE = new ItemPickaxeEA(EnumMatterTypesEA.ORANGE_MATTER, OM_CHARGES, newToolProperties());
	
	@RegistryName("om/axe")
	ItemAxeEA ORANGE_MATTER_AXE = new ItemAxeEA(EnumMatterTypesEA.ORANGE_MATTER, OM_CHARGES, newToolProperties());
	
	@RegistryName("om/shovel")
	ItemShovelEA ORANGE_MATTER_SHOVEL = new ItemShovelEA(EnumMatterTypesEA.ORANGE_MATTER, OM_CHARGES, newToolProperties());
	
	@RegistryName("om/sword")
	ItemSwordEA ORANGE_MATTER_SWORD = new ItemSwordEA(EnumMatterTypesEA.ORANGE_MATTER, OM_CHARGES, 20, newToolProperties());
	
	@RegistryName("om/hoe")
	ItemHoeEA ORANGE_MATTER_HOE = new ItemHoeEA(EnumMatterTypesEA.ORANGE_MATTER, OM_CHARGES, newToolProperties());
	
	@RegistryName("om/shears")
	ItemShearsEA ORANGE_MATTER_SHEARS = new ItemShearsEA(EnumMatterTypesEA.ORANGE_MATTER, OM_CHARGES, newToolProperties());
	
	@RegistryName("om/hammer")
	ItemHammerEA ORANGE_MATTER_HAMMER = new ItemHammerEA(EnumMatterTypesEA.ORANGE_MATTER, OM_CHARGES, newToolProperties());
	
	@RegistryName("om/katar")
	ItemKatarEA ORANGE_MATTER_KATAR = new ItemKatarEA(EnumMatterTypesEA.ORANGE_MATTER, OM_CHARGES, newToolProperties());
	
	@RegistryName("om/morning_star")
	ItemMorningStarEA ORANGE_MATTER_MORNING_STAR = new ItemMorningStarEA(EnumMatterTypesEA.ORANGE_MATTER, OM_CHARGES, newToolProperties());
	
	// GREEN MATTER //
	
	@RegistryName("gm/pickaxe")
	ItemPickaxeEA GREEN_MATTER_PICKAXE = new ItemPickaxeEA(EnumMatterTypesEA.GREEN_MATTER, GM_CHARGES, newToolProperties());
	
	@RegistryName("gm/axe")
	ItemAxeEA GREEN_MATTER_AXE = new ItemAxeEA(EnumMatterTypesEA.GREEN_MATTER, GM_CHARGES, newToolProperties());
	
	@RegistryName("gm/shovel")
	ItemShovelEA GREEN_MATTER_SHOVEL = new ItemShovelEA(EnumMatterTypesEA.GREEN_MATTER, GM_CHARGES, newToolProperties());
	
	@RegistryName("gm/sword")
	ItemSwordEA GREEN_MATTER_SWORD = new ItemSwordEA(EnumMatterTypesEA.GREEN_MATTER, GM_CHARGES, 20, newToolProperties());
	
	@RegistryName("gm/hoe")
	ItemHoeEA GREEN_MATTER_HOE = new ItemHoeEA(EnumMatterTypesEA.GREEN_MATTER, GM_CHARGES, newToolProperties());
	
	@RegistryName("gm/shears")
	ItemShearsEA GREEN_MATTER_SHEARS = new ItemShearsEA(EnumMatterTypesEA.GREEN_MATTER, GM_CHARGES, newToolProperties());
	
	@RegistryName("gm/hammer")
	ItemHammerEA GREEN_MATTER_HAMMER = new ItemHammerEA(EnumMatterTypesEA.GREEN_MATTER, GM_CHARGES, newToolProperties());
	
	@RegistryName("gm/katar")
	ItemKatarEA GREEN_MATTER_KATAR = new ItemKatarEA(EnumMatterTypesEA.GREEN_MATTER, GM_CHARGES, newToolProperties());
	
	@RegistryName("gm/morning_star")
	ItemMorningStarEA GREEN_MATTER_MORNING_STAR = new ItemMorningStarEA(EnumMatterTypesEA.GREEN_MATTER, GM_CHARGES, newToolProperties());
	
	static Item.Properties newToolProperties()
	{
		return ItemsEA.newProperties().stacksTo(1).fireResistant();
	}
}