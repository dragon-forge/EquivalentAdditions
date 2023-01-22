package org.zeith.equivadds.tiles;

import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
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
import org.zeith.equivadds.init.TilesEA;
import org.zeith.equivadds.net.PacketSyncEMC;
import org.zeith.equivadds.util.EMCHelper;
import org.zeith.equivadds.util.EMCStorage;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.Network;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.tiles.tooltip.own.ITooltip;
import org.zeith.hammerlib.tiles.tooltip.own.ITooltipProvider;
import org.zeith.hammerlib.util.java.Cast;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.UUID;

public class TileEMCProxy
		extends TileSyncableTickable
		implements IEmcStorage, ITooltipProvider
{
	@NBTSerializable
	public UUID owner;
	
	@NBTSerializable
	public String username = "Unknown";
	
	public boolean hasKnowledge;
	IKnowledgeProvider knowledge;
	
	@NBTSerializable
	public final EMCStorage internal = new EMCStorage();
	
	public TileEMCProxy(BlockPos pos, BlockState state)
	{
		super(TilesEA.EMC_PROXY, pos, state);
		dispatcher.registerProperty("emc", internal.emcSync);
	}
	
	@Override
	public void update()
	{
		if(isOnServer())
		{
			long need = getNeededEmc();
			if(need > 0)
			{
				need = EMCHelper.pullEMC(need, level, worldPosition, EmcAction.EXECUTE);
				internal.emc += need;
				if(need > 0) internal.storedEmcChanged();
			}
			
			knowledge = getKnowledge();
			if((knowledge == null) == hasKnowledge || atTickRate(20))
				sync();
			hasKnowledge = knowledge != null;
		} else if(atTickRate(10))
			setTooltipDirty(true);
	}
	
	public IKnowledgeProvider getKnowledge()
	{
		if(!level.isClientSide && level.getServer() != null)
		{
			var mp = level.getServer().getPlayerList().getPlayer(owner);
			if(mp != null)
			{
				username = mp.getGameProfile().getName();
				IKnowledgeProvider know = mp.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY, null).orElse(null);
				if(know != null)
				{
					know.setEmc(know.getEmc().add(BigInteger.valueOf(internal.emc)));
					Network.sendTo(new PacketSyncEMC(know.getEmc()), mp);
					
					internal.emc = 0L;
				}
				return know;
			}
		}
		return null;
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
				Component.literal(String.format("%,d", Math.round(internal.getStoredEmc()))).withStyle(ChatFormatting.AQUA)
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
}