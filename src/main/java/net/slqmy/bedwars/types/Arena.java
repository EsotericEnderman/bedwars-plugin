package net.slqmy.bedwars.types;

import net.slqmy.bedwars.Bedwars;
import net.slqmy.bedwars.enums.GameState;
import net.slqmy.bedwars.enums.Team;
import net.slqmy.bedwars.utility.ConfigurationUtility;
import net.slqmy.bedwars.utility.types.BedLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Arena {
	private final Bedwars plugin;

	private final int id;
	private final Location spawn;
	private final HashMap<Team, Location> spawns;
	private final HashMap<Team, BedLocation> bedLocations;
	private final double voidLevel;
	private final List<UUID> players = new ArrayList<>();

	private GameState state = GameState.WAITING;
	private Game game;
	private Countdown countdown;

	public Arena(@NotNull final Bedwars plugin, final int id, @NotNull final Location spawn, @NotNull final HashMap<Team, Location> spawns, @NotNull final HashMap<Team, BedLocation> bedLocations, final double voidLevel) {
		this.plugin = plugin;

		this.id = id;

		this.spawn = spawn;
		this.spawns = spawns;
		this.bedLocations = bedLocations;

		this.voidLevel = voidLevel;

		this.game = new Game(plugin, this);
		this.countdown = new Countdown(plugin, this);
	}

	public void start() {
		game.start();
	}

	public void reset(final boolean kickPlayers) {
		state = GameState.WAITING;

		countdown.cancel();

		countdown = new Countdown(plugin, this);
		game.cancelTasks();
		game = new Game(plugin, this);

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

	public void addPlayer(@NotNull final Player player) {
		players.add(player.getUniqueId());

		player.teleport(spawn);
		player.getInventory().clear();

		player.sendTitle("", "", 0, 0, 0);

		if (state == GameState.WAITING && players.size() >= ConfigurationUtility.getRequiredPlayers()) {
			countdown.start();
		}
	}

	public void removePlayer(@NotNull final Player player) {
		players.remove(player.getUniqueId());
		player.teleport(ConfigurationUtility.getLobbySpawn());
		player.getInventory().clear();

		if (players.size() < ConfigurationUtility.getRequiredPlayers()) {
			if (state == GameState.COUNTDOWN) {
				reset(false);

				sendMessage(ChatColor.RED + "There are not enough players! Countdown cancelled.");
				sendTitle(ChatColor.RED + "Countdown cancelled!");
			} else if (state == GameState.PLAYING) {
				reset(false);

				sendMessage(ChatColor.RED + "The game has ended because too many players have left.");
			}
		}
	}

	public void sendMessage(@NotNull final String message) {
		for (final UUID uuid : players) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			player.sendMessage(message);
		}
	}

	public void sendTitle(@NotNull final String title) {
		for (final UUID uuid : players) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			player.sendTitle(title, "", 10, 10, 10);
		}
	}

	public int getID() {
		return id;
	}

	public double getVoidLevel() {
		return voidLevel;
	}

	public HashMap<Team, Location> getSpawns() {
		return spawns;
	}

	public HashMap<Team, BedLocation> getBedLocations() {
		return bedLocations;
	}

	public GameState getState() {
		return state;
	}

	public void setState(@NotNull final GameState state) {
		this.state = state;
	}

	public List<UUID> getPlayers() {
		return players;
	}

	public Game getGame() {
		return game;
	}
}
