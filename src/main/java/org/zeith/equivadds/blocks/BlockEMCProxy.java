package org.zeith.equivadds.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import org.zeith.equivadds.init.EnumMatterTypesEA;
import org.zeith.equivadds.init.TilesEA;
import org.zeith.equivadds.tiles.TileEMCProxy;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.util.java.Cast;

import java.util.List;

import static org.zeith.equivadds.EquivalentAdditions.TAB;

public class BlockEMCProxy
		extends Block
		implements EntityBlock, ICreativeTabBlock
{
	public BlockEMCProxy(Properties props)
	{
		super(props.requiresCorrectToolForDrops());
		BlockHarvestAdapter.bindTool(BlockHarvestAdapter.MineableType.PICKAXE, EnumMatterTypesEA.BLUE_MATTER, this);
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
	{
		if(entity != null)
		{
			var proxy = Cast.cast(level.getBlockEntity(pos), TileEMCProxy.class);
			if(proxy == null)
			{
				proxy = newBlockEntity(pos, state);
				level.setBlockEntity(proxy);
			}
			
			proxy.owner = entity.getUUID();
			proxy.sync();
		}
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_)
	{
		return List.of(new ItemStack(this));
	}
	
	@Nullable
	@Override
	public TileEMCProxy newBlockEntity(BlockPos pos, BlockState state)
	{
		return TilesEA.EMC_PROXY.create(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
	{
		return BlockAPI.ticker();
	}
	
	@Override
	public CreativeModeTab getCreativeTab()
	{
		return TAB;
	}
}