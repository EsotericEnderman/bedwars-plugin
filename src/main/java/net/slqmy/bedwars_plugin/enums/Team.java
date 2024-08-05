package net.slqmy.bedwars_plugin.enums;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.jetbrains.annotations.NotNull;

public enum Team {
	RED("Red", ChatColor.RED, BarColor.RED),
	YELLOW("Yellow", ChatColor.YELLOW, BarColor.YELLOW),
	GREEN("Green", ChatColor.GREEN, BarColor.GREEN),
	BLUE("Blue", ChatColor.BLUE, BarColor.BLUE);

	private final String name;
	private final ChatColor colourString;
	private final BarColor bossBarColour;

	Team(@NotNull final String name, @NotNull final ChatColor colourString, @NotNull final BarColor bossBarColour) {
		this.name = name;
		this.colourString = colourString;
		this.bossBarColour = bossBarColour;
	}

	public String getName() {
		return name;
	}

	public ChatColor getColour() {
		return colourString;
	}

	public BarColor getBossBarColour() {
		return bossBarColour;
	}
}
