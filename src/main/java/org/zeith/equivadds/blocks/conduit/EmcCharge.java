package org.zeith.equivadds.blocks.conduit;

import org.zeith.hammerlib.util.charging.AbstractCharge;

public class EmcCharge
		extends AbstractCharge
{
	public long EMC;
	
	public EmcCharge(long emc)
	{
		this.EMC = Math.max(emc, 0);
	}
	
	public EmcCharge discharge(long emc)
	{
		return new EmcCharge(this.EMC - emc);
	}
	
	@Override
	public boolean containsCharge()
	{
		return EMC > 0;
	}
	
	@Override
	public EmcCharge copy()
	{
		return new EmcCharge(EMC);
	}
}
