package org.zeith.equivadds.util.spoof;

import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ContainerTypeRegistryObjectSpoof<CONTAINER extends AbstractContainerMenu>
		extends ContainerTypeRegistryObject<CONTAINER>
{
	public final MenuType<CONTAINER> type;
	
	public ContainerTypeRegistryObjectSpoof(MenuType<CONTAINER> type)
	{
		super(null);
		this.type = type;
	}
	
	@Override
	public @Nonnull MenuType<CONTAINER> get()
	{
		return type;
	}
	
	@Override
	public String getInternalRegistryName()
	{
		return Objects.toString(ForgeRegistries.MENU_TYPES.getKey(type));
	}
}
