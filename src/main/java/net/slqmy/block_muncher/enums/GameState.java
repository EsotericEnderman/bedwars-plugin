package net.slqmy.block_muncher.enums;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public enum GameState {
	WAITING(ChatColor.GREEN + "Waiting for players..."),
	COUNTDOWN(ChatColor.YELLOW + "Game starting!"),
	PLAYING(ChatColor.RED + "Game active.");

	private final String name;

	GameState(@NotNull final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
