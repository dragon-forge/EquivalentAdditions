package org.zeith.equivadds.init;

import com.google.common.base.Suppliers;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.Range;
import org.zeith.equivadds.tiles.relays.TileCustomRelay;
import org.zeith.equivadds.util.spoof.BlockEntityTypeRegistryObjectSpoof;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import static moze_intel.projecte.gameObjs.EnumRelayTier.MK3;
import static org.zeith.equivadds.init.TilesEA.*;

public enum EnumRelayTiersEA
		implements StringRepresentable, IExtensibleEnum
{
	MK4("relay_mk4", MK3.getChargeRate() * 3L, MK3.getStorage() * 3L, () -> RELAY_MK4),
	MK5("relay_mk5", MK4.getChargeRate() * 4L, MK4.getStorage() * 4L, () -> RELAY_MK5),
	MK6("relay_mk6", MK5.getChargeRate() * 4L, MK5.getStorage() * 4L, () -> RELAY_MK6),
	MK7("relay_mk7", MK6.getChargeRate() * 5L, MK6.getStorage() * 5L, () -> RELAY_MK7);
	
	private final String name;
	private final long chargeRate;
	private final long storage;
	private final Supplier<BlockEntityTypeRegistryObjectSpoof<? extends TileCustomRelay>> tileType;
	
	EnumRelayTiersEA(String name, @Range(from = 0, to = Long.MAX_VALUE) long chargeRate, @Range(from = 1, to = Long.MAX_VALUE) long storage, Supplier<BlockEntityType<? extends TileCustomRelay>> tileType)
	{
		this.name = name;
		this.chargeRate = chargeRate;
		this.storage = storage;
		this.tileType = Suppliers.memoize(() -> new BlockEntityTypeRegistryObjectSpoof<>(tileType.get()).serverTicker(TileCustomRelay::tickServer));
	}
	
	public static EnumRelayTiersEA create(String name, String name0, @Range(from = 0, to = Long.MAX_VALUE) long chargeRate, @Range(from = 1, to = Long.MAX_VALUE) long storage, Supplier<BlockEntityType<? extends TileCustomRelay>> tileType)
	{
		throw new IllegalStateException("Enum not extended");
	}
	
	@Override
	public @Nonnull String getSerializedName()
	{
		return this.name;
	}
	
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getChargeRate()
	{
		return chargeRate;
	}
	
	@Range(from = 1, to = Long.MAX_VALUE)
	public long getStorage()
	{
		return storage;
	}
	
	@Override
	public String toString()
	{
		return this.getSerializedName();
	}
	
	public BlockEntityTypeRegistryObjectSpoof<? extends TileCustomRelay> getTileType()
	{
		return tileType.get();
	}
}