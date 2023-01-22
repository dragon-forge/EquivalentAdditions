package org.zeith.equivadds.init;

import net.minecraft.world.item.Item;
import org.zeith.equivadds.items.ItemFuel;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;

import static org.zeith.equivadds.EquivalentAdditions.TAB;

@SimplyRegister
public interface ItemsEA
{
	@RegistryName("blue_matter")
	Item BLUE_MATTER = newItem();
	
	@RegistryName("purple_matter")
	Item PURPLE_MATTER = newItem();
	
	@RegistryName("orange_matter")
	Item ORANGE_MATTER = newItem();
	
	@RegistryName("green_matter")
	Item GREEN_MATTER = newItem();
	
	@RegistryName("zeitheron_fuel")
	ItemFuel ZEITH_FUEL = new ItemFuel(newProperties(), ItemFuel.ZEITH_FUEL_BURN_TIME);
	
	@RegistryName("mysterium_fuel")
	ItemFuel MYSTERIUM_FUEL = new ItemFuel(newProperties(), ItemFuel.MYSTERIUM_FUEL_BURN_TIME);
	
	@RegistryName("citrinium_fuel")
	ItemFuel CITRINIUM_FUEL = new ItemFuel(newProperties(), ItemFuel.CITRINIUM_FUEL_BURN_TIME);
	
	@RegistryName("verdanite_fuel")
	ItemFuel VERDANITE_FUEL = new ItemFuel(newProperties(), ItemFuel.VERDANITE_FUEL_BURN_TIME);
	
	static Item.Properties newProperties()
	{
		return new Item.Properties().tab(TAB);
	}
	
	static Item newItem()
	{
		return new Item(newProperties());
	}
}