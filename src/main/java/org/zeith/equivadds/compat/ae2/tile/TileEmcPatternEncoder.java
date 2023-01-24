package org.zeith.equivadds.compat.ae2.tile;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.core.definitions.AEItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.zeith.equivadds.compat.ae2.container.ContainerEmcPatternEncoder;
import org.zeith.equivadds.compat.ae2.init.ItemsEAAE2;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.api.tiles.IContainerTile;
import org.zeith.hammerlib.tiles.TileSyncable;

public class TileEmcPatternEncoder
		extends TileSyncable
		implements IContainerTile
{
	@NBTSerializable("Items")
	public final SimpleInventory items = new SimpleInventory(2);
	
	@NBTSerializable("Target")
	public final SimpleInventory target = new SimpleInventory(1);
	
	public TileEmcPatternEncoder(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		items.getSlotLimit = slot -> slot == 0 ? 64 : 1;
		target.stackSizeLimit = 1;
	}
	
	@Override
	public AbstractContainerMenu openContainer(Player player, int windowId)
	{
		return new ContainerEmcPatternEncoder(this, windowId, player.getInventory());
	}
	
	public boolean encode(IFluidHandler.FluidAction action)
	{
		var target = this.target.getItem(0);
		if(target.isEmpty()) return false;
		
		var outPattern = items.getItem(1);
		if(!outPattern.isEmpty())
		{
			if(PatternDetailsHelper.isEncodedPattern(outPattern))
			{
				if(action.execute())
				{
					items.setItem(1, ItemsEAAE2.EMC_SYNTHESIS_PATTERN.encode(AEItemKey.of(target)));
					sync();
				}
				return true;
			}
		} else
		{
			var patternItem = items.getItem(0);
			if(AEItems.BLANK_PATTERN.isSameAs(patternItem))
			{
				if(action.execute())
				{
					patternItem.split(1);
					items.setItem(1, ItemsEAAE2.EMC_SYNTHESIS_PATTERN.encode(AEItemKey.of(target)));
					sync();
				}
				return true;
			}
		}
		
		return false;
	}
}