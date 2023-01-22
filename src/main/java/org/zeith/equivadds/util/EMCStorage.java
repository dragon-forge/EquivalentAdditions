package org.zeith.equivadds.util;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import org.jetbrains.annotations.Range;
import org.zeith.hammerlib.api.io.IAutoNBTSerializable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyLong;
import org.zeith.hammerlib.util.java.DirectStorage;

public class EMCStorage
		implements IEmcStorage, IAutoNBTSerializable
{
	@NBTSerializable
	public long emc;
	
	public final long maxEMC;
	
	public EMCStorage()
	{
		this(Long.MAX_VALUE);
	}
	
	public EMCStorage(long maxEMC)
	{
		this.maxEMC = maxEMC;
	}
	
	public final PropertyLong emcSync = new PropertyLong(DirectStorage.create(v -> emc = v, () -> emc));
	
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
			this.storedEmcChanged();
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
			this.storedEmcChanged();
		}
		return toAdd;
	}
	
	public void storedEmcChanged()
	{
		emcSync.markChanged(true);
	}
}