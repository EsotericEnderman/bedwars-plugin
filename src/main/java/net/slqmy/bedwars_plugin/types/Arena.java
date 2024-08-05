package net.slqmy.bedwars_plugin.types;

import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import net.slqmy.bedwars_plugin.BedwarsPlugin;
import net.slqmy.bedwars_plugin.enums.GameState;
import net.slqmy.bedwars_plugin.enums.Team;
import net.slqmy.bedwars_plugin.utility.ConfigurationUtility;
import net.slqmy.bedwars_plugin.utility.types.BedLocation;

import java.util.*;

public final class Arena {
	private final BedwarsPlugin plugin;

	private final int id;
	private final Location spawnLocation;
	private final HashMap<Team, Location> spawns;
	private final HashMap<Team, BedLocation> bedLocations;
	private final double voidLevel;
	private final List<UUID> players = new ArrayList<>();
	private final Villager villager;

	private boolean isWorldLoaded = true;

	private GameState state = GameState.WAITING;
	private Game game;
	private Countdown countdown;

	public Arena(@NotNull final BedwarsPlugin plugin, final int id, @NotNull final Location spawnLocation, @NotNull final HashMap<Team, Location> spawns, @NotNull final HashMap<Team, BedLocation> bedLocations, @NotNull final Location npcLocation, final double voidLevel) {
		this.plugin = plugin;

		this.id = id;

		this.spawnLocation = spawnLocation;
		this.spawns = spawns;
		this.bedLocations = bedLocations;

		final World world = npcLocation.getWorld();
		assert world != null;
		villager = (Villager) world.spawnEntity(npcLocation, EntityType.VILLAGER);

		villager.setAI(false);
		villager.setInvulnerable(true);
		villager.setCollidable(false); // Doesn't seem to do anything.
		villager.setSilent(true);

		villager.setCustomNameVisible(true);
		villager.setProfession(Villager.Profession.FARMER);

		updateVillager();

		this.voidLevel = voidLevel;

		this.game = new Game(plugin, this);
		this.countdown = new Countdown(plugin, this);
	}

	public void start() {
		game.start();
	}

	public void reset(final boolean kickPlayers) {
		countdown.cancel();


		if (kickPlayers) {
			for (final UUID uuid : players) {
				final Player player = Bukkit.getPlayer(uuid);
				assert player != null;

				player.teleport(ConfigurationUtility.getLobbySpawn());
				player.getInventory().clear();

				final BossBar playerBossBar = plugin.getArenaManager().getBossBars().get(game.getTeams().get(player.getUniqueId()));
				player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());

				if (playerBossBar != null) {
					playerBossBar.removePlayer(player);
				}
			}

			players.clear();

			final World arenaWorld = spawnLocation.getWorld();
			assert arenaWorld != null;

			arenaWorld.setAutoSave(false);

			boolean success = Bukkit.unloadWorld(arenaWorld, false);
			isWorldLoaded = !success;

			final World reloadedWorld = Bukkit.createWorld(new WorldCreator(arenaWorld.getName()));
			assert reloadedWorld != null;

			reloadedWorld.setAutoSave(false);

			Bukkit.reload();
		}

		countdown = new Countdown(plugin, this);
		game.cancelTasks();
		game = new Game(plugin, this);

		state = GameState.WAITING;

		updateVillager();
	}

	public void addPlayer(@NotNull final Player player) {
		players.add(player.getUniqueId());

		player.teleport(spawnLocation);

		updateVillager();

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

		final BossBar playerBossBar = plugin.getArenaManager().getBossBars().get(game.getTeams().get(player.getUniqueId()));

		if (playerBossBar != null) {
			playerBossBar.removePlayer(player);
		}

		player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());

		if (players.size() < ConfigurationUtility.getRequiredPlayers()) {
			if (state == GameState.COUNTDOWN) {
				sendMessage(ChatColor.RED + "There are not enough players! Countdown cancelled.");
				sendTitle(ChatColor.RED + "Countdown cancelled!");
				reset(false);
			} else if (state == GameState.PLAYING) {
				sendMessage(ChatColor.RED + "The game has ended because too many players have left.");
				reset(true);
			}
		}

		updateVillager();
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

	public void updateVillager() {
		final int playerCount = players.size();

		villager.setCustomName(
						ChatColor.GOLD + "Bedwars Arena " + ChatColor.YELLOW + ChatColor.UNDERLINE + "#" + id + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "(" + ChatColor.YELLOW + playerCount + ChatColor.GRAY + " player" + (playerCount == 1 ? "" : "s") + ") " + ChatColor.DARK_GRAY + "- " + ChatColor.YELLOW + "Click to join!"
		);
	}

	public int getID() {
		return id;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
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

	public boolean isWorldLoaded() {
		return isWorldLoaded;
	}

	public Villager getVillager() {
		return villager;
	}

	public Game getGame() {
		return game;
	}

	public void toggleIsWorldLoaded() {
		isWorldLoaded = !isWorldLoaded;
	}
}
