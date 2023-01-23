package org.zeith.equivadds.compat.ae2.me;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.zeith.equivadds.EquivalentAdditions;

public class EMCKeyType
		extends AEKeyType
{
	public static final Component EMC = Component.translatable("gui." + EquivalentAdditions.MOD_ID + ".emc");
	public static final AEKeyType TYPE = new EMCKeyType();
	
	public EMCKeyType()
	{
		super(EquivalentAdditions.id("emc"), EMCKey.class, EMC);
	}
	
	@Nullable
	@Override
	public AEKey readFromPacket(FriendlyByteBuf friendlyByteBuf)
	{
		return EMCKey.KEY;
	}
	
	@Nullable
	@Override
	public AEKey loadKeyFromTag(CompoundTag compoundTag)
	{
		return EMCKey.KEY;
	}
	
	@Override
	public int getAmountPerOperation()
	{
		return 8192;
	}
	
	@Override
	public int getAmountPerByte()
	{
		return 1024 * 1024;
	}
}