package org.zeith.equivadds.blocks.conduit;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;

public class EmcConduitContents
		implements INBTSerializable<ListTag>, IEmcStorage
{
	public final double[] energy = new double[6];
	
	public long max;
	
	public void add(Direction to, float fe)
	{
		energy[to.ordinal()] += fe;
	}
	
	public long emit(TileEmcConduit conduit)
	{
		max = conduit.properties.transfer();
		
		long emit = 0;
		
		for(Direction dir : BlockConduit.DIRECTIONS)
		{
			var fe = energy[dir.ordinal()];
			if(fe >= 1.0D)
			{
				long sent = conduit.emitToDirect(dir, (long) fe, EmcAction.EXECUTE);
				fe -= sent;
				emit += sent;
				energy[dir.ordinal()] = fe;
			}
		}
		
		return emit;
	}
	
	@Override
	public ListTag serializeNBT()
	{
		var lst = new ListTag();
		for(double i : energy) lst.add(DoubleTag.valueOf(i));
		return lst;
	}
	
	@Override
	public void deserializeNBT(ListTag nbt)
	{
		var l = Math.min(nbt.size(), energy.length);
		for(int i = 0; i < l; ++i)
			energy[i] = nbt.getFloat(i);
	}
	
	@Override
	public long insertEmc(long maxReceive, EmcAction simulate)
	{
		return 0;
	}
	
	@Override
	public long extractEmc(long maxExtract, EmcAction simulate)
	{
		int extracted = 0;
		for(int i = 0; i < energy.length && maxExtract > 0; i++)
		{
			if(energy[i] > 0)
			{
				var te = Math.min(maxExtract, energy[i]);
				
				extracted += te;
				maxExtract -= te;
				
				if(simulate.execute())
					energy[i] -= te;
			}
		}
		return extracted;
	}
	
	@Override
	public long getStoredEmc()
	{
		return (int) (energy[0] + energy[1] + energy[2] + energy[3] + energy[4] + energy[5]);
	}
	
	@Override
	public long getMaximumEmc()
	{
		return max * 6;
	}
}