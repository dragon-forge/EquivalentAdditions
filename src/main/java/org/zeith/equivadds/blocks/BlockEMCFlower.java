package org.zeith.equivadds.blocks;

import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.*;
import org.zeith.equivadds.api.EmcFlower;
import org.zeith.equivadds.init.ItemsEA;
import org.zeith.equivadds.tiles.TileEMCFlower;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.core.adapter.TagAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import static org.zeith.equivadds.EquivalentAdditions.TAB;

public class BlockEMCFlower
		extends Block
		implements ICustomBlockItem, EntityBlock
{
	public static final VoxelShape SHAPE = Shapes.or(
			Shapes.box(0.40625, 0.0625, 0.40625, 0.59375, 0.25, 0.59375),
			Shapes.box(0.40625, 0.25, 0.59375, 0.59375, 0.4375, 0.78125),
			Shapes.box(0.40625, 0.25, 0.21875, 0.59375, 0.4375, 0.40625),
			Shapes.box(0.21875, 0.25, 0.40625, 0.40625, 0.4375, 0.59375),
			Shapes.box(0.59375, 0.25, 0.40625, 0.78125, 0.4375, 0.59375),
			Shapes.box(0.78125, 0.4375, 0.40625, 0.96875, 0.625, 0.59375),
			Shapes.box(0.03125, 0.4375, 0.40625, 0.21875, 0.625, 0.59375),
			Shapes.box(0.40625, 0.4375, 0.03125, 0.59375, 0.625, 0.21875),
			Shapes.box(0.40625, 0.4375, 0.78125, 0.59375, 0.625, 0.96875),
			Shapes.box(0.59375, 0.4375, 0.59375, 0.78125, 0.625, 0.78125),
			Shapes.box(0.21875, 0.4375, 0.59375, 0.40625, 0.625, 0.78125),
			Shapes.box(0.21875, 0.4375, 0.21875, 0.40625, 0.625, 0.40625),
			Shapes.box(0.59375, 0.4375, 0.21875, 0.78125, 0.625, 0.40625),
			Shapes.box(0.40625, 0.625, 0.21875, 0.59375, 0.8125, 0.40625),
			Shapes.box(0.40625, 0.625, 0.59375, 0.59375, 0.8125, 0.78125),
			Shapes.box(0.21875, 0.625, 0.40625, 0.40625, 0.8125, 0.59375),
			Shapes.box(0.59375, 0.625, 0.40625, 0.78125, 0.8125, 0.59375),
			Shapes.box(0.40625, 0.8125, 0.40625, 0.59375, 1, 0.59375),
			Shapes.box(0, 0, 0, 1, 0.0625, 1),
			Shapes.box(0, 0.0625, 0, 1, 0.125, 0.0625),
			Shapes.box(0, 0.0625, 0.9375, 1, 0.125, 1),
			Shapes.box(0, 0.0625, 0.0625, 0.0625, 0.125, 0.9375),
			Shapes.box(0.9375, 0.0625, 0.0625, 1, 0.125, 0.9375)
	);
	
	public final EmcFlower.FlowerProperties flowerProperties;
	public final Supplier<BlockEntityType<? extends TileEMCFlower>> tile;
	
	public BlockEMCFlower(Properties props, EmcFlower.FlowerProperties flowerProperties, Supplier<BlockEntityType<? extends TileEMCFlower>> tile)
	{
		super(props);
		this.flowerProperties = flowerProperties;
		this.tile = tile;
		
		TagAdapter.bind(PETags.Blocks.MINEABLE_WITH_MORNING_STAR, this);
		TagAdapter.bind(PETags.Blocks.MINEABLE_WITH_HAMMER, this);
		TagAdapter.bind(BlockTags.MINEABLE_WITH_PICKAXE, this);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
	{
		return SHAPE;
	}
	
	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter level, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags)
	{
		super.appendHoverText(stack, level, tooltips, flags);
		
		if(ProjectEConfig.client.statToolTips.get())
		{
			tooltips.add(PELang.EMC_MAX_GEN_RATE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, Constants.EMC_FORMATTER.format(flowerProperties.genRate())));
			tooltips.add(PELang.EMC_MAX_STORAGE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, Constants.EMC_FORMATTER.format(flowerProperties.storage())));
		}
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_)
	{
		return List.of(new ItemStack(this));
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return tile.get().create(pos, state);
	}
	
	@Nonnull
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return BlockAPI.ticker();
	}
	
	@Override
	public BlockItem createBlockItem()
	{
		return new BlockItem(this, ItemsEA.newProperties().tab(TAB));
	}
}