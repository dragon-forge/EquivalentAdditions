package org.zeith.equivadds.blocks.conduit;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.gameObjs.block_entities.CollectorMK1BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.zeith.equivadds.api.EmcConduit;
import org.zeith.equivadds.api.IHasEmcPriority;
import org.zeith.equivadds.blocks.conduit.base.traversable.EndpointData;
import org.zeith.equivadds.blocks.conduit.base.traversable.ITraversable;
import org.zeith.equivadds.tiles.TileEMCFlower;
import org.zeith.equivadds.tiles.collectors.TileCustomCollector;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.java.Cast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TileEmcConduit
		extends TileSyncableTickable
		implements ITraversable<EmcCharge>
{
	public final EmcConduit.ConduitProperties properties;
	
	@NBTSerializable("Contents")
	public final EmcConduitContents contents = new EmcConduitContents();
	
	public TileEmcConduit(BlockEntityType<?> type, EmcConduit.ConduitProperties properties, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		this.properties = properties;
	}
	
	@Override
	public void update()
	{
		contents.emit(this);
	}
	
	public boolean doesConnectTo(Direction to)
	{
		return level.getBlockEntity(worldPosition.relative(to)) instanceof TileEmcConduit
				|| relativeEnergyHandler(to).isPresent();
	}
	
	public void emitTo(Direction to, float fe)
	{
		contents.add(to, fe);
	}
	
	public long emitToDirect(Direction to, long emc, IEmcStorage.EmcAction action)
	{
		return relativeEnergyHandler(to)
				.map(storage -> storage instanceof TileEMCFlower || storage instanceof TileCustomCollector || storage instanceof CollectorMK1BlockEntity ? 0 : storage.insertEmc(emc, action))
				.orElse(0L);
	}
	
	private final net.minecraftforge.common.util.LazyOptional<?>[] sidedEmcHandlers =
			Direction.stream()
					.map(dir -> LazyOptional.of(() -> new EmcConduitHandler(dir, this)))
					.toArray(LazyOptional[]::new);
	
	@Override
	public @Nonnull <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
	{
		if(side != null && cap == PECapabilities.EMC_STORAGE_CAPABILITY) return sidedEmcHandlers[side.ordinal()].cast();
		return super.getCapability(cap, side);
	}
	
	public LazyOptional<IEmcStorage> relativeEnergyHandler(Direction to)
	{
		var be = level.getBlockEntity(worldPosition.relative(to));
		return be == null || be instanceof TileEmcConduit
				? LazyOptional.empty() // Either there is no block entity, or the block entity is a pipe
				: be.getCapability(PECapabilities.EMC_STORAGE_CAPABILITY, to.getOpposite());
	}
	
	private boolean connectsTo(Direction to, TileEmcConduit wire)
	{
		return true;
	}
	
	@Override
	public Optional<? extends ITraversable<EmcCharge>> getRelativeTraversable(Direction side, EmcCharge contents)
	{
		return Cast.optionally(level.getBlockEntity(worldPosition.relative(side)), TileEmcConduit.class)
				.filter(pipe -> connectsTo(side, pipe));
	}
	
	public int getPriority(Direction dir)
	{
		if(level.getBlockEntity(worldPosition.relative(dir)) instanceof IHasEmcPriority p)
			return p.getPriority(dir.getOpposite(), this);
		return 0;
	}
	
	@Override
	public List<EndpointData> getEndpoints(EmcCharge contents)
	{
		return Stream.of(BlockConduit.DIRECTIONS)
				.filter(dir ->
				{
					long canEmit = emitToDirect(dir, contents.EMC, IEmcStorage.EmcAction.SIMULATE);
					return canEmit > 0L && canEmit > this.contents.energy[dir.ordinal()];
				})
				.map(dir -> new EndpointData(worldPosition, dir, getPriority(dir), true))
				.toList();
	}
	
	@Override
	public BlockPos getPosition()
	{
		return worldPosition;
	}
}