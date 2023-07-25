package net.slqmy.bedwars.types;

import net.slqmy.bedwars.Bedwars;
import net.slqmy.bedwars.enums.GameState;
import net.slqmy.bedwars.enums.Team;
import net.slqmy.bedwars.utility.ConfigurationUtility;
import net.slqmy.bedwars.utility.types.BedLocation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Game {
	private final Bedwars plugin;

	private final Arena arena;
	private final HashMap<UUID, Team> teams = new HashMap<>();
	private final HashMap<Team, Boolean> bedsAlive = new HashMap<>();
	private final List<UUID> alive = new ArrayList<>();
	private final List<BukkitTask> tasks = new ArrayList<>();

	public Game(@NotNull final Bedwars plugin, @NotNull final Arena arena) {
		this.plugin = plugin;
		this.arena = arena;
	}

	public void start() {
		arena.setState(GameState.PLAYING);
		arena.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!");
		arena.sendMessage(ChatColor.DARK_GRAY + " \n| " + ChatColor.GREEN + "Break other players' beds and kill them to win!\n ");

		for (int i = 0; i < arena.getPlayers().size(); i++) {
			final UUID uuid = arena.getPlayers().get(i);
			final Team team = Team.values()[i];

			teams.put(uuid, team);
			bedsAlive.put(team, true);

			final BedLocation bedLocation = arena.getBedLocations().get(team);
			Block block = bedLocation.getBlock();

			// Idea: make a create bed utility method lol.
			for (final Bed.Part bedPart : Bed.Part.values()) {
				final Bed data = (Bed) Material.RED_BED.createBlockData();

				data.setPart(bedPart);
				data.setFacing(bedLocation.getFacing());

				block.setBlockData(data);

				block.setMetadata("team", new FixedMetadataValue(plugin, team.name()));
				block = block.getRelative(bedLocation.getFacing().getOppositeFace());
			}

			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			player.teleport(arena.getSpawns().get(team));
			player.setGameMode(GameMode.SURVIVAL);

			final PlayerInventory inventory = player.getInventory();

			inventory.clear();
			inventory.addItem(new ItemStack(Material.WOODEN_SWORD));

			inventory.setHelmet(new ItemStack(Material.LEATHER_HELMET));
			inventory.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
			inventory.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			inventory.setBoots(new ItemStack(Material.LEATHER_BOOTS));

			alive.add(uuid);
		}

		tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			for (final UUID uuid : alive) {
				final Player player = Bukkit.getPlayer(uuid);
				assert player != null;

				if (player.getLocation().getY() <= arena.getVoidLevel()) {
					player.setFallDistance(0);
					handleDeath(player);
				}
			}
		}, 40, 4));
	}

	public boolean destroyBed(@NotNull final Team team, @NotNull final Player breaker) {
		final Team breakerTeam = teams.get(breaker.getUniqueId());

		if (teams.get(breaker.getUniqueId()).equals(team)) {
			return false;
		}

		arena.sendMessage(
						ChatColor.DARK_GRAY + " \n| " + breakerTeam.getColour() + breaker.getName()
						+ ChatColor.YELLOW + " has broken " + team.getColour() + team.getName() + " bed" + ChatColor.YELLOW + "!\n "
		);

		bedsAlive.replace(team, false);

		return true;
	}

	public void handleDeath(@NotNull final Player noob) {
		final Team team = teams.get(noob.getUniqueId());

		if (bedsAlive.get(team)) {
			noob.teleport(arena.getSpawns().get(team));
			arena.sendMessage(team.getColour() + noob.getName() + ChatColor.YELLOW + " died!");
		} else {
			noob.teleport(ConfigurationUtility.getLobbySpawn());
			arena.sendMessage(team.getColour() + noob.getName() + ChatColor.YELLOW + " has been eliminated!");

			alive.remove(noob.getUniqueId());
		}

		if (alive.size() == 1) {
			final Player pro = Bukkit.getPlayer(alive.get(0));
			assert pro != null;

			final Team proTeam = teams.get(pro.getUniqueId());

			arena.sendMessage(proTeam.getColour() + pro.getName() + ChatColor.YELLOW + " has won!");

			arena.sendTitle(ChatColor.RED.toString() + ChatColor.BOLD + "GAME OVER!");
			pro.sendTitle(ChatColor.GOLD.toString() + ChatColor.BOLD + "VICTORY!", "", 20, 20, 20);

			arena.reset();
		}
	}

	public Location getRespawnLocation(@NotNull final Player player) {
		final Team team = teams.get(player.getUniqueId());

		if (bedsAlive.get(team)) {
			return arena.getSpawns().get(team);
		} else {
			return ConfigurationUtility.getLobbySpawn();
		}
	}

	public void cancelTasks() {
		for (final BukkitTask task : tasks) {
			task.cancel();
		}
	}
}
