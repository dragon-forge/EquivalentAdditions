package org.zeith.equivadds.init;

import net.minecraft.world.item.Item;
import org.zeith.equivadds.items.tools.*;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;

@SimplyRegister
public interface ToolsEA
{
	@RegistryName("bm_pickaxe")
	ItemPickaxeEA BLUE_MATTER_PICKAXE = new ItemPickaxeEA(EnumMatterTypesEA.BLUE_MATTER, 4, newToolProperties());
	
	@RegistryName("bm_shovel")
	ItemShovelEA BLUE_MATTER_SHOVEL = new ItemShovelEA(EnumMatterTypesEA.BLUE_MATTER, 4, newToolProperties());
	
	@RegistryName("bm_hoe")
	ItemHoeEA BLUE_MATTER_HOE = new ItemHoeEA(EnumMatterTypesEA.BLUE_MATTER, 4, newToolProperties());
	
	@RegistryName("bm_sword")
	ItemSwordEA BLUE_MATTER_SWORD = new ItemSwordEA(EnumMatterTypesEA.BLUE_MATTER, 4, 16, newToolProperties());
	
	static Item.Properties newToolProperties()
	{
		return ItemsEA.newProperties().stacksTo(1).fireResistant();
	}
}