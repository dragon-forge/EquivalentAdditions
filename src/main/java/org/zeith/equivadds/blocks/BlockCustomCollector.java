package org.zeith.equivadds.blocks;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.blocks.BlockDirection;
import moze_intel.projecte.gameObjs.blocks.PEEntityBlock;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.utils.*;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.zeith.equivadds.init.EnumCollectorTiersEA;
import org.zeith.equivadds.tiles.collectors.TileCustomCollector;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.core.adapter.TagAdapter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

import static org.zeith.equivadds.EquivalentAdditions.TAB;

public class BlockCustomCollector
		extends BlockDirection
		implements PEEntityBlock<TileCustomCollector>, ICreativeTabBlock
{
	private final EnumCollectorTiersEA tier;
	
	public BlockCustomCollector(EnumCollectorTiersEA tier, BlockBehaviour.Properties props)
	{
		super(props);
		this.tier = tier;
		
		TagAdapter.bind(PETags.Blocks.MINEABLE_WITH_MORNING_STAR, this);
		TagAdapter.bind(PETags.Blocks.MINEABLE_WITH_HAMMER, this);
		TagAdapter.bind(BlockTags.MINEABLE_WITH_PICKAXE, this);
	}
	
	public EnumCollectorTiersEA getTier()
	{
		return this.tier;
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_)
	{
		return List.of(new ItemStack(this));
	}
	
	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter level, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags)
	{
		super.appendHoverText(stack, level, tooltips, flags);
		
		if(ProjectEConfig.client.statToolTips.get())
		{
			tooltips.add(PELang.EMC_MAX_GEN_RATE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, Constants.EMC_FORMATTER.format(tier.getGenRate())));
			tooltips.add(PELang.EMC_MAX_STORAGE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, Constants.EMC_FORMATTER.format(tier.getStorage())));
		}
	}
	
	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public @Nonnull InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit)
	{
		if(level.isClientSide)
		{
			return InteractionResult.SUCCESS;
		} else
		{
			TileCustomCollector collector = WorldHelper.getBlockEntity(TileCustomCollector.class, level, pos, true);
			if(collector != null) NetworkHooks.openScreen((ServerPlayer) player, collector, pos);
			return InteractionResult.CONSUME;
		}
	}
	
	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public @Nullable MenuProvider getMenuProvider(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos)
	{
		return WorldHelper.getBlockEntity(TileCustomCollector.class, level, pos, true);
	}
	
	@Override
	public @Nullable BlockEntityTypeRegistryObject<? extends TileCustomCollector> getType()
	{
		return this.tier.getTileType();
	}
	
	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public boolean triggerEvent(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, int id, int param)
	{
		super.triggerEvent(state, level, pos, id, param);
		return this.triggerBlockEntityEvent(state, level, pos, id, param);
	}
	
	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public boolean hasAnalogOutputSignal(@Nonnull BlockState state)
	{
		return true;
	}
	
	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos)
	{
		var collector = WorldHelper.getBlockEntity(TileCustomCollector.class, level, pos, true);
		if(collector == null)
		{
			return super.getAnalogOutputSignal(state, level, pos);
		} else
		{
			Optional<IItemHandler> cap = collector.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).resolve();
			if(cap.isEmpty())
			{
				return super.getAnalogOutputSignal(state, level, pos);
			} else
			{
				ItemStack charging = cap.get().getStackInSlot(0);
				if(!charging.isEmpty())
				{
					Optional<IItemEmcHolder> holderCapability = charging.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
					if(holderCapability.isPresent())
					{
						IItemEmcHolder emcHolder = holderCapability.get();
						return MathUtils.scaleToRedstone(emcHolder.getStoredEmc(charging), emcHolder.getMaximumEmc(charging));
					} else
					{
						return MathUtils.scaleToRedstone(collector.getStoredEmc(), collector.getEmcToNextGoal());
					}
				} else
				{
					return MathUtils.scaleToRedstone(collector.getStoredEmc(), collector.getMaximumEmc());
				}
			}
		}
	}
	
	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving)
	{
		if(state.getBlock() != newState.getBlock())
		{
			TileCustomCollector ent = WorldHelper.getBlockEntity(TileCustomCollector.class, level, pos);
			if(ent != null) ent.clearLocked();
			
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}
	
	@Override
	public CreativeModeTab getCreativeTab()
	{
		return TAB;
	}
}