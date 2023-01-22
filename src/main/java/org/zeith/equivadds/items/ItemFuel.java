package org.zeith.equivadds.items;

import moze_intel.projecte.gameObjs.EnumFuelType;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.core.adapter.TagAdapter;

public class ItemFuel
		extends Item
{
	public static final int ZEITH_FUEL_BURN_TIME = EnumFuelType.AETERNALIS_FUEL.getBurnTime() * 4;
	public static final int MYSTERIUM_FUEL_BURN_TIME = ZEITH_FUEL_BURN_TIME * 4;
	public static final int CITRINIUM_FUEL_BURN_TIME = MYSTERIUM_FUEL_BURN_TIME * 4;
	public static final int VERDANITE_FUEL_BURN_TIME = CITRINIUM_FUEL_BURN_TIME * 4;
	
	public final int burnTime;
	
	public ItemFuel(Properties props, int burnTime)
	{
		super(props);
		this.burnTime = burnTime;
		TagAdapter.bind(PETags.Items.COLLECTOR_FUEL, this);
	}
	
	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType)
	{
		return burnTime;
	}
}