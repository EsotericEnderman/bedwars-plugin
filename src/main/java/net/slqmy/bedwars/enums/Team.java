package net.slqmy.bedwars.enums;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public enum Team {
	RED("Red", ChatColor.RED),
	YELLOW("Yellow", ChatColor.YELLOW),
	GREEN("Green", ChatColor.GREEN),
	BLUE("Blue", ChatColor.BLUE);

	private final String name;
	private final ChatColor colourString;

	Team(@NotNull final String name, @NotNull final ChatColor colourString) {
		this.name = name;
		this.colourString = colourString;
	}

	public String getName() {
		return name;
	}

	public ChatColor getColour() {
		return colourString;
	}
}
