package org.zeith.equivadds.items.tools;

import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.*;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.IBarHelper;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.ToolHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.equivadds.init.EnumMatterTypesEA;
import org.zeith.equivadds.util.ToolHelperEA;

import java.util.List;
import java.util.function.Consumer;

public class ItemPickaxeEA
		extends PickaxeItem
		implements IItemCharge, IItemMode, IBarHelper
{
	private final EnumMatterTypesEA matterType;
	private final ILangEntry[] modeDesc;
	private final int numCharges;
	
	public ItemPickaxeEA(EnumMatterTypesEA matterType, int numCharges, Item.Properties props)
	{
		super(matterType, 4, -2.8F, props);
		this.modeDesc = new ILangEntry[] {
				PELang.MODE_PICK_1,
				PELang.MODE_PICK_2,
				PELang.MODE_PICK_3,
				PELang.MODE_PICK_4
		};
		this.matterType = matterType;
		this.numCharges = numCharges;
	}
	
	@Override
	public boolean isEnchantable(@NotNull ItemStack stack)
	{
		return false;
	}
	
	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book)
	{
		return false;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		return false;
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
	{
		return 0;
	}
	
	@Override
	public boolean isBarVisible(@NotNull ItemStack stack)
	{
		return true;
	}
	
	@Override
	public float getWidthForBar(ItemStack stack)
	{
		return 1.0F - this.getChargePercent(stack);
	}
	
	@Override
	public int getBarWidth(@NotNull ItemStack stack)
	{
		return this.getScaledBarWidth(stack);
	}
	
	@Override
	public int getBarColor(@NotNull ItemStack stack)
	{
		return this.getColorForBar(stack);
	}
	
	@Override
	public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state)
	{
		return ToolHelperEA.canMatterMine(this.matterType, state.getBlock()) ? 1200000.0F : ToolHelperEA.getDestroySpeed(super.getDestroySpeed(stack, state), this.matterType, this.getCharge(stack));
	}
	
	@Override
	public int getNumCharges(@NotNull ItemStack stack)
	{
		return this.numCharges;
	}
	
	@Override
	public ILangEntry[] getModeLangEntries()
	{
		return this.modeDesc;
	}
	
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags)
	{
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(this.getToolTip(stack));
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt)
	{
		return new ItemCapabilityWrapper(stack,
				new ChargeItemCapabilityWrapper(),
				new ModeChangerItemCapabilityWrapper()
		);
	}
	
	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		return ProjectEConfig.server.items.pickaxeAoeVeinMining.get() ? ItemHelper.actionResultFromType(ToolHelper.mineOreVeinsInAOE(player, hand), stack) : InteractionResultHolder.pass(stack);
	}
	
	@Override
	public @NotNull InteractionResult useOn(UseOnContext context)
	{
		Player player = context.getPlayer();
		if(player != null && !ProjectEConfig.server.items.pickaxeAoeVeinMining.get())
		{
			BlockPos pos = context.getClickedPos();
			return ItemHelper.isOre(context.getLevel().getBlockState(pos))
					? ToolHelper.tryVeinMine(player, context.getItemInHand(), pos, context.getClickedFace())
					: InteractionResult.PASS;
		} else
		{
			return InteractionResult.PASS;
		}
	}
	
	@Override
	public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity living)
	{
		ToolHelper.digBasedOnMode(stack, level, pos, living, Item::getPlayerPOVHitResult);
		return true;
	}
}