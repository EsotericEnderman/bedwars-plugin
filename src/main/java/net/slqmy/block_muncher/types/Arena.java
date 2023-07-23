package net.slqmy.block_muncher.types;

import net.slqmy.block_muncher.BlockMuncher;
import net.slqmy.block_muncher.enums.GameState;
import net.slqmy.block_muncher.utility.ConfigurationUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Arena {
	private final BlockMuncher plugin;

	private final int id;
	private final Location spawn;

	private GameState state = GameState.WAITING;
	private final List<UUID> players = new ArrayList<>();
	private Game game;
	private Countdown countdown;

	public Arena(@NotNull final BlockMuncher plugin, final int id, @NotNull final Location spawn) {
		this.plugin = plugin;

		this.id = id;
		this.spawn = spawn;

		this.game = new Game(this);
		this.countdown = new Countdown(plugin, this);
	}

	public void start() {
		game.start();
	}

	public void reset(final boolean kickPlayers) {
		state = GameState.WAITING;

		countdown.cancel();

		countdown = new Countdown(plugin, this);
		game = new Game(this);

		if (kickPlayers) {
			final Location location = ConfigurationUtility.getLobbySpawn();

			for (final UUID uuid : players) {
				final Player player = Bukkit.getPlayer(uuid);
				assert player != null;

				player.teleport(location);
			}

			players.clear();
		}
	}

	public void sendMessage(@NotNull final String message) {
		for (final UUID uuid : players) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			player.sendMessage(message);
		}
	}

	public void sendTitle(@NotNull final String title, @NotNull final String subtitle) {
		for (final UUID uuid : players) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			player.sendTitle(title, subtitle, 10, 10, 10);
		}
	}

	public void sendTitle(@NotNull final String title) {
		for (final UUID uuid : players) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			player.sendTitle(title, "", 10, 10, 10);
		}
	}

	public void addPlayer(@NotNull final Player player) {
		players.add(player.getUniqueId());

		player.teleport(spawn);

		if (state == GameState.WAITING && players.size() >= ConfigurationUtility.getMinPlayers()) {
			countdown.start();
		}
	}

	public void removePlayer(@NotNull final Player player) {
		players.remove(player.getUniqueId());

		if (players.size() < ConfigurationUtility.getMinPlayers()) {
			if (state == GameState.COUNTDOWN) {
				reset(false);

				sendMessage(ChatColor.RED + "There are not enough players! Countdown cancelled.");
				sendTitle(ChatColor.RED + "Countdown cancelled!");
			} else if (state == GameState.PLAYING) {
				reset(false);

				sendMessage(ChatColor.RED + "The game has ended because too many players have left.");
			}
		}

		player.teleport(ConfigurationUtility.getLobbySpawn());
	}

	public int getID() {
		return id;
	}

	public GameState getState() {
		return state;
	}

	public List<UUID> getPlayers() {
		return players;
	}

	public Game getGame() {
		return game;
	}

	public void setState(@NotNull final GameState state) {
		this.state = state;
	}
}
