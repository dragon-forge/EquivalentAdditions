package org.zeith.equivadds.util;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class EMCHelper
{
	static final Direction[] DIRS = Direction.values();
	
	public static long pullEMC(long needed, Level level, BlockPos pos, IEmcStorage.EmcAction action)
	{
		long got = 0L;
		
		for(Direction dir : DIRS)
		{
			var be = level.getBlockEntity(pos.relative(dir));
			if(be == null) continue;
			
			var cap = be.getCapability(PECapabilities.EMC_STORAGE_CAPABILITY, dir.getOpposite());
			if(!cap.isPresent()) continue;
			
			var storage = cap.resolve().orElseThrow();
			
			long delta = storage.extractEmc(needed, action);
			got += delta;
			needed -= delta;
			
			if(needed <= 0)
				return got;
		}
		
		return got;
	}
}