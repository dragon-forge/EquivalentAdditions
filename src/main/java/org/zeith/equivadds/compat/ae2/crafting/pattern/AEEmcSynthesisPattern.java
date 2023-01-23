package org.zeith.equivadds.compat.ae2.crafting.pattern;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.zeith.equivadds.compat.ae2.me.EMCKey;

import javax.annotation.Nullable;
import java.util.Objects;

public class AEEmcSynthesisPattern
		implements IPatternDetails
{
	private final AEItemKey definition;
	private final IPatternDetails.IInput[] inputs;
	private final GenericStack[] outputs;
	private final ItemStack output;
	private final long emc;
	
	public AEEmcSynthesisPattern(AEItemKey definition, Level level)
	{
		this.definition = definition;
		CompoundTag tag = Objects.requireNonNull(definition.getTag());
		
		long savedEmc = ItemEMCSynthesisPattern.getEmc(tag);
		AEItemKey output = ItemEMCSynthesisPattern.getOutput(tag);
		
		this.emc = savedEmc;
		
		this.output = output.toStack();
		
		if(this.output.isEmpty() || this.emc < 1L)
		{
			throw new IllegalStateException("The recipe " + output + " produced an empty item stack result.");
		} else
		{
			this.inputs = new IInput[] { new EmcInput(savedEmc) };
			this.outputs = new GenericStack[] { GenericStack.fromItemStack(this.output) };
		}
	}
	
	public long getEmc()
	{
		return emc;
	}
	
	@Override
	public int hashCode()
	{
		return this.definition.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj != null && obj.getClass() == this.getClass() && ((AEEmcSynthesisPattern) obj).definition.equals(this.definition);
	}
	
	@Override
	public AEItemKey getDefinition()
	{
		return this.definition;
	}
	
	@Override
	public IPatternDetails.IInput[] getInputs()
	{
		return this.inputs;
	}
	
	@Override
	public GenericStack[] getOutputs()
	{
		return this.outputs;
	}
	
	public ItemStack assemble(Level level)
	{
		return this.output.copy();
	}
	
	private static class EmcInput
			implements IPatternDetails.IInput
	{
		private final long emc;
		
		private EmcInput(long emc)
		{
			this.emc = emc;
		}
		
		@Override
		public GenericStack[] getPossibleInputs()
		{
			return new GenericStack[] {
					new GenericStack(EMCKey.KEY, emc)
			};
		}
		
		@Override
		public long getMultiplier()
		{
			return 1L;
		}
		
		@Override
		public boolean isValid(AEKey input, Level level)
		{
			return input == EMCKey.KEY;
		}
		
		@Nullable
		public AEKey getRemainingKey(AEKey template)
		{
			return null;
		}
	}
}
