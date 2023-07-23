package net.slqmy.bedwars.enums;

import org.jetbrains.annotations.NotNull;

public enum Team {
	RED("Red"),
	BLUE("Blue"),
	GREEN("Green"),
	YELLOW("Yellow");

	private final String name;

	Team(@NotNull final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
