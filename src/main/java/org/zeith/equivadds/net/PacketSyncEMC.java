package org.zeith.equivadds.net;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.*;

import java.math.BigInteger;

@MainThreaded
public class PacketSyncEMC
		implements IPacket
{
	BigInteger emc;
	
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
}