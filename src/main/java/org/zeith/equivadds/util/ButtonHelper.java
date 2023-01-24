package org.zeith.equivadds.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ButtonHelper
{
	public static Button.OnTooltip tooltip(Screen screen, Component component)
	{
		return new Button.OnTooltip()
		{
			@Override
			public void onTooltip(Button btn, PoseStack pose, int x, int y)
			{
				screen.renderTooltip(pose, Minecraft.getInstance().font.split(component, Math.max(screen.width / 4 - 43, 170)), x, y);
			}
			
			@Override
			public void narrateTooltip(Consumer<Component> consumer)
			{
				consumer.accept(component);
			}
		};
	}
}