package org.zeith.equivadds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.equivadds.init.ItemsEA;
import org.zeith.equivadds.proxy.ClientProxyEA;
import org.zeith.equivadds.proxy.CommonProxyEA;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.event.fml.FMLFingerprintCheckEvent;
import org.zeith.hammerlib.util.CommonMessages;

@Mod(EquivalentAdditions.MOD_ID)
public class EquivalentAdditions
{
	public static final String MOD_ID = "equivadds";
	public static final Logger LOG = LogManager.getLogger("EquivalentAdditions");
	
	public static final CommonProxyEA PROXY = DistExecutor.unsafeRunForDist(() -> ClientProxyEA::new, () -> CommonProxyEA::new);
	
	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID)
	{
		@Override
		public ItemStack makeIcon()
		{
			return new ItemStack(ItemsEA.BLUE_MATTER);
		}
	};
	
	public EquivalentAdditions()
	{
		CommonMessages.printMessageOnIllegalRedistribution(EquivalentAdditions.class,
				LOG, "Equivalent Additions", "https://www.curseforge.com/minecraft/mc-mods/equivalent-additions");
		LanguageAdapter.registerMod(MOD_ID);
		
		var modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener(this::checkFingerprint);
		PROXY.construct(modBus);
	}
	
	private void checkFingerprint(FMLFingerprintCheckEvent e)
	{
		CommonMessages.printMessageOnFingerprintViolation(e, "97e852e9b3f01b83574e8315f7e77651c6605f2b455919a7319e9869564f013c",
				LOG, "Equivalent Additions", "https://www.curseforge.com/minecraft/mc-mods/equivalent-additions");
	}
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
}