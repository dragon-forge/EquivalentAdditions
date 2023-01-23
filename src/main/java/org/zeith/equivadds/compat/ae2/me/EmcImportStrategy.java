package org.zeith.equivadds.compat.ae2.me;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.networking.storage.IStorageService;
import appeng.util.BlockApiCache;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.zeith.equivadds.EquivalentAdditions;

public class EmcImportStrategy
		implements StackImportStrategy
{
	private final BlockApiCache<IEmcStorage> apiCache;
	private final Direction fromSide;
	
	public EmcImportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide)
	{
		this.apiCache = BlockApiCache.create(PECapabilities.EMC_STORAGE_CAPABILITY, level, fromPos);
		this.fromSide = fromSide;
	}
	
	@Override
	public boolean transfer(StackTransferContext context)
	{
		if(!context.isKeyTypeEnabled(EMCKeyType.TYPE)) return false;
		
		IEmcStorage receiver = this.apiCache.find(this.fromSide);
		if(receiver == null) return false;
		
		long remainingTransferAmount = (long) context.getOperationsRemaining() * (long) EMCKeyType.TYPE.getAmountPerOperation();
		IStorageService inv = context.getInternalStorage();
		
		long amount = Math.min(remainingTransferAmount, receiver.getStoredEmc());
		if(amount > 0) receiver.extractEmc(amount, IEmcStorage.EmcAction.EXECUTE);
		
		long inserted = inv.getInventory().insert(EMCKey.KEY, amount, Actionable.MODULATE, context.getActionSource());
		long leftover;
		if(inserted < amount)
		{
			leftover = amount - inserted;
			
			long backfill = Math.min(leftover, receiver.getNeededEmc());
			if(backfill > 0)
				receiver.insertEmc(backfill, IEmcStorage.EmcAction.EXECUTE);
			
			if(leftover > backfill)
				EquivalentAdditions.LOG.error("Storage import issue, voided {} EMC", leftover - backfill);
		}
		
		leftover = Math.max(1L, inserted / (long) EMCKeyType.TYPE.getAmountPerOperation());
		context.reduceOperationsRemaining(leftover);
		return amount > 0;
	}
}