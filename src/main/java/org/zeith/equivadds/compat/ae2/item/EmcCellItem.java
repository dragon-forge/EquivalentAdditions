package org.zeith.equivadds.compat.ae2.item;

import appeng.api.stacks.KeyCounter;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.StorageCell;
import appeng.hooks.AEToolItem;
import appeng.items.AEBaseItem;
import appeng.util.InteractionUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.equivadds.compat.ae2.init.ItemsEAAE2;
import org.zeith.equivadds.compat.ae2.item.cell.IEmcCellItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class EmcCellItem
		extends AEBaseItem
		implements IEmcCellItem, AEToolItem
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
}
