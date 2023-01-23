package org.zeith.equivadds.compat.ae2.crafting.pattern;

import appeng.api.stacks.AEItemKey;
import appeng.core.AELog;
import appeng.crafting.pattern.EncodedPatternItem;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ItemEMCSynthesisPattern
		extends EncodedPatternItem
{
	public ItemEMCSynthesisPattern(Properties properties)
	{
		super(properties);
	}
	
	@Nullable
	@Override
	public AEEmcSynthesisPattern decode(ItemStack stack, Level level, boolean tryRecovery)
	{
		if(stack.getItem() == this && stack.hasTag() && level != null)
		{
			return this.decode(AEItemKey.of(stack), level);
		} else
		{
			return null;
		}
	}
	
	@Override
	public AEEmcSynthesisPattern decode(AEItemKey what, Level level)
	{
		if(what != null && what.hasTag())
		{
			try
			{
				return new AEEmcSynthesisPattern(what, level);
			} catch(Exception var4)
			{
				AELog.warn("Could not decode an invalid crafting pattern %s: %s", what.getTag(), var4);
				return null;
			}
		} else
		{
			return null;
		}
	}
	
	public ItemStack encode(AEItemKey out)
	{
		ItemStack stack = new ItemStack(this);
		encode(stack.getOrCreateTag(), out);
		return stack;
	}
	
	public static AEItemKey getOutput(CompoundTag nbt)
	{
		Objects.requireNonNull(nbt, "Pattern must have an out tag.");
		return AEItemKey.fromTag(nbt.getCompound("out"));
	}
	
	public static long getEmc(CompoundTag nbt)
	{
		return EMCHelper.getEmcValue(getOutput(nbt).toStack());
	}
	
	public static void encode(CompoundTag tag, AEItemKey output)
	{
		tag.put("out", output.toTag());
	}
}
