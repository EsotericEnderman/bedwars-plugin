package dev.enderman.minecraft.plugins.bedwars.events.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import dev.enderman.minecraft.plugins.bedwars.BedwarsPlugin;
import dev.enderman.minecraft.plugins.bedwars.managers.NameTagManager;
import dev.enderman.minecraft.plugins.bedwars.types.Arena;
import dev.enderman.minecraft.plugins.bedwars.utility.ConfigurationUtility;

public final class ConnectionListener implements Listener {
	private final BedwarsPlugin plugin;

	public ConnectionListener(@NotNull final BedwarsPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(@NotNull final PlayerJoinEvent event) {
		event.getPlayer().teleport(ConfigurationUtility.getLobbySpawn());
	}

	@EventHandler
	public void onPlayerQuit(@NotNull final PlayerQuitEvent event) {
		final Player player = event.getPlayer();

		final Arena arena = plugin.getArenaManager().getArena(player);

		if (arena != null) {
			arena.removePlayer(player);
		}

		NameTagManager.removeTag(player);
	}
}
