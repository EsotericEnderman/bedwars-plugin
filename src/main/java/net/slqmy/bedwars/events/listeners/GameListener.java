package net.slqmy.bedwars.events.listeners;

import net.slqmy.bedwars.Bedwars;
import net.slqmy.bedwars.enums.GameState;
import net.slqmy.bedwars.enums.Team;
import net.slqmy.bedwars.types.Arena;
import net.slqmy.bedwars.types.Game;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class GameListener implements Listener {
	private final Bedwars plugin;

	public GameListener(@NotNull final Bedwars plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(@NotNull final BlockBreakEvent event) {
		final Arena arena = plugin.getArenaManager().getArena(event.getPlayer().getUniqueId());

		if (arena != null && arena.getState() == GameState.PLAYING) {
			final Game game = arena.getGame();
			final Block block = event.getBlock();

			final List<MetadataValue> values = block.getMetadata("team");

			if (values.size() != 0) {
				final String teamName = values.get(0).asString();
				final Team team = Team.valueOf(teamName);

				event.setCancelled(!game.destroyBed(team, event.getPlayer()));
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(@NotNull final PlayerDeathEvent event) {
		final Player player = event.getEntity();
		final Arena arena = plugin.getArenaManager().getArena(player.getUniqueId());

		if (arena != null && arena.getState() == GameState.PLAYING) {
			arena.getGame().handleDeath(player);
		}
	}

	@EventHandler
	public void onPlayerRespawn(@NotNull final PlayerRespawnEvent event) {
		final Arena arena = plugin.getArenaManager().getArena(event.getPlayer().getUniqueId());

		if (arena != null && arena.getState() == GameState.PLAYING) {
			event.setRespawnLocation(arena.getGame().getRespawnLocation(event.getPlayer()));
		}
	}
}
