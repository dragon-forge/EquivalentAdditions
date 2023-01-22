package org.zeith.equivadds.blocks;

import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.blocks.BlockDirection;
import moze_intel.projecte.gameObjs.blocks.PEEntityBlock;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.utils.*;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.zeith.equivadds.init.EnumRelayTiersEA;
import org.zeith.equivadds.tiles.relays.TileCustomRelay;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.core.adapter.TagAdapter;

import javax.annotation.Nonnull;
import java.util.List;

import static org.zeith.equivadds.EquivalentAdditions.TAB;

public class BlockCustomRelay
		extends BlockDirection
		implements PEEntityBlock<TileCustomRelay>, ICreativeTabBlock
{
	
	private final EnumRelayTiersEA tier;
	
	public BlockCustomRelay(EnumRelayTiersEA tier, Properties props)
	{
		super(props);
		this.tier = tier;
		
		TagAdapter.bind(PETags.Blocks.MINEABLE_WITH_MORNING_STAR, this);
		TagAdapter.bind(PETags.Blocks.MINEABLE_WITH_HAMMER, this);
		TagAdapter.bind(BlockTags.MINEABLE_WITH_PICKAXE, this);
	}
	
	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter level, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags)
	{
		super.appendHoverText(stack, level, tooltips, flags);
		if(ProjectEConfig.client.statToolTips.get())
		{
			tooltips.add(PELang.EMC_MAX_OUTPUT_RATE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, Constants.EMC_FORMATTER.format(tier.getChargeRate())));
			tooltips.add(PELang.EMC_MAX_STORAGE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, Constants.EMC_FORMATTER.format(tier.getStorage())));
		}
	}
	
	public EnumRelayTiersEA getTier()
	{
		return tier;
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_)
	{
		return List.of(new ItemStack(this));
	}
	
	@Nonnull
	@Override
	@Deprecated
	public InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand,
								 @Nonnull BlockHitResult rtr)
	{
		if(level.isClientSide)
		{
			return InteractionResult.SUCCESS;
		}
		TileCustomRelay relay = WorldHelper.getBlockEntity(TileCustomRelay.class, level, pos, true);
		if(relay != null)
		{
			NetworkHooks.openScreen((ServerPlayer) player, relay, pos);
		}
		return InteractionResult.CONSUME;
	}
	
	@Nullable
	@Override
	public BlockEntityTypeRegistryObject<? extends TileCustomRelay> getType()
	{
		return tier.getTileType();
	}
	
	@Override
	@Deprecated
	public boolean triggerEvent(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, int id, int param)
	{
		super.triggerEvent(state, level, pos, id, param);
		return triggerBlockEntityEvent(state, level, pos, id, param);
	}
	
	@Override
	@Deprecated
	public boolean hasAnalogOutputSignal(@Nonnull BlockState state)
	{
		return true;
	}
	
	@Override
	@Deprecated
	public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos)
	{
		TileCustomRelay relay = WorldHelper.getBlockEntity(TileCustomRelay.class, level, pos, true);
		if(relay == null)
		{
			return 0;
		}
		return MathUtils.scaleToRedstone(relay.getStoredEmc(), relay.getMaximumEmc());
	}
	
	@Override
	public CreativeModeTab getCreativeTab()
	{
		return TAB;
	}
}