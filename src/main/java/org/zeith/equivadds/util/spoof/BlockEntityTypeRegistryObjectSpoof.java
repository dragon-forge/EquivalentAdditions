package org.zeith.equivadds.util.spoof;

import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import net.minecraft.world.level.block.entity.*;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class BlockEntityTypeRegistryObjectSpoof<BE extends BlockEntity>
		extends BlockEntityTypeRegistryObject<BE>
{
	@Nullable
	private BlockEntityTicker<BE> clientTicker;
	@Nullable
	private BlockEntityTicker<BE> serverTicker;
	
	protected final BlockEntityType<BE> type;
	
	public BlockEntityTypeRegistryObjectSpoof(BlockEntityType<BE> type)
	{
		super(null);
		this.type = type;
	}
	
	@Override
	public @Nonnull BlockEntityType<BE> get()
	{
		return type;
	}
	
	public BlockEntityTypeRegistryObjectSpoof<BE> clientTicker(BlockEntityTicker<BE> ticker)
	{
		clientTicker = ticker;
		return this;
	}
	
	//Internal use only
	public BlockEntityTypeRegistryObjectSpoof<BE> serverTicker(BlockEntityTicker<BE> ticker)
	{
		serverTicker = ticker;
		return this;
	}
	
	@Nullable
	@Override
	public BlockEntityTicker<BE> getTicker(boolean isClient)
	{
		return isClient ? clientTicker : serverTicker;
	}
}