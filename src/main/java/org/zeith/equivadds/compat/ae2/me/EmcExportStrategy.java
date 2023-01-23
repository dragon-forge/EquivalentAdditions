package org.zeith.equivadds.compat.ae2.me;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.util.BlockApiCache;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class EmcExportStrategy
		implements StackExportStrategy
{
	private final BlockApiCache<IEmcStorage> apiCache;
	private final Direction fromSide;
	
	public EmcExportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide)
	{
		this.apiCache = BlockApiCache.create(PECapabilities.EMC_STORAGE_CAPABILITY, level, fromPos);
		this.fromSide = fromSide;
	}
	
	@Override
	public long transfer(StackTransferContext context, AEKey what, long amount)
	{
		IEmcStorage receiver = this.apiCache.find(this.fromSide);
		if(receiver == null) return 0L;
		
		long insertable = Math.min(amount, receiver.getNeededEmc());
		long extracted = StorageHelper.poweredExtraction(context.getEnergySource(), context.getInternalStorage().getInventory(), EMCKey.KEY, insertable, context.getActionSource(), Actionable.MODULATE);
		if(extracted > 0)
			receiver.insertEmc(extracted, IEmcStorage.EmcAction.EXECUTE);
		
		return extracted;
	}
	
	@Override
	public long push(AEKey what, long amount, Actionable mode)
	{
		IEmcStorage receiver = this.apiCache.find(this.fromSide);
		if(receiver == null) return 0L;
		return receiver.insertEmc(amount, IEmcStorage.EmcAction.get(!mode.isSimulate()));
	}
}
