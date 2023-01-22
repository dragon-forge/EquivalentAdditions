package org.zeith.equivadds.proxy;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegisterEvent;
import org.zeith.equivadds.container.gui.GuiCustomCollector;
import org.zeith.equivadds.container.gui.GuiCustomRelay;
import org.zeith.equivadds.init.ContainerTypesEA;

public class ClientProxyEA
		extends CommonProxyEA
{
	@Override
	public void construct(IEventBus modBus)
	{
		super.construct(modBus);
		modBus.addListener(this::registerContainers);
	}
	
	private void registerContainers(RegisterEvent event)
	{
		event.register(Registry.MENU_REGISTRY, helper ->
		{
			registerScreen(ContainerTypesEA.COLLECTOR_CONTAINER.get(), GuiCustomCollector.Baseline::new);
			registerScreen(ContainerTypesEA.RELAY_CONTAINER.get(), GuiCustomRelay.Baseline::new);
		});
	}
	
	private static <C extends AbstractContainerMenu, U extends Screen & MenuAccess<C>> void registerScreen(MenuType<C> type, MenuScreens.ScreenConstructor<C, U> factory)
	{
		MenuScreens.register(type, factory);
	}
}