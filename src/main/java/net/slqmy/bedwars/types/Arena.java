package net.slqmy.bedwars.types;

import net.slqmy.bedwars.Bedwars;
import net.slqmy.bedwars.enums.GameState;
import net.slqmy.bedwars.enums.Team;
import net.slqmy.bedwars.utility.ConfigurationUtility;
import net.slqmy.bedwars.utility.types.BedLocation;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Arena {
	private final Bedwars plugin;

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

	public Arena(@NotNull final Bedwars plugin, final int id, @NotNull final Location spawnLocation, @NotNull final HashMap<Team, Location> spawns, @NotNull final HashMap<Team, BedLocation> bedLocations, @NotNull final Location npcLocation, final double voidLevel) {
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

		countdown = new Countdown(plugin, this);
		game.cancelTasks();
		game = new Game(plugin, this);

		if (kickPlayers) {

			for (final UUID uuid : players) {
				final Player player = Bukkit.getPlayer(uuid);
				assert player != null;

				player.teleport(ConfigurationUtility.getLobbySpawn());
				player.getInventory().clear();
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
		villager.setCustomName(
						ChatColor.GOLD + "Bedwars Arena " + ChatColor.YELLOW + ChatColor.UNDERLINE + "#" + id + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "(" + ChatColor.YELLOW + players.size() + ChatColor.GRAY + " players) " + ChatColor.DARK_GRAY + "- " + ChatColor.YELLOW + "Click to join!"
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
		this.isWorldLoaded = !isWorldLoaded;
	}
}
