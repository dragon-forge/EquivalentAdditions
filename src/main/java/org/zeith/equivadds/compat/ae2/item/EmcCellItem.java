package org.zeith.equivadds.compat.ae2.item;

import appeng.api.stacks.KeyCounter;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.StorageCell;
import appeng.hooks.AEToolItem;
import appeng.items.AEBaseItem;
import appeng.util.InteractionUtil;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.capability.EmcHolderItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.*;
import org.zeith.equivadds.compat.ae2.init.ItemsEAAE2;
import org.zeith.equivadds.compat.ae2.item.cell.IEmcCellItem;
import org.zeith.equivadds.compat.ae2.me.EMCKeyType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class EmcCellItem
		extends AEBaseItem
		implements IEmcCellItem, AEToolItem, IItemEmcHolder
{
	private final ItemLike coreItem;
	private final int totalBytes;
	private final double idleDrain;
	
	public EmcCellItem(Item.Properties properties, ItemLike coreItem, int kilobytes, double idleDrain)
	{
		super(properties.stacksTo(1));
		this.coreItem = coreItem;
		this.totalBytes = kilobytes * 1024;
		this.idleDrain = idleDrain;
	}
	
	public ItemLike getCoreItem()
	{
		return coreItem;
	}
	
	@Override
	public long getTotalBytes()
	{
		return this.totalBytes;
	}
	
	@Override
	public double getIdleDrain()
	{
		return this.idleDrain;
	}
	
	@ParametersAreNonnullByDefault
	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		this.disassembleDrive(player.getItemInHand(hand), level, player);
		return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()), player.getItemInHand(hand));
	}
	
	private boolean disassembleDrive(ItemStack stack, Level level, Player player)
	{
		if(InteractionUtil.isInAlternateUseMode(player))
		{
			if(level.isClientSide())
				return false;
			
			Inventory pInv = player.getInventory();
			StorageCell inv = StorageCells.getCellInventory(stack, null);
			
			if(inv != null && pInv.getSelected() == stack)
			{
				KeyCounter list = inv.getAvailableStacks();
				
				if(list.isEmpty())
				{
					pInv.setItem(pInv.selected, ItemStack.EMPTY);
					pInv.placeItemBackInInventory(new ItemStack(this.coreItem));
					pInv.placeItemBackInInventory(new ItemStack(ItemsEAAE2.EMC_CELL_HOUSING));
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		return this.disassembleDrive(stack, context.getLevel(), context.getPlayer()) ? InteractionResult.sidedSuccess(context.getLevel().isClientSide()) : InteractionResult.PASS;
	}
	
	@ParametersAreNonnullByDefault
	@Override
	public void appendHoverText(ItemStack is, @Nullable Level level, List<Component> lines, TooltipFlag tooltipFlag)
	{
		this.addCellInformationToTooltip(is, lines);
	}
	
	@Override
	public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
	{
		return new ItemCapabilityWrapper(stack, new EmcHolderItemCapabilityWrapper());
	}
	
	// Let ProjectE know about internal EMC stored here.
	
	@Override
	public long insertEmc(@NotNull ItemStack stack, long toInsert, IEmcStorage.EmcAction action)
	{
		if(toInsert < 0) return extractEmc(stack, -toInsert, action);
		return 0L;
	}
	
	@Override
	public long extractEmc(@NotNull ItemStack stack, long toExtract, IEmcStorage.EmcAction action)
	{
		if(toExtract < 0) return insertEmc(stack, -toExtract, action);
		
		long storedEmc = getStoredEmc(stack);
		long toRemove = Math.min(storedEmc, toExtract);
		if(action.execute())
			stack.getOrCreateTag().putLong("amount", stack.getOrCreateTag().getLong("amount") - toRemove);
		
		return toRemove;
	}
	
	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getStoredEmc(@NotNull ItemStack stack)
	{
		return stack.getOrCreateTag().getLong("amount");
	}
	
	@Override
	@Range(from = 1, to = Long.MAX_VALUE)
	public long getMaximumEmc(@NotNull ItemStack stack)
	{
		return getTotalBytes() * EMCKeyType.TYPE.getAmountPerByte();
	}
}