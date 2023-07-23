package net.slqmy.block_muncher.events.listeners;

import net.slqmy.block_muncher.BlockMuncher;
import net.slqmy.block_muncher.types.Arena;
import net.slqmy.block_muncher.utility.ConfigurationUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class ConnectionListener implements Listener {
	private final BlockMuncher plugin;

	public ConnectionListener(@NotNull final BlockMuncher plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(@NotNull final PlayerJoinEvent event) {
		event.getPlayer().teleport(ConfigurationUtility.getLobbySpawn());
	}

	@EventHandler
	public void onPlayerQuit(@NotNull final PlayerQuitEvent event) {
		final Player player = event.getPlayer();

		final Arena arena = plugin.getArenaManager().getArena(player.getUniqueId());

		if (arena != null) {
			arena.removePlayer(player);
		}
	}
}
