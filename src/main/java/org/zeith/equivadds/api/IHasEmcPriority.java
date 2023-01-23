package org.zeith.equivadds.api;

import net.minecraft.core.Direction;
import org.zeith.equivadds.blocks.conduit.TileEmcConduit;

public interface IHasEmcPriority
{
	int getPriority(Direction from, TileEmcConduit conduit);
}