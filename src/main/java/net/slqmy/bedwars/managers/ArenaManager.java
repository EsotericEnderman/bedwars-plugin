package net.slqmy.bedwars.managers;

import net.slqmy.bedwars.Bedwars;
import net.slqmy.bedwars.enums.Team;
import net.slqmy.bedwars.types.Arena;
import net.slqmy.bedwars.utility.types.BedLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ArenaManager {
	private final List<Arena> arenas = new ArrayList<>();

	public ArenaManager(@NotNull final Bedwars plugin) {
		final FileConfiguration config = plugin.getConfig();
		final ConfigurationSection arenasList = config.getConfigurationSection("arenas");
		assert arenasList != null;

		for (final String arenaKey : arenasList.getKeys(false)) {
			final String worldName = config.getString("arenas." + arenaKey + ".world-name");
			assert worldName != null;

			final World world = Bukkit.getWorld(worldName);

			final ConfigurationSection teams = config.getConfigurationSection("arenas." + arenaKey + ".teams");
			assert teams != null;

			final HashMap<Team, Location> spawns = new HashMap<>();
			final HashMap<Team, BedLocation> bedLocations = new HashMap<>();

			for (String team : teams.getKeys(false)) {
				spawns.put(
								Team.valueOf(team.toUpperCase()),
								new Location(
												world,
												config.getDouble("arenas." + arenaKey + ".teams." + team + ".spawn.x"),
												config.getDouble("arenas." + arenaKey + ".teams." + team + ".spawn.y"),
												config.getDouble("arenas." + arenaKey + ".teams." + team + ".spawn.z"),
												(float) config.getDouble("arenas." + arenaKey + ".teams." + team + ".spawn.yaw"),
												(float) config.getDouble("arenas." + arenaKey + ".teams." + team + ".spawn.pitch")
								)
				);

				final String facing = config.getString("arenas." + arenaKey + ".teams." + team + ".bed-location.facing");
				assert facing != null;

				bedLocations.put(
								Team.valueOf(team.toUpperCase()),
								new BedLocation(
												world,
												config.getDouble("arenas." + arenaKey + ".teams." + team + ".bed-location.x"),
												config.getDouble("arenas." + arenaKey + ".teams." + team + ".bed-location.y"),
												config.getDouble("arenas." + arenaKey + ".teams." + team + ".bed-location.z"),
												BlockFace.valueOf(facing.toUpperCase())
								)
				);
			}

			final String spawnWorldName = config.getString("arenas." + arenaKey + ".spawn.world-name");
			assert spawnWorldName != null;

			arenas.add(
							new Arena(
											plugin,
											Integer.parseInt(arenaKey),
											new Location(
															Bukkit.getWorld(spawnWorldName),
															config.getDouble("arenas." + arenaKey + ".spawn.x"),
															config.getDouble("arenas." + arenaKey + ".spawn.y"),
															config.getDouble("arenas." + arenaKey + ".spawn.z"),
															(float) config.getDouble("arenas." + arenaKey + ".spawn.yaw"),
															(float) config.getDouble("arenas." + arenaKey + ".spawn.pitch")

											),
											spawns,
											bedLocations,
											config.getDouble("arenas." + arenaKey + ".void-level")
							)
			);
		}
	}

	public List<Arena> getArenas() {
		return arenas;
	}

	public @Nullable Arena getArena(@NotNull final Player player) {
		for (final Arena arena : arenas) {
			if (arena.getPlayers().contains(player.getUniqueId())) {
				return arena;
			}
		}

		return null;
	}

	public @Nullable Arena getArena(final int id) {
		for (final Arena arena : arenas) {
			if (arena.getID() == id) {
				return arena;
			}
		}

		return null;
	}
}
