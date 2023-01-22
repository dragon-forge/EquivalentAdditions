package org.zeith.equivadds.init;

import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import org.zeith.equivadds.api.EmcFlower;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;

@SimplyRegister(prefix = "emc_flowers/")
public interface BEMCFlowersEA
{
	@RegistryName("mk1")
	EmcFlower MK1 = new EmcFlower("mk1", EmcFlower.FlowerProperties.ofVanilla(EnumCollectorTier.MK1, EnumRelayTier.MK1), ItemsEA::newItem);
	
	@RegistryName("mk2")
	EmcFlower MK2 = new EmcFlower("mk2", EmcFlower.FlowerProperties.ofVanilla(EnumCollectorTier.MK2, EnumRelayTier.MK2), ItemsEA::newItem);
	
	@RegistryName("mk3")
	EmcFlower MK3 = new EmcFlower("mk3", EmcFlower.FlowerProperties.ofVanilla(EnumCollectorTier.MK3, EnumRelayTier.MK3), ItemsEA::newItem);
	
	@RegistryName("mk4")
	EmcFlower MK4 = new EmcFlower("mk4", EmcFlower.FlowerProperties.ofCustom(EnumCollectorTiersEA.MK4, EnumRelayTiersEA.MK4), ItemsEA::newItem);
	
	@RegistryName("mk5")
	EmcFlower MK5 = new EmcFlower("mk5", EmcFlower.FlowerProperties.ofCustom(EnumCollectorTiersEA.MK5, EnumRelayTiersEA.MK5), ItemsEA::newItem);
	
	@RegistryName("mk6")
	EmcFlower MK6 = new EmcFlower("mk6", EmcFlower.FlowerProperties.ofCustom(EnumCollectorTiersEA.MK6, EnumRelayTiersEA.MK6), ItemsEA::newItem);
	
	@RegistryName("mk7")
	EmcFlower MK7 = new EmcFlower("mk7", EmcFlower.FlowerProperties.ofCustom(EnumCollectorTiersEA.MK7, EnumRelayTiersEA.MK7), ItemsEA::newItem);
}