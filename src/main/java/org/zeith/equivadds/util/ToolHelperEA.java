package org.zeith.equivadds.util;

import moze_intel.projecte.gameObjs.blocks.IMatterBlock;
import net.minecraft.world.level.block.Block;
import org.zeith.equivadds.init.EnumMatterTypesEA;

public class ToolHelperEA
{
	public static float getDestroySpeed(float parentDestroySpeed, EnumMatterTypesEA matterType, int charge)
	{
		return parentDestroySpeed == 1.0F ? parentDestroySpeed : parentDestroySpeed + matterType.getChargeModifier() * (float) charge;
	}
	
	public static boolean canMatterMine(EnumMatterTypesEA matterType, Block block)
	{
		if(block instanceof IMatterBlock mb)
			return mb.getMatterType().getMatterTier() <= matterType.getMatterTier();
		return false;
	}
}