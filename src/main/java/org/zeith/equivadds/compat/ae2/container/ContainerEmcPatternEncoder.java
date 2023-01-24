package org.zeith.equivadds.compat.ae2.container;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.core.definitions.AEItems;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.zeith.equivadds.compat.ae2.client.gui.GuiEmcPatternEncoder;
import org.zeith.equivadds.compat.ae2.tile.TileEmcPatternEncoder;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.api.inv.IScreenContainer;

public class ContainerEmcPatternEncoder
		extends AbstractContainerMenu
		implements IScreenContainer
{
	public final TileEmcPatternEncoder tile;
	public final ContainerLevelAccess access;
	
	public ContainerEmcPatternEncoder(TileEmcPatternEncoder tile, int windowId, Inventory inv)
	{
		super(ContainerAPI.TILE_CONTAINER, windowId);
		this.tile = tile;
		this.access = ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos());
		
		this.addSlot(new PatternInputSlot(tile.items, 0, 116, 23));
		this.addSlot(new PatternOutputSlot(tile.items, 1, 116, 66));
		
		this.addSlot(new EMCAbleItemSlot(tile.target, 0, 44, 43));
		
		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				this.addSlot(new Slot(inv, y + x * 9 + 9, 8 + y * 18, 115 + x * 18));
		
		for(x = 0; x < 9; ++x)
			this.addSlot(new Slot(inv, x, 8 + x * 18, 173));
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slotIdx)
	{
		ItemStack res = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIdx);
		if(slot != null && slot.hasItem())
		{
			ItemStack item = slot.getItem();
			
			res = item.copy();
			
			if(slotIdx < 2)
			{
				if(!this.moveItemStackTo(item, 3, this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			} else if(!this.moveItemStackTo(item, 0, 2, false))
			{
				return ItemStack.EMPTY;
			}
			
			if(item.isEmpty())
			{
				slot.set(ItemStack.EMPTY);
			} else
			{
				slot.setChanged();
			}
		}
		
		return res;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return stillValid(access, player, tile.getBlockState().getBlock());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new GuiEmcPatternEncoder(this, inv, label);
	}
	
	@Override
	public boolean clickMenuButton(Player player, int button)
	{
		if(button == 1)
		{
			return tile.encode(IFluidHandler.FluidAction.EXECUTE);
		}
		
		return false;
	}
	
	public class EMCAbleItemSlot
			extends Slot
	{
		public EMCAbleItemSlot(Container container, int id, int x, int y)
		{
			super(container, id, x, y);
		}
		
		@Override
		public boolean mayPlace(ItemStack item)
		{
			if(tile.getLevel() instanceof ServerLevel sl && EMCHelper.getEmcValue(item) > 0L)
				set(item.getItem().getDefaultInstance());
			
			return false;
		}
		
		@Override
		public boolean mayPickup(Player player)
		{
			if(tile.getLevel() instanceof ServerLevel sl)
				set(ItemStack.EMPTY);
			
			return false;
		}
	}
	
	public static class PatternInputSlot
			extends Slot
	{
		public PatternInputSlot(Container container, int id, int x, int y)
		{
			super(container, id, x, y);
		}
		
		@Override
		public boolean mayPlace(ItemStack item)
		{
			return AEItems.BLANK_PATTERN.isSameAs(item);
		}
	}
	
	public static class PatternOutputSlot
			extends Slot
	{
		public PatternOutputSlot(Container container, int id, int x, int y)
		{
			super(container, id, x, y);
		}
		
		@Override
		public boolean mayPlace(ItemStack item)
		{
			return PatternDetailsHelper.isEncodedPattern(item);
		}
	}
}