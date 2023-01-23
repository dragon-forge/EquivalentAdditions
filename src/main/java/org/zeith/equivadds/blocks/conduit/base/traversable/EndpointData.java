package org.zeith.equivadds.blocks.conduit.base.traversable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Objects;

public record EndpointData(BlockPos pos, Direction dir, int priority, boolean valid)
		implements INBTSerializable<CompoundTag>
{
	public EndpointData(CompoundTag tag)
	{
		this(BlockPos.of(tag.getLong("Pos")), DIRECTIONS[tag.getByte("Dir")], tag.getInt("Priority"), tag.getBoolean("Valid"));
	}
	
	static final Direction[] DIRECTIONS = Direction.values();
	
	public BlockPos getActualPosition()
	{
		return pos.relative(dir);
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		var nbt = new CompoundTag();
		
		nbt.putLong("Pos", pos.asLong());
		nbt.putByte("Dir", (byte) dir.ordinal());
		nbt.putInt("Priority", priority);
		nbt.putBoolean("Valid", valid);
		
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
	}
	
	public boolean sameBlock(EndpointData endpoint)
	{
		return Objects.equals(pos.relative(dir), endpoint.pos.relative(endpoint.dir));
	}
}