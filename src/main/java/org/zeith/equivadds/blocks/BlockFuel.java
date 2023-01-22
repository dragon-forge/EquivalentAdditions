package org.zeith.equivadds.blocks;

import com.google.common.base.Suppliers;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import org.zeith.equivadds.init.ItemsEA;
import org.zeith.equivadds.items.ItemFuel;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.core.adapter.TagAdapter;

import java.util.function.Supplier;

public class BlockFuel
		extends SimpleBlockEA
		implements ICustomBlockItem
{
	public final Supplier<ItemFuel> fuel;
	
	public BlockFuel(Properties props, Supplier<ItemFuel> fuel)
	{
		super(props, BlockHarvestAdapter.MineableType.PICKAXE, Tiers.IRON);
		this.fuel = fuel;
	}
	
	@Override
	public BlockItem createBlockItem()
	{
		Supplier<Integer> burnTime = Suppliers.memoize(() -> fuel.get().burnTime * 9);
		var bi = new BlockItem(this, ItemsEA.newProperties())
		{
			@Override
			public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType)
			{
				return burnTime.get();
			}
		};
		TagAdapter.bind(PETags.Items.COLLECTOR_FUEL, bi);
		return bi;
	}
}