package org.zeith.equivadds.blocks.conduit;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import net.minecraft.core.Direction;
import org.zeith.equivadds.blocks.conduit.base.traversable.ITraversable;
import org.zeith.equivadds.blocks.conduit.base.traversable.TraversableHelper;
import org.zeith.hammerlib.util.java.Cast;

import java.util.function.Function;
import java.util.stream.LongStream;

public record EmcConduitHandler(Direction from, TileEmcConduit conduit)
		implements IEmcStorage
{
	@Override
	public long getStoredEmc()
	{
		return 0;
	}
	
	@Override
	public long getMaximumEmc()
	{
		return 0;
	}
	
	@Override
	public long extractEmc(long emc, EmcAction action)
	{
		return conduit.contents.extractEmc(emc, action);
	}
	
	@Override
	public long insertEmc(long maxReceive, EmcAction action)
	{
		maxReceive = Math.min(maxReceive, conduit.properties.transfer());
		if(maxReceive <= 0)
			return 0;
		
		var charge = new EmcCharge(maxReceive);
		var paths = TraversableHelper.findAllPaths(conduit, from, charge);
		
		Function<Integer, IEmcStorage> targets = i ->
		{
			var path = paths.get(i);
			return Cast.optionally(path.lastElement(), TileEmcConduit.class)
					.flatMap(w -> w.relativeEnergyHandler(path.endpoint.dir()).resolve())
					.orElse(null);
		};
		
		var receives = EnergyBalancingHelper.balanceOut(targets, paths.size(), charge.EMC);
		
		long receiveAmount = 0;
		
		if(action.execute())
		{
			long[] sendToPath = receives.balanced();
			for(int i = 0; i < sendToPath.length; ++i)
			{
				var path = paths.get(i);
				for(int j = 0; j < path.size() && sendToPath[i] > 0; j++)
				{
					ITraversable<EmcCharge> component = path.get(j);
					if(component instanceof TileEmcConduit rem)
					{
						float rec;
						if(j == path.size() - 1 && (rec = Math.max(0F, sendToPath[i])) > 0)
						{
							rem.emitTo(path.endpoint.dir(), rec);
							receiveAmount += rec;
						}
					}
				}
			}
		} else
			receiveAmount += LongStream.of(receives.balanced()).sum();
		
		return receiveAmount;
	}
	
	@Override
	public boolean isRelay()
	{
		return true;
	}
}
