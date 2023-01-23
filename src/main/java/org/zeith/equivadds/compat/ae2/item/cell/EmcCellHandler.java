package org.zeith.equivadds.compat.ae2.item.cell;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.localization.Tooltips;
import moze_intel.projecte.utils.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.zeith.equivadds.EquivalentAdditions;

import javax.annotation.Nullable;
import java.util.List;

public class EmcCellHandler
		implements ICellHandler
{
	public static final EmcCellHandler INSTANCE = new EmcCellHandler();
	
	public EmcCellHandler()
	{
	}
	
	public boolean isCell(ItemStack is)
	{
		return !is.isEmpty() && is.getItem() instanceof IEmcCellItem;
	}
	
	public @Nullable EmcCellInventory getCellInventory(ItemStack is, @Nullable ISaveProvider host)
	{
		return this.isCell(is) ? new EmcCellInventory((IEmcCellItem) is.getItem(), is, host) : null;
	}
	
	public void addCellInformationToTooltip(ItemStack is, List<Component> lines)
	{
		EmcCellInventory handler = this.getCellInventory(is, null);
		if(handler != null)
		{
			if(handler.getEmc() > 0)
				lines.add(Component.translatable("tooltip." + EquivalentAdditions.MOD_ID + ".stored",
						Component.literal(Constants.EMC_FORMATTER.format(handler.getEmc())).withStyle(ChatFormatting.AQUA)
				));
			
			lines.add(Tooltips.bytesUsed(handler.getUsedBytes(), handler.getTotalBytes()));
		}
	}
}
