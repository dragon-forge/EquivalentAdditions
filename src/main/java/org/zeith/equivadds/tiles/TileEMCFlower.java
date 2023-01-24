package org.zeith.equivadds.tiles;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.gameObjs.block_entities.RelayMK1BlockEntity;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.*;
import org.zeith.equivadds.EquivalentAdditions;
import org.zeith.equivadds.api.EmcFlower;
import org.zeith.equivadds.tiles.relays.TileCustomRelay;
import org.zeith.equivadds.util.EMCStorage;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.HLTargetPoint;
import org.zeith.hammerlib.net.Network;
import org.zeith.hammerlib.net.packets.SyncTileEntityPacket;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.tiles.tooltip.own.ITooltip;
import org.zeith.hammerlib.tiles.tooltip.own.ITooltipProvider;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.mcf.NormalizedTicker;

import java.util.ArrayList;
import java.util.List;

public class TileEMCFlower
		extends TileSyncableTickable
		implements ITooltipProvider, IEmcStorage
{
	public final EmcFlower.FlowerProperties props;
	
	@NBTSerializable("me")
	public final EMCStorage storage;
	
	@NBTSerializable("unproc_emc")
	private double unprocessedEMC;
	
	protected final NormalizedTicker ticker = NormalizedTicker.create(this::updateEmc);
	
	public TileEMCFlower(BlockEntityType<?> type, EmcFlower.FlowerProperties props, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		this.props = props;
		
		this.storage = new EMCStorage(props.storage());
		this.storage.onEmcChanged = () -> isDirty = true;
	}
	
	@Override
	public void update()
	{
		// This prevents any speed-ups by ensuring only one tick happens per one server tick.
		// This tile does not need any extra updates besides the main update,
		// since otherwise we may cause unwanted lag.
		// We compensate extra ticks by multiplying the EMC production by the amount of suppressed ticks.
		ticker.tick(level);
	}
	
	private void updateEmc(int ticks)
	{
		if(!storage.hasMaxedEmc())
		{
			this.unprocessedEMC += (double) this.props.genRate() * ((float) this.getSunLevel() / 320.0F) * ticks;
			if(this.unprocessedEMC >= 1.0)
				this.unprocessedEMC -= (double) storage.insertEmc((long) this.unprocessedEMC, IEmcStorage.EmcAction.EXECUTE);
		}
		
		if(storage.getStoredEmc() > 0L)
		{
			long toSend = storage.getStoredEmc() < props.genRate() ? storage.getStoredEmc() : props.genRate();
			this.sendToAllAcceptors(toSend);
			this.sendRelayBonus();
		}
	}
	
	@Override
	public void syncNow()
	{
		Network.sendToArea(new HLTargetPoint(worldPosition, 10, level), new SyncTileEntityPacket(this, true));
	}
	
	public int getSunLevel()
	{
		return this.level.dimensionType().ultraWarm() ? 16 : this.level.getMaxLocalRawBrightness(this.worldPosition.above()) + 1;
	}
	
	private void sendRelayBonus()
	{
		for(Direction dir : Direction.values())
		{
			var be = this.level.getBlockEntity(this.worldPosition.relative(dir));
			
			RelayMK1BlockEntity relay = Cast.cast(be, RelayMK1BlockEntity.class);
			if(relay != null) relay.addBonus();
			
			TileCustomRelay cr = Cast.cast(be, TileCustomRelay.class);
			if(cr != null) cr.addBonus();
		}
	}
	
	protected long sendToAllAcceptors(long emc)
	{
		if(level == null)
		{
			//If we cannot provide me then just return
			return 0;
		}
		
		emc = Math.min(storage.emc, emc);
		
		long sentEmc = 0;
		List<IEmcStorage> targets = new ArrayList<>();
		for(Direction dir : Direction.values())
		{
			BlockPos neighboringPos = worldPosition.relative(dir);
			// Make sure the neighboring block is loaded as if we are on a chunk border on the edge of loaded chunks this may not be the case
			if(level.isLoaded(neighboringPos))
			{
				BlockEntity neighboringBE = WorldHelper.getBlockEntity(level, neighboringPos);
				if(neighboringBE != null)
				{
					neighboringBE.getCapability(PECapabilities.EMC_STORAGE_CAPABILITY, dir.getOpposite()).ifPresent(theirEmcStorage ->
					{
						if(!isRelay() || !theirEmcStorage.isRelay())
						{
							//If they are both relays don't add the pairing to prevent thrashing
							if(theirEmcStorage.insertEmc(1, EmcAction.SIMULATE) > 0)
							{
								//If they would be wiling to accept any Emc then we consider them to be an "acceptor"
								targets.add(theirEmcStorage);
							}
						}
					});
				}
			}
		}
		
		if(!targets.isEmpty())
		{
			long emcPer = emc / targets.size();
			for(IEmcStorage target : targets)
			{
				long emcCanProvide = storage.extractEmc(emcPer, IEmcStorage.EmcAction.SIMULATE);
				long acceptedEmc = target.insertEmc(emcCanProvide, IEmcStorage.EmcAction.EXECUTE);
				storage.extractEmc(acceptedEmc, IEmcStorage.EmcAction.EXECUTE);
				sentEmc += acceptedEmc;
			}
		}
		
		return sentEmc;
	}
	
	@Override
	public void readNBT(CompoundTag nbt)
	{
		super.readNBT(nbt);
		setTooltipDirty(true);
	}
	
	final LazyOptional<?> thisLazy = LazyOptional.of(() -> this);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == PECapabilities.EMC_STORAGE_CAPABILITY) return thisLazy.cast();
		return super.getCapability(cap, side);
	}
	
	private boolean isDirty;
	
	@Override
	public boolean isTooltipDirty()
	{
		return isDirty;
	}
	
	@Override
	public void setTooltipDirty(boolean dirty)
	{
		isDirty = dirty;
	}
	
	@Override
	public void addInformation(ITooltip tip)
	{
		tip.addStack(new ItemStack(getBlockState().getBlock()), 16, 16)
				.addText(getBlockState().getBlock().getName())
				.newLine();
		
		tip.addText(Component.translatable("tooltip." + EquivalentAdditions.MOD_ID + ".stored",
				Component.literal(Constants.EMC_FORMATTER.format(storage.getStoredEmc())).withStyle(ChatFormatting.AQUA)
		)).newLine();
		
		tip.addText(Component.translatable("tooltip." + EquivalentAdditions.MOD_ID + ".capacity",
				Component.literal(Constants.EMC_FORMATTER.format(storage.getMaximumEmc())).withStyle(ChatFormatting.AQUA)
		)).newLine();
	}
	
	@Override
	public @Range(from = 0L, to = 9223372036854775807L) long getStoredEmc()
	{
		return storage.getStoredEmc();
	}
	
	@Override
	public @Range(from = 1L, to = 9223372036854775807L) long getMaximumEmc()
	{
		return storage.getMaximumEmc();
	}
	
	@Override
	public long extractEmc(long l, EmcAction emcAction)
	{
		return storage.extractEmc(l, emcAction);
	}
	
	@Override
	public long insertEmc(long l, EmcAction emcAction)
	{
		// Flowers are SOURCE of EMC, and thus can not accept EMC into them!
		return 0;
	}
	
	@Override
	public boolean isRelay()
	{
		return false;
	}
}