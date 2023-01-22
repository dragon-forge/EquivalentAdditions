package org.zeith.equivadds.items.tools;

import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.*;
import moze_intel.projecte.gameObjs.items.IBarHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.zeith.equivadds.init.EnumMatterTypesEA;
import org.zeith.equivadds.util.ToolHelperEA;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemToolEA
		extends DiggerItem
		implements IItemCharge, IBarHelper
{
	private final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();
	protected final EnumMatterTypesEA matterType;
	private final int numCharges;
	
	public ItemToolEA(EnumMatterTypesEA matterType, TagKey<Block> blocks, float damage, float attackSpeed, int numCharges, Item.Properties props)
	{
		super(damage, attackSpeed, matterType, blocks, props);
		this.matterType = matterType;
		this.numCharges = numCharges;
		this.addItemCapability(ChargeItemCapabilityWrapper::new);
	}
	
	protected void addItemCapability(Supplier<ItemCapability<?>> capabilitySupplier)
	{
		this.supportedCapabilities.add(capabilitySupplier);
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
		return ToolHelperEA.getDestroySpeed(this.getShortCutDestroySpeed(stack, state), this.matterType, this.getCharge(stack));
	}
	
	@Override
	public int getNumCharges(@NotNull ItemStack stack)
	{
		return this.numCharges;
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt)
	{
		return this.supportedCapabilities.isEmpty() ? super.initCapabilities(stack, nbt) : new ItemCapabilityWrapper(stack, this.supportedCapabilities);
	}
	
	protected float getShortCutDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state)
	{
		return super.getDestroySpeed(stack, state);
	}
}