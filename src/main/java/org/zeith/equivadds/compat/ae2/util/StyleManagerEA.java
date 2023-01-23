package org.zeith.equivadds.compat.ae2.util;

import appeng.client.gui.style.ScreenStyle;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
import org.zeith.equivadds.mixins.StyleManagerAccessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class StyleManagerEA
{
	private static final Map<ResourceLocation, ScreenStyle> styleCache = new HashMap<>();
	public static final String PROP_INCLUDES = "includes";
	
	private static ResourceManager resourceManager;
	
	public static ScreenStyle loadStyleDoc(ResourceLocation path)
	{
		ScreenStyle style;
		
		try
		{
			style = loadStyleDocInternal(path);
		} catch(FileNotFoundException e)
		{
			throw new RuntimeException("Failed to find Screen JSON file: " + path + ": " + e.getMessage());
		} catch(Exception e)
		{
			throw new RuntimeException("Failed to read Screen JSON file: " + path, e);
		}
		
		// We only require the final style-document to be fully valid,
		// includes are allowed to be partially valid.
		style.validate();
		return style;
	}
	
	private static JsonObject loadMergedJsonTree(ResourceLocation path, Set<String> loadedFiles, Set<String> resourcePacks)
			throws IOException
	{
		if(!loadedFiles.add(path.toString()))
		{
			throw new IllegalStateException("Recursive style includes: " + loadedFiles);
		}
		
		if(resourceManager == null)
		{
			throw new IllegalStateException("ResourceManager was not set. Was initialize called?");
		}
		
		JsonObject document;
		
		var resource = resourceManager.getResource(path)
				.orElseThrow(() -> new FileNotFoundException(path.toString()));
		
		resourcePacks.add(resource.sourcePackId());
		try(var reader = resourceManager.openAsReader(path))
		{
			document = ScreenStyle.GSON.fromJson(reader, JsonObject.class);
		}
		
		String basePath = "/screens/";
		
		// Resolve the includes present in the document
		if(document.has(PROP_INCLUDES))
		{
			String[] includes = ScreenStyle.GSON.fromJson(document.get(PROP_INCLUDES), String[].class);
			List<JsonObject> layers = new ArrayList<>();
			for(String include : includes)
			{
				if(include.contains(":"))
					layers.add(loadMergedJsonTree(new ResourceLocation(include), loadedFiles, resourcePacks));
				else
					layers.add(StyleManagerAccessor.callLoadMergedJsonTree(basePath + include, loadedFiles, resourcePacks));
			}
			layers.add(document);
			document = combineLayers(layers);
		}
		
		return document;
		
	}
	
	// Builds a new JSON document from layered documents
	private static JsonObject combineLayers(List<JsonObject> layers)
	{
		JsonObject result = new JsonObject();
		
		// Start by copying over all properties layer-by-layer while overwriting properties set by
		// previous layers.
		for(JsonObject layer : layers)
		{
			for(Map.Entry<String, JsonElement> entry : layer.entrySet())
			{
				result.add(entry.getKey(), entry.getValue());
			}
		}
		
		// Merge the following keys by merging their properties
		mergeObjectKeys("slots", layers, result);
		mergeObjectKeys("text", layers, result);
		mergeObjectKeys("palette", layers, result);
		mergeObjectKeys("images", layers, result);
		mergeObjectKeys("terminalStyle", layers, result);
		mergeObjectKeys("widgets", layers, result);
		
		return result;
	}
	
	/**
	 * Merges a single object property across multiple layers by merging the object keys. Higher layers win when there
	 * is a conflict.
	 */
	private static void mergeObjectKeys(String propertyName, List<JsonObject> layers, JsonObject target)
			throws JsonParseException
	{
		JsonObject mergedObject = null;
		for(JsonObject layer : layers)
		{
			JsonElement layerEl = layer.get(propertyName);
			if(layerEl != null)
			{
				if(!layerEl.isJsonObject())
					throw new JsonParseException("Expected " + propertyName + " to be an object, but was: " + layerEl);
				
				JsonObject layerObj = layerEl.getAsJsonObject();
				
				if(mergedObject == null)
				{
					mergedObject = new JsonObject();
				}
				for(Map.Entry<String, JsonElement> entry : layerObj.entrySet())
				{
					mergedObject.add(entry.getKey(), entry.getValue());
				}
			}
		}
		
		if(mergedObject != null)
		{
			target.add(propertyName, mergedObject);
		}
	}
	
	private static ScreenStyle loadStyleDocInternal(ResourceLocation path) throws IOException
	{
		ScreenStyle style = styleCache.get(path);
		if(style != null)
		{
			return style;
		}
		
		Set<String> resourcePacks = new HashSet<>();
		try
		{
			JsonObject document = loadMergedJsonTree(path, new HashSet<>(), resourcePacks);
			style = ScreenStyle.GSON.fromJson(document, ScreenStyle.class);
			style.validate();
		} catch(IOException e)
		{
			throw e;
		} catch(Exception e)
		{
			throw new JsonParseException("Failed to load style from " + path + " (packs: " + resourcePacks + ")", e);
		}
		
		styleCache.put(path, style);
		return style;
	}
	
	public static void initialize(ResourceManager resourceManager)
	{
		if(resourceManager instanceof ReloadableResourceManager r)
			r.registerReloadListener(new ReloadListener());
		setResourceManager(resourceManager);
	}
	
	private static void setResourceManager(ResourceManager resourceManager)
	{
		StyleManagerEA.resourceManager = resourceManager;
		StyleManagerEA.styleCache.clear();
	}
	
	private static class ReloadListener
			implements ResourceManagerReloadListener
	{
		@Override
		public void onResourceManagerReload(ResourceManager p_10758_)
		{
			setResourceManager(resourceManager);
		}
	}
}