package org.zeith.equivadds.compat.ae2.me;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.zeith.equivadds.EquivalentAdditions;

import java.util.List;

public class EMCKey
		extends AEKey
{
	public static final AEKey KEY = new EMCKey();
	private static final ResourceLocation ID = EquivalentAdditions.id("emc");
	
	public EMCKey()
	{
		super(EMCKeyType.EMC);
	}
	
	@Override
	public AEKeyType getType()
	{
		return EMCKeyType.TYPE;
	}
	
	@Override
	public AEKey dropSecondary()
	{
		return this;
	}
	
	@Override
	public CompoundTag toTag()
	{
		return new CompoundTag();
	}
	
	@Override
	public Object getPrimaryKey()
	{
		return this;
	}
	
	@Override
	public ResourceLocation getId()
	{
		return ID;
	}
	
	@Override
	public void writeToPacket(FriendlyByteBuf friendlyByteBuf)
	{
	}
	
	@Override
	public void addDrops(long l, List<ItemStack> list, Level level, BlockPos blockPos)
	{
	}
}
