package org.zeith.equivadds.mixins.ae2;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.*;
import org.zeith.equivadds.compat.ae2.menu.MenuEmcSynthesisChamber;
import org.zeith.equivadds.compat.ae2.tile.TileEmcSynthesisChamber;
import org.zeith.hammerlib.api.tiles.IContainerTile;
import org.zeith.hammerlib.util.java.Cast;

@Mixin(value = TileEmcSynthesisChamber.class, remap = false)
@Implements({
		@Interface(iface = IContainerTile.class, prefix = "ict$")
})
public class TileEmcSynthesisChamberMixin
{
	public AbstractContainerMenu ict$openContainer(Player player, int windowId)
	{
		return new MenuEmcSynthesisChamber(windowId, player.getInventory(), Cast.cast(this));
	}
}