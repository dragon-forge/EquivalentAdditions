package org.zeith.equivadds.init;

import com.google.common.base.Suppliers;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.Range;
import org.zeith.equivadds.tiles.collectors.TileCustomCollector;
import org.zeith.equivadds.util.spoof.BlockEntityTypeRegistryObjectSpoof;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import static moze_intel.projecte.gameObjs.EnumCollectorTier.MK3;
import static org.zeith.equivadds.init.TilesEA.*;

public enum EnumCollectorTiersEA
		implements StringRepresentable, IExtensibleEnum
{
	MK4("collector_mk4", MK3.getGenRate() * 3L, MK3.getStorage() * 3L, () -> COLLECTOR_MK4),
	MK5("collector_mk5", MK4.getGenRate() * 4L, MK4.getStorage() * 4L, () -> COLLECTOR_MK5),
	MK6("collector_mk6", MK5.getGenRate() * 4L, MK5.getStorage() * 4L, () -> COLLECTOR_MK6),
	MK7("collector_mk7", MK6.getGenRate() * 5L, MK6.getStorage() * 5L, () -> COLLECTOR_MK7);
	
	private final String name;
	private final long genRate;
	private final long storage;
	private final Supplier<BlockEntityTypeRegistryObjectSpoof<? extends TileCustomCollector>> tileType;
	
	EnumCollectorTiersEA(@Range(from = 1L, to = Long.MAX_VALUE) String name, long genRate, long storage, Supplier<BlockEntityType<? extends TileCustomCollector>> tileType)
	{
		this.name = name;
		this.genRate = genRate;
		this.storage = storage;
		this.tileType = Suppliers.memoize(() -> new BlockEntityTypeRegistryObjectSpoof<>(tileType.get()).serverTicker(TileCustomCollector::tickServer));
	}
	
	public static EnumCollectorTiersEA create(String name, @Range(from = 1L, to = Long.MAX_VALUE) String name0, long genRate, long storage, Supplier<BlockEntityType<? extends TileCustomCollector>> tileType)
	{
		throw new IllegalStateException("Enum not extended");
	}
	
	@Override
	public @Nonnull String getSerializedName()
	{
		return this.name;
	}
	
	public @Range(
			from = 0L,
			to = Long.MAX_VALUE
	) long getGenRate()
	{
		return this.genRate;
	}
	
	public @Range(
			from = 1L,
			to = Long.MAX_VALUE
	) long getStorage()
	{
		return this.storage;
	}
	
	@Override
	public String toString()
	{
		return this.getSerializedName();
	}
	
	public BlockEntityTypeRegistryObjectSpoof<? extends TileCustomCollector> getTileType()
	{
		return tileType.get();
	}
}