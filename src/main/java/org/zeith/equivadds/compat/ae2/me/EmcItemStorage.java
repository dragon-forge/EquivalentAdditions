package org.zeith.equivadds.compat.ae2.me;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import net.minecraft.world.item.ItemStack;
import org.zeith.equivadds.compat.ae2.item.cell.IEmcCellItem;

import javax.annotation.Nullable;

public class EmcItemStorage
{
	protected final IItemEmcHolder holder;
	protected final ItemStack stack;
	
	public EmcItemStorage(IItemEmcHolder holder, ItemStack stack)
	{
		this.holder = holder;
		this.stack = stack;
	}
	
	public static boolean has(ItemStack stack)
	{
		if(stack.getItem() instanceof IEmcCellItem) return false;
		return stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).isPresent();
	}
	
	public static @Nullable EmcItemStorage wrap(ItemStack stack)
	{
		if(stack.isEmpty()) return null;
		if(stack.getItem() instanceof IEmcCellItem) return null;
		var cap = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve().orElse(null);
		if(cap == null) return null;
		return new EmcItemStorage(cap, stack);
	}
	
	public long getMaxEmc()
	{
		return holder.getMaximumEmc(stack);
	}
	
	public long getStoredEmc()
	{
		return holder.getStoredEmc(stack);
	}
	
	public long insertEmc(long amount, IEmcStorage.EmcAction action)
	{
		return holder.insertEmc(stack, amount, action);
	}
	
	public long extractEmc(long amount, IEmcStorage.EmcAction action)
	{
		return holder.extractEmc(stack, amount, action);
	}
	
	public ItemStack getStack()
	{
		return stack;
	}
}