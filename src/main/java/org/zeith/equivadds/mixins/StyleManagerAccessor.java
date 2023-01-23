package org.zeith.equivadds.mixins;

import appeng.client.gui.style.StyleManager;
import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.IOException;
import java.util.Set;

@Mixin(StyleManager.class)
public interface StyleManagerAccessor
{
	@Invoker
	static JsonObject callLoadMergedJsonTree(String path, Set<String> loadedFiles, Set<String> resourcePacks) throws IOException
	{
		throw new UnsupportedOperationException();
	}
}
