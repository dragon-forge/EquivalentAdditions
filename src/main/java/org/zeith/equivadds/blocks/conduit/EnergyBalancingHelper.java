package org.zeith.equivadds.blocks.conduit;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;

import java.util.*;
import java.util.function.Function;

public class EnergyBalancingHelper
{
	public record BalanceResult(long[] balanced, long leftover)
	{
	}
	
	public static BalanceResult balanceOut(Function<Integer, IEmcStorage> storages, int size, long sendFE)
	{
		// TODO: I NEED HELP WITH BALANCING OUT ENERGY, WHILE BEING AS EFFICIENT AS POSSBLE, SEND HELP!!!
		
		if(size == 1)
		{
			IEmcStorage es = storages.apply(0);
			long rec = es != null ? es.insertEmc(sendFE, IEmcStorage.EmcAction.SIMULATE) : 0;
			rec = Math.min(rec, sendFE);
			return new BalanceResult(new long[] { rec }, sendFE - rec);
		}
		
		final long totalFE = sendFE;
		long[] balanced = new long[size];
		
		List<IEmcStorage> storagesLst = new ArrayList<>(size);
		List<StorageInfo> inf = new ArrayList<>(size);
		
		for(int i = 0; i < size; ++i)
		{
			IEmcStorage storage = storages.apply(i);
			storagesLst.add(storage);
			if(storage != null && storage.insertEmc(1L, IEmcStorage.EmcAction.SIMULATE) > 0L)
			{
				long rec = storage.insertEmc(totalFE, IEmcStorage.EmcAction.SIMULATE);
				inf.add(new StorageInfo(storage, rec));
			}
		}
		
		inf.sort(Comparator.comparingLong(StorageInfo::maxReceive));
		
		for(int i = 0; i < inf.size() && sendFE > 0; i++)
		{
			StorageInfo info = inf.get(i);
			
			int idx = storagesLst.indexOf(info.storage);
			if(idx >= 0)
			{
				long rec = info.maxReceive / inf.size();
				if(rec > 0) balanced[idx] = Math.min(sendFE, rec);
				sendFE -= balanced[idx];
			}
		}
		
		for(int i = inf.size() - 1; i >= 0 && sendFE > 0; i--)
		{
			StorageInfo info = inf.get(i);
			
			int idx = storagesLst.indexOf(info.storage);
			if(idx >= 0)
			{
				long rec = info.maxReceive;
				if(rec > 0)
				{
					long ob = balanced[idx];
					balanced[idx] = Math.min(rec, balanced[idx] + Math.min(rec, sendFE));
					sendFE -= balanced[idx] - ob;
				}
			}
		}
		
		return new BalanceResult(balanced, sendFE);
	}
	
	private record StorageInfo(IEmcStorage storage, long maxReceive)
	{
	}
}