package org.zeith.equivadds.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.zeith.equivadds.EquivalentAdditions;
import org.zeith.hammerlib.annotations.Setup;

public interface TagsEA
{
	interface Blocks
	{
		TagKey<Block> NEEDS_BLUE_MATTER_TOOL = tag("needs_blue_matter_tool");
		TagKey<Block> NEEDS_PURPLE_MATTER_TOOL = tag("needs_purple_matter_tool");
		TagKey<Block> NEEDS_ORANGE_MATTER_TOOL = tag("needs_orange_matter_tool");
		TagKey<Block> NEEDS_GREEN_MATTER_TOOL = tag("needs_green_matter_tool");
		
		private static TagKey<Block> tag(String name)
		{
			return BlockTags.create(EquivalentAdditions.id(name));
		}
		
		private static TagKey<Block> forgeTag(String name)
		{
			return BlockTags.create(new ResourceLocation("forge", name));
		}
		
		@Setup
		static void setup()
		{
		}
	}
}