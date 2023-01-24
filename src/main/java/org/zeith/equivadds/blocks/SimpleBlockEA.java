package org.zeith.equivadds.blocks;

import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.core.adapter.TagAdapter;

import java.util.List;

import static org.zeith.equivadds.EquivalentAdditions.TAB;

public class SimpleBlockEA
		extends Block
		implements ICreativeTabBlock
{
	public SimpleBlockEA(Properties props, BlockHarvestAdapter.MineableType type, Tier tier)
	{
		super(props);
		
		if(tier == null)
		{
			if(type != null)
				TagAdapter.bind(type.blockTag(), this);
		} else
			BlockHarvestAdapter.bindTool(type, tier, this);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_)
	{
		return List.of(new ItemStack(this));
	}
	
	@Override
	public CreativeModeTab getCreativeTab()
	{
		return TAB;
	}
}