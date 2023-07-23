package net.slqmy.block_muncher.types;

import net.slqmy.block_muncher.enums.GameState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public final class Game {
	private final Arena arena;
	private final HashMap<UUID, Integer> points = new HashMap<>();

	public Game(@NotNull final Arena arena) {
		this.arena = arena;
	}

	public void start() {
		arena.setState(GameState.PLAYING);
		arena.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!");
		arena.sendMessage(ChatColor.GREEN + "Eat (break) " + ChatColor.YELLOW + ChatColor.UNDERLINE + "20" + ChatColor.GREEN
				+ " blocks as fast as possible to win!");

		for (final UUID uuid : arena.getPlayers()) {
			points.put(uuid, 0);
		}
	}

	public void addPoint(@NotNull final Player player) {
		final UUID uuid = player.getUniqueId();
		final int playerPoints = points.get(uuid) + 1;

		if (playerPoints == 20) {
			arena.sendMessage(
					ChatColor.GOLD.toString() + ChatColor.UNDERLINE + player.getName() + " " + ChatColor.GREEN
							+ "has won the game! Thank you for playing.");
			arena.sendTitle(ChatColor.RED.toString() + ChatColor.BOLD + "GAME OVER!");

			player.sendTitle(ChatColor.GOLD.toString() + ChatColor.BOLD + "VICTORY!", "", 10, 10, 10);

			arena.reset(true);

			return;
		}

		player.sendMessage(ChatColor.YELLOW + "+1" + ChatColor.GREEN + " Point");
		points.replace(uuid, playerPoints);
	}
}
