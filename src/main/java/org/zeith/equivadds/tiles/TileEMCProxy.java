package org.zeith.equivadds.tiles;

import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.utils.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.zeith.equivadds.EquivalentAdditions;
import org.zeith.equivadds.api.IHasEmcPriority;
import org.zeith.equivadds.api.IMeEmcStorage;
import org.zeith.equivadds.blocks.conduit.TileEmcConduit;
import org.zeith.equivadds.init.TilesEA;
import org.zeith.equivadds.net.PacketSyncEMC;
import org.zeith.equivadds.util.EMCHelper;
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

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.UUID;

public class TileEMCProxy
		extends TileSyncableTickable
		implements IEmcStorage, ITooltipProvider, IMeEmcStorage, IHasEmcPriority
{
	@NBTSerializable
	public UUID owner;
	
	@NBTSerializable
	public String username = "Unknown";
	
	public boolean hasKnowledge;
	IKnowledgeProvider knowledge;
	
	@NBTSerializable
	public final EMCStorage internal = new EMCStorage();
	
	protected final NormalizedTicker normTick = NormalizedTicker.create(this::updateNorm);
	protected final NormalizedTicker clientTick = NormalizedTicker.create(this::clientUpdateNorm);
	
	protected boolean emcChanged;
	
	public TileEMCProxy(BlockPos pos, BlockState state)
	{
		super(TilesEA.EMC_PROXY, pos, state);
		internal.onEmcChanged = () -> emcChanged = true;
	}
	
	@Override
	public void serverTick()
	{
		// This prevents any speed-ups by ensuring only one tick happens per one server tick.
		// This tile does not need any extra updates besides the main update,
		// since otherwise we may cause unwanted lag.
		normTick.tick(level);
	}
	
	@Override
	public void clientTick()
	{
		clientTick.tick(level);
	}
	
	public void updateNorm(int suppressed)
	{
		long need = getNeededEmc();
		if(need > 0)
		{
			need = EMCHelper.pullEMC(need, level, worldPosition, EmcAction.EXECUTE);
			internal.emc += need;
			if(need > 0) internal.storedEmcChanged();
		}
		
		knowledge = updateKnowledge();
		if((knowledge == null) == hasKnowledge || normTick.atTickRate(20))
			sync();
		hasKnowledge = knowledge != null;
	}
	
	public void clientUpdateNorm(int suppressed)
	{
		if(clientTick.atTickRate(10))
			setTooltipDirty(true);
	}
	
	@Override
	public void syncNow()
	{
		Network.sendToArea(new HLTargetPoint(worldPosition, 10, level), new SyncTileEntityPacket(this, true));
	}
	
	protected IKnowledgeProvider updateKnowledge()
	{
		if(!level.isClientSide && level.getServer() != null)
		{
			var mp = level.getServer().getPlayerList().getPlayer(owner);
			if(mp != null)
			{
				username = mp.getGameProfile().getName();
				
				IKnowledgeProvider know = mp.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY, null)
						.resolve()
						.orElse(null);
				
				if(know != null)
				{
					if(internal.emc > 0)
					{
						know.setEmc(know.getEmc().add(BigInteger.valueOf(internal.emc)));
						internal.emc = 0L;
						emcChanged = true;
					}
					
					if(emcChanged)
					{
						emcChanged = false;
						PacketSyncEMC.queueEmcSync(owner);
					}
				}
				
				return know;
			}
		}
		return null;
	}
	
	@Nullable
	public IKnowledgeProvider getKnowledge()
	{
		return knowledge;
	}
	
	@Override
	public @Range(from = 0L, to = 9223372036854775807L) long getStoredEmc()
	{
		return internal.getStoredEmc();
	}
	
	@Override
	public @Range(from = 1L, to = 9223372036854775807L) long getMaximumEmc()
	{
		return internal.getMaximumEmc();
	}
	
	@Override
	public @Range(from = 0L, to = 9223372036854775807L) long getNeededEmc()
	{
		return internal.getNeededEmc();
	}
	
	@Override
	public boolean hasMaxedEmc()
	{
		return internal.hasMaxedEmc();
	}
	
	@Override
	public long extractEmc(long l, EmcAction emcAction)
	{
		return internal.extractEmc(l, emcAction);
	}
	
	@Override
	public long insertEmc(long l, EmcAction emcAction)
	{
		return internal.insertEmc(l, emcAction);
	}
	
	@Override
	public boolean isRelay()
	{
		return true;
	}
	
	public final LazyOptional<IEmcStorage> emcOpt = LazyOptional.of(Cast.staticValue(this)::get);
	
	@Override
	public @Nonnull <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == PECapabilities.EMC_STORAGE_CAPABILITY) return emcOpt.cast();
		return super.getCapability(cap, side);
	}
	
	boolean tdirty;
	
	@Override
	public boolean isTooltipDirty()
	{
		return tdirty;
	}
	
	@Override
	public void setTooltipDirty(boolean dirty)
	{
		this.tdirty = dirty;
	}
	
	static final DecimalFormat FORMAT = new DecimalFormat("#0.0");
	
	@Override
	public void addInformation(ITooltip tip)
	{
		tip.addStack(new ItemStack(getBlockState().getBlock()), 16, 16)
				.addText(getBlockState().getBlock().getName())
				.newLine();
		
		tip.addText(Component.translatable("tooltip." + EquivalentAdditions.MOD_ID + ".stored",
				Component.literal(Constants.EMC_FORMATTER.format(Math.round(internal.getStoredEmc()))).withStyle(ChatFormatting.AQUA)
		)).newLine();
		
		if(owner != null && level != null)
		{
			var pl = level.getPlayerByUUID(owner);
			if(pl != null) username = pl.getGameProfile().getName();
		}
		
		tip.addText(Component.translatable("tooltip." + EquivalentAdditions.MOD_ID + ".owner"));
		tip.addText(Component.literal(": "));
		tip.addText(Component.literal(username).withStyle(ChatFormatting.GREEN));
	}
	
	public static final BigInteger MAXLONG = BigInteger.valueOf(Long.MAX_VALUE);
	
	@Override
	public long getExternalMeEmc()
	{
		if(knowledge != null) return knowledge.getEmc().min(MAXLONG).longValue();
		return 0;
	}
	
	@Override
	public long getExternalEmcCapacity()
	{
		if(knowledge != null) return Long.MAX_VALUE;
		return 0;
	}
	
	@Override
	public long insertExternalEmc(long amount, EmcAction action)
	{
		if(knowledge != null)
		{
			if(action.execute() && amount > 0)
			{
				knowledge.setEmc(knowledge.getEmc().add(BigInteger.valueOf(amount)));
				emcChanged = true;
			}
			return amount;
		}
		
		return 0;
	}
	
	@Override
	public long extractExternalEmc(long amount, EmcAction action)
	{
		if(knowledge != null)
		{
			// It's a long value anyway, so this is safe.
			amount = BigInteger.valueOf(amount)
					.min(knowledge.getEmc())
					.longValue();
			
			if(action.execute() && amount > 0)
			{
				knowledge.setEmc(knowledge.getEmc().subtract(BigInteger.valueOf(amount)));
				emcChanged = true;
			}
			
			return amount;
		}
		
		return 0;
	}
	
	@Override
	public int getPriority(Direction from, TileEmcConduit conduit)
	{
		return 100;
	}
}