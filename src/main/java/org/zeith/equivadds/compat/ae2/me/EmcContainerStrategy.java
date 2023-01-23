package org.zeith.equivadds.compat.ae2.me;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EmcContainerStrategy
		implements ContainerItemStrategy<EMCKey, EmcItemStorage>
{
	@Override
	public @Nullable GenericStack getContainedStack(ItemStack stack)
	{
		var holder = EmcItemStorage.wrap(stack);
		if(holder != null) return new GenericStack(EMCKey.KEY, holder.getStoredEmc());
		return null;
	}
	
	@Override
	public @Nullable EmcItemStorage findCarriedContext(Player player, AbstractContainerMenu menu)
	{
		return EmcItemStorage.wrap(menu.getCarried());
	}
	
	@Override
	public @Nullable EmcItemStorage findPlayerSlotContext(Player player, int slot)
	{
		return EmcItemStorage.wrap(player.getInventory().getItem(slot));
	}
	
	@Override
	public long extract(EmcItemStorage item, EMCKey emcKey, long amount, Actionable mode)
	{
		long extracted = Math.min(amount, item.getStoredEmc());
		
		if(extracted > 0 && mode == Actionable.MODULATE)
			item.extractEmc(extracted, IEmcStorage.EmcAction.EXECUTE);
		
		return extracted;
	}
	
	@Override
	public long insert(EmcItemStorage item, EMCKey emcKey, long amount, Actionable mode)
	{
		long inserted = Math.min(amount, item.getMaxEmc() - item.getStoredEmc());
		
		if(inserted > 0 && mode == Actionable.MODULATE)
			item.insertEmc(inserted, IEmcStorage.EmcAction.EXECUTE);
		
		return inserted;
	}
	
	@Override
	public void playFillSound(Player player, EMCKey emcKey)
	{
		
	}
	
	@Override
	public void playEmptySound(Player player, EMCKey emcKey)
	{
		
	}
	
	@Override
	public @Nullable GenericStack getExtractableContent(EmcItemStorage item)
	{
		return new GenericStack(EMCKey.KEY, item.getStoredEmc());
	}
}
