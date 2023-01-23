package org.zeith.equivadds.compat.ae2.client;

import appeng.api.client.AEStackRendering;
import appeng.items.storage.BasicStorageCell;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.zeith.equivadds.compat.ae2.init.ItemsEAAE2;
import org.zeith.equivadds.compat.ae2.me.EMCKey;
import org.zeith.equivadds.compat.ae2.me.EMCKeyType;

public interface CompatAE2Client
{
	static void init()
	{
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		bus.addListener((RegisterColorHandlersEvent.Item event) ->
		{
			for(var tier : ItemsEAAE2.Tier.values())
				event.register(BasicStorageCell::getColor, ItemsEAAE2.get(tier));
		});
		
		bus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() ->
		{
			AEStackRendering.register(EMCKeyType.TYPE, EMCKey.class, new EmcRenderer());
		}));
		
		bus.addListener((TextureStitchEvent.Pre e) ->
		{
			e.addSprite(EmcRenderer.EMC_SPRITE);
		});
	}
}