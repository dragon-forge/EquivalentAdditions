package org.zeith.equivadds.net;

import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zeith.hammerlib.net.*;

import java.math.BigInteger;
import java.util.*;

@MainThreaded
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PacketSyncEMC
		implements IPacket
{
	public static final List<UUID> NEED_SYNC = new ArrayList<>();
	
	BigInteger emc;
	
	public static void queueEmcSync(UUID player)
	{
		if(!NEED_SYNC.contains(player))
			NEED_SYNC.add(player);
	}
	
	public PacketSyncEMC(BigInteger emc)
	{
		this.emc = emc;
	}
	
	public PacketSyncEMC()
	{
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeUtf(emc.toString(Character.MAX_RADIX));
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		emc = new BigInteger(buf.readUtf(), Character.MAX_RADIX);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext ctx)
	{
		LocalPlayer player = Minecraft.getInstance().player;
		if(player != null)
		{
			player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).ifPresent((cap) ->
			{
				cap.setEmc(this.emc);
				if(player.containerMenu instanceof TransmutationContainer container)
					container.transmutationInventory.updateClientTargets();
			});
		}
	}
	
	@SubscribeEvent
	public static void serverTick(TickEvent.ServerTickEvent e)
	{
		// After we're done with ticking tiles...
		if(e.phase == TickEvent.Phase.END)
		{
			Set<UUID> sent = new HashSet<>();
			while(!NEED_SYNC.isEmpty())
			{
				var id = NEED_SYNC.remove(0);
				if(sent.add(id))
				{
					var mp = e.getServer().getPlayerList().getPlayer(id);
					if(mp == null) continue;
					
					IKnowledgeProvider know = mp.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY, null)
							.resolve()
							.orElse(null);
					
					if(know != null)
						Network.sendTo(new PacketSyncEMC(know.getEmc()), mp);
				}
			}
		}
	}
}