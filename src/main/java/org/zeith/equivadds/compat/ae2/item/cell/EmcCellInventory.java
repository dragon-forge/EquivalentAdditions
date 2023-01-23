package org.zeith.equivadds.compat.ae2.item.cell;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.zeith.equivadds.compat.ae2.me.EMCKey;
import org.zeith.equivadds.compat.ae2.me.EMCKeyType;

public class EmcCellInventory
		implements StorageCell
{
	private static final String AMOUNT = "amount";
	private final IEmcCellItem cellType;
	private final ItemStack i;
	private final ISaveProvider container;
	private long storedEmc;
	private boolean isPersisted = true;
	
	public EmcCellInventory(IEmcCellItem cellType, ItemStack o, ISaveProvider container)
	{
		this.cellType = cellType;
		this.i = o;
		this.container = container;
		this.storedEmc = this.getTag().getLong("amount");
		
		String ITEM_COUNT_TAG = "ic";
		String STACK_KEYS = "keys";
		String STACK_AMOUNTS = "amts";
		
		if(this.getTag().contains(ITEM_COUNT_TAG))
		{
			long[] amounts = this.getTag().getLongArray(STACK_AMOUNTS);
			ListTag tags = this.getTag().getList(STACK_KEYS, 10);
			
			for(int i = 0; i < amounts.length; ++i)
			{
				if(AEKey.fromTagGeneric(tags.getCompound(i)) == EMCKey.KEY)
				{
					this.storedEmc += amounts[i];
				}
			}
			
			this.getTag().remove(ITEM_COUNT_TAG);
			this.getTag().remove(STACK_KEYS);
			this.getTag().remove(STACK_AMOUNTS);
			this.saveChanges();
		}
		
	}
	
	private CompoundTag getTag()
	{
		return this.i.getOrCreateTag();
	}
	
	@Override
	public CellState getStatus()
	{
		if(this.storedEmc == 0L)
		{
			return CellState.EMPTY;
		} else if(this.storedEmc == this.getMaxEmc())
		{
			return CellState.FULL;
		} else
		{
			return this.storedEmc > this.getMaxEmc() / 2L ? CellState.TYPES_FULL : CellState.NOT_EMPTY;
		}
	}
	
	@Override
	public double getIdleDrain()
	{
		return this.cellType.getIdleDrain();
	}
	
	public long getMaxEmc()
	{
		return this.cellType.getTotalBytes() * (long) EMCKeyType.TYPE.getAmountPerByte();
	}
	
	public long getEmc()
	{
		return storedEmc;
	}
	
	protected long getTotalBytes()
	{
		return this.cellType.getTotalBytes();
	}
	
	protected long getUsedBytes()
	{
		int amountPerByte = EMCKeyType.TYPE.getAmountPerByte();
		return (this.storedEmc + (long) amountPerByte - 1L) / (long) amountPerByte;
	}
	
	protected void saveChanges()
	{
		this.isPersisted = false;
		if(this.container != null)
		{
			this.container.saveChanges();
		} else
		{
			this.persist();
		}
		
	}
	
	@Override
	public long insert(AEKey what, long amount, Actionable mode, IActionSource source)
	{
		if(!(what instanceof EMCKey))
		{
			return 0L;
		} else
		{
			long inserted = Math.min(this.getMaxEmc() - this.storedEmc, amount);
			if(mode == Actionable.MODULATE)
			{
				this.storedEmc += inserted;
				this.saveChanges();
			}
			
			return inserted;
		}
	}
	
	@Override
	public long extract(AEKey what, long amount, Actionable mode, IActionSource source)
	{
		if(!(what instanceof EMCKey))
		{
			return 0L;
		} else
		{
			long extracted = Math.min(this.storedEmc, amount);
			if(mode == Actionable.MODULATE)
			{
				this.storedEmc -= extracted;
				this.saveChanges();
			}
			
			return extracted;
		}
	}
	
	@Override
	public void persist()
	{
		if(!this.isPersisted)
		{
			if(this.storedEmc <= 0L)
			{
				this.getTag().remove("amount");
			} else
			{
				this.getTag().putLong("amount", this.storedEmc);
			}
			
			this.isPersisted = true;
		}
	}
	
	@Override
	public void getAvailableStacks(KeyCounter out)
	{
		if(this.storedEmc > 0L)
		{
			out.add(EMCKey.KEY, this.storedEmc);
		}
	}
	
	@Override
	public Component getDescription()
	{
		return this.i.getHoverName();
	}
}