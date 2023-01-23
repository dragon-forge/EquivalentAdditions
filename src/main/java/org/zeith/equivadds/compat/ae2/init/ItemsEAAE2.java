package org.zeith.equivadds.compat.ae2.init;

import appeng.core.definitions.AEItems;
import net.minecraft.world.item.Item;
import org.zeith.equivadds.compat.ae2.crafting.pattern.ItemEMCSynthesisPattern;
import org.zeith.equivadds.compat.ae2.item.EmcCellItem;
import org.zeith.equivadds.init.ItemsEA;
import org.zeith.hammerlib.annotations.RegistryName;

public interface ItemsEAAE2
{
	@RegistryName("emc_cell_housing")
	Item EMC_CELL_HOUSING = ItemsEA.newItem();
	
	@RegistryName("emc_storage_cell_1k")
	EmcCellItem EMC_CELL_1K = new EmcCellItem(ItemsEA.newProperties().stacksTo(1), AEItems.CELL_COMPONENT_1K, 1, 0.5);
	
	@RegistryName("emc_storage_cell_4k")
	EmcCellItem EMC_CELL_4K = new EmcCellItem(ItemsEA.newProperties().stacksTo(1), AEItems.CELL_COMPONENT_4K, 4, 1);
	
	@RegistryName("emc_storage_cell_16k")
	EmcCellItem EMC_CELL_16K = new EmcCellItem(ItemsEA.newProperties().stacksTo(1), AEItems.CELL_COMPONENT_16K, 16, 1.5);
	
	@RegistryName("emc_storage_cell_64k")
	EmcCellItem EMC_CELL_64K = new EmcCellItem(ItemsEA.newProperties().stacksTo(1), AEItems.CELL_COMPONENT_64K, 64, 2);
	
	@RegistryName("emc_storage_cell_256k")
	EmcCellItem EMC_CELL_256K = new EmcCellItem(ItemsEA.newProperties().stacksTo(1), AEItems.CELL_COMPONENT_256K, 256, 2.5);
	
	@RegistryName("emc_synthesis_pattern")
	ItemEMCSynthesisPattern EMC_SYNTHESIS_PATTERN = new ItemEMCSynthesisPattern(ItemsEA.newProperties().stacksTo(1));
	
	static EmcCellItem get(Tier tier)
	{
		return switch(tier)
				{
					case _1K -> EMC_CELL_1K;
					case _4K -> EMC_CELL_4K;
					case _16K -> EMC_CELL_16K;
					case _64K -> EMC_CELL_64K;
					case _256K -> EMC_CELL_256K;
				};
	}
	
	enum Tier
	{
		_1K, _4K, _16K, _64K, _256K
	}
}