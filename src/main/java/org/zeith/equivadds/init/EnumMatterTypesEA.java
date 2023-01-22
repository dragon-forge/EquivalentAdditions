package org.zeith.equivadds.init;

import moze_intel.projecte.gameObjs.EnumMatterType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.Nullable;
import org.zeith.equivadds.EquivalentAdditions;

import java.util.Collections;
import java.util.List;

public enum EnumMatterTypesEA
		implements StringRepresentable, Tier, IExtensibleEnum
{
	BLUE_MATTER("blue_matter", 5.0F, 18.0F, 16.0F, 6, TagsEA.Blocks.NEEDS_BLUE_MATTER_TOOL, EnumMatterType.RED_MATTER, EquivalentAdditions.id("purple_matter")),
	PURPLE_MATTER("purple_matter", 6.0F, 20.0F, 18.0F, 7, TagsEA.Blocks.NEEDS_PURPLE_MATTER_TOOL, BLUE_MATTER, EquivalentAdditions.id("orange_matter")),
	ORANGE_MATTER("orange_matter", 7.0F, 22.0F, 20.0F, 8, TagsEA.Blocks.NEEDS_ORANGE_MATTER_TOOL, PURPLE_MATTER, EquivalentAdditions.id("green_matter")),
	GREEN_MATTER("green_matter", 8.0F, 24.0F, 22.0F, 9, TagsEA.Blocks.NEEDS_GREEN_MATTER_TOOL, ORANGE_MATTER, null);
	
	private final String name;
	private final float attackDamage;
	private final float efficiency;
	private final float chargeModifier;
	private final int harvestLevel;
	private final TagKey<Block> neededTag;
	
	EnumMatterTypesEA(String name, float attackDamage, float efficiency, float chargeModifier, int harvestLevel, @Nullable TagKey<Block> neededTag, Tier previous, ResourceLocation next)
	{
		this.name = name;
		this.attackDamage = attackDamage;
		this.efficiency = efficiency;
		this.chargeModifier = chargeModifier;
		this.harvestLevel = harvestLevel;
		this.neededTag = neededTag;
		TierSortingRegistry.registerTier(this, EquivalentAdditions.id(name), List.of(previous), next == null ? Collections.emptyList() : List.of(next));
	}
	
	public static EnumMatterTypesEA create(String name, String name0, float attackDamage, float efficiency, float chargeModifier, int harvestLevel, @Nullable TagKey<Block> neededTag, Tier previous, ResourceLocation next)
	{
		throw new IllegalStateException("Enum not extended");
	}
	
	@Override
	public String getSerializedName()
	{
		return this.name;
	}
	
	@Override
	public String toString()
	{
		return this.getSerializedName();
	}
	
	@Override
	public int getUses()
	{
		return 0;
	}
	
	public float getChargeModifier()
	{
		return this.chargeModifier;
	}
	
	@Override
	public float getSpeed()
	{
		return this.efficiency;
	}
	
	@Override
	public float getAttackDamageBonus()
	{
		return this.attackDamage;
	}
	
	@Override
	public int getLevel()
	{
		return this.harvestLevel;
	}
	
	@Override
	public int getEnchantmentValue()
	{
		return 0;
	}
	
	@Override
	public Ingredient getRepairIngredient()
	{
		return Ingredient.EMPTY;
	}
	
	public int getMatterTier()
	{
		return 2 + this.ordinal();
	}
	
	@Override
	public TagKey<Block> getTag()
	{
		return this.neededTag;
	}
}