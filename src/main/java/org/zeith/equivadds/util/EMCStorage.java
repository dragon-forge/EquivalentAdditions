package org.zeith.equivadds.util;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import org.jetbrains.annotations.Range;
import org.zeith.hammerlib.api.io.IAutoNBTSerializable;
import org.zeith.hammerlib.api.io.NBTSerializable;

public class EMCStorage
		implements IEmcStorage, IAutoNBTSerializable
{
	@NBTSerializable
	public long emc;
	
	public final long maxEMC;
	
	public Runnable onEmcChanged = () ->
	{
	};
	
	public EMCStorage()
	{
		this(Long.MAX_VALUE);
	}
	
	public EMCStorage(long maxEMC)
	{
		this.maxEMC = maxEMC;
	}
	
	@Override
	public @Range(from = 0L, to = Long.MAX_VALUE) long getStoredEmc()
	{
		return emc;
	}
	
	@Override
	public @Range(from = 1L, to = Long.MAX_VALUE) long getMaximumEmc()
	{
		return maxEMC;
	}
	
	@Override
	public long extractEmc(long toExtract, EmcAction action)
	{
		long toRemove = Math.min(this.getStoredEmc(), toExtract);
		if(action.execute())
		{
			this.emc -= toRemove;
			storedEmcChanged();
		}
		return toRemove;
	}
	
	@Override
	public long insertEmc(long toAccept, EmcAction action)
	{
		long toAdd = Math.min(this.getNeededEmc(), toAccept);
		if(action.execute())
		{
			this.emc += toAdd;
			storedEmcChanged();
		}
		return toAdd;
	}
	
	public void storedEmcChanged()
	{
		onEmcChanged.run();
	}
}