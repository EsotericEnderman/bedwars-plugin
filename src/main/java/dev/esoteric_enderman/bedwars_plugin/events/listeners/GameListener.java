package dev.esoteric_enderman.bedwars_plugin.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import dev.esoteric_enderman.bedwars_plugin.BedwarsPlugin;
import dev.esoteric_enderman.bedwars_plugin.enums.GameState;
import dev.esoteric_enderman.bedwars_plugin.enums.Team;
import dev.esoteric_enderman.bedwars_plugin.types.Arena;
import dev.esoteric_enderman.bedwars_plugin.types.Game;

import java.util.List;

public final class GameListener implements Listener {
	private final BedwarsPlugin plugin;

	public GameListener(@NotNull final BedwarsPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onWorldLoad(@NotNull final WorldLoadEvent event) {
		final Arena arena = plugin.getArenaManager().getArena(event.getWorld());

		if (arena != null) {
			arena.toggleIsWorldLoaded();
		}
	}

	// PlayerInteractAtEntityEvent does not work for some reason.
	@EventHandler
	public void onPlayerInteractEntity(@NotNull final PlayerInteractEntityEvent event) {
		final Entity clickedEntity = event.getRightClicked();

		if (event.getHand() == EquipmentSlot.OFF_HAND || clickedEntity.getType() != EntityType.VILLAGER) {
			return;
		}

		final Arena arena = plugin.getArenaManager().getArena(clickedEntity.getUniqueId());

		if (arena != null) {
			Bukkit.dispatchCommand(event.getPlayer(), "bedwars join " + arena.getID());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(@NotNull final EntityDamageByEntityEvent event) {

		if (event.getDamager() instanceof final Player attacker) {
			final Arena arena = plugin.getArenaManager().getArena(attacker);

			if (arena != null && arena.getState() != GameState.PLAYING) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(@NotNull final BlockBreakEvent event) {
		final Arena arena = plugin.getArenaManager().getArena(event.getPlayer());

		if (arena == null) {
			return;
		}

		if (arena.getState() == GameState.PLAYING) {
			final Game game = arena.getGame();
			final Block block = event.getBlock();

			final List<MetadataValue> values = block.getMetadata("team");

			if (values.size() != 0) {
				final String teamName = values.get(0).asString();
				final Team team = Team.valueOf(teamName);

				event.setCancelled(!game.destroyBed(team, event.getPlayer()));
			}
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(@NotNull final PlayerDeathEvent event) {
		final Player player = event.getEntity();
		final Arena arena = plugin.getArenaManager().getArena(player);

		if (arena != null && arena.getState() == GameState.PLAYING) {
			arena.getGame().handleDeath(player);
		}
	}

	@EventHandler
	public void onPlayerRespawn(@NotNull final PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		final Arena arena = plugin.getArenaManager().getArena(player);

		if (arena == null) {
			return;
		}

		event.setRespawnLocation(
						arena.getState() == GameState.PLAYING ?
										arena.getGame().getRespawnLocation(player)
										: arena.getSpawnLocation()
		);
	}
}
