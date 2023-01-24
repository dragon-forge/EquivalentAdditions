package org.zeith.equivadds.compat.ae2.client;

import appeng.api.client.AEStackRendering;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.StyleManager;
import appeng.init.client.InitScreens;
import appeng.items.storage.BasicStorageCell;
import appeng.menu.AEBaseMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.zeith.equivadds.compat.ae2.IInitializer;
import org.zeith.equivadds.compat.ae2.init.ItemsEAAE2;
import org.zeith.equivadds.compat.ae2.me.EMCKey;
import org.zeith.equivadds.compat.ae2.me.EMCKeyType;
import org.zeith.equivadds.compat.ae2.util.StyleManagerEA;

public class CompatAE2Client
		implements IInitializer
{
	public CompatAE2Client()
	{
	}
	
	@Override
	public void init(IEventBus bus)
	{
		bus.addListener((RegisterColorHandlersEvent.Item event) ->
		{
			for(var tier : ItemsEAAE2.Tier.values())
				event.register(BasicStorageCell::getColor, ItemsEAAE2.get(tier));
		});
		
		bus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() ->
		{
			AEStackRendering.register(EMCKeyType.TYPE, EMCKey.class, new EmcRenderer());
			StyleManagerEA.initialize(Minecraft.getInstance().getResourceManager());
		}));
		
		bus.addListener((TextureStitchEvent.Pre e) ->
		{
			e.addSprite(EmcRenderer.EMC_SPRITE);
		});
	}
	
	public static <M extends AEBaseMenu, U extends AEBaseScreen<M>> void register(MenuType<M> type,
																				  InitScreens.StyledScreenFactory<M, U> factory,
																				  String stylePath)
	{
		MenuScreens.<M, U> register(type, (menu, playerInv, title) ->
		{
			var style = StyleManager.loadStyleDoc(stylePath);
			
			return factory.create(menu, playerInv, title, style);
		});
	}
}