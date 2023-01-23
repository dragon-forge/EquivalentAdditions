package org.zeith.equivadds.compat.ae2.net;

import appeng.api.stacks.AEKey;
import appeng.client.render.crafting.AssemblerAnimationStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.equivadds.compat.ae2.tile.TileEmcSynthesisChamber;
import org.zeith.hammerlib.net.*;

@MainThreaded
public class PacketAssemblerAnimation
		implements IPacket
{
	private BlockPos pos;
	public byte rate;
	public AEKey what;
	
	public PacketAssemblerAnimation(BlockPos pos, byte rate, AEKey what)
	{
		this.pos = pos;
		this.rate = rate;
		this.what = what;
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pos);
		buf.writeByte(rate);
		AEKey.writeKey(buf, what);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		this.pos = buf.readBlockPos();
		this.rate = buf.readByte();
		this.what = AEKey.readKey(buf);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext ctx)
	{
		var lvl = Minecraft.getInstance().level;
		if(lvl == null) return;
		BlockEntity te = lvl.getBlockEntity(pos);
		if(te instanceof TileEmcSynthesisChamber ma)
			ma.setAnimationStatus(new AssemblerAnimationStatus(rate, what.wrapForDisplayOrFilter()));
	}
}