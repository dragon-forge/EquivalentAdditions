package org.zeith.equivadds.compat.ae2.me;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.core.localization.GuiText;
import appeng.util.BlockApiCache;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.zeith.equivadds.api.IMeEmcStorage;

public class EmcExternalStorageStrategy
		implements ExternalStorageStrategy
{
	private final BlockApiCache<IEmcStorage> apiCache;
	private final Direction fromSide;
	
	public EmcExternalStorageStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide)
	{
		this.apiCache = BlockApiCache.create(PECapabilities.EMC_STORAGE_CAPABILITY, level, fromPos);
		this.fromSide = fromSide;
	}
	
	@Nullable
	@Override
	public MEStorage createWrapper(boolean extractableOnly, Runnable injectOrExtractCallback)
	{
		IEmcStorage storage = apiCache.find(fromSide);
		
		// Handle cases like EMC Proxy.
		if(storage instanceof IMeEmcStorage me)
			return new EmcExternalStorage(me, injectOrExtractCallback);
		
		return storage != null ? new EmcStorage(storage, injectOrExtractCallback) : null;
	}
	
	private record EmcStorage(IEmcStorage receiver, Runnable injectOrExtractCallback)
			implements MEStorage
	{
		@Override
		public long insert(AEKey what, long amount, Actionable mode, IActionSource source)
		{
			long inserted = receiver.insertEmc(amount, IEmcStorage.EmcAction.get(!mode.isSimulate()));
			
			if(inserted > 0 && mode == Actionable.MODULATE)
				injectOrExtractCallback.run();
			
			return inserted;
		}
		
		@Override
		public long extract(AEKey what, long amount, Actionable mode, IActionSource source)
		{
			long extracted = receiver.extractEmc(amount, IEmcStorage.EmcAction.get(!mode.isSimulate()));
			
			if(extracted > 0 && mode == Actionable.MODULATE)
				injectOrExtractCallback.run();
			
			return extracted;
		}
		
		@Override
		public void getAvailableStacks(KeyCounter out)
		{
			var emc = receiver.getStoredEmc();
			if(emc > 0) out.add(EMCKey.KEY, emc);
		}
		
		@Override
		public Component getDescription()
		{
			return GuiText.ExternalStorage.text(EMCKeyType.TYPE.getDescription());
		}
	}
	
	private record EmcExternalStorage(IMeEmcStorage receiver, Runnable injectOrExtractCallback)
			implements MEStorage
	{
		@Override
		public long insert(AEKey what, long amount, Actionable mode, IActionSource source)
		{
			long inserted = receiver.insertExternalEmc(amount, IEmcStorage.EmcAction.get(!mode.isSimulate()));
			
			if(inserted > 0 && mode == Actionable.MODULATE)
				injectOrExtractCallback.run();
			
			return inserted;
		}
		
		@Override
		public long extract(AEKey what, long amount, Actionable mode, IActionSource source)
		{
			long extracted = receiver.extractExternalEmc(amount, IEmcStorage.EmcAction.get(!mode.isSimulate()));
			
			if(extracted > 0 && mode == Actionable.MODULATE)
				injectOrExtractCallback.run();
			
			return extracted;
		}
		
		@Override
		public void getAvailableStacks(KeyCounter out)
		{
			var emc = receiver.getExternalMeEmc();
			if(emc > 0) out.add(EMCKey.KEY, emc);
		}
		
		@Override
		public Component getDescription()
		{
			return GuiText.ExternalStorage.text(EMCKeyType.TYPE.getDescription());
		}
	}
}