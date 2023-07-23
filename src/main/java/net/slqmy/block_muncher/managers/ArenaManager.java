package net.slqmy.block_muncher.managers;

import net.slqmy.block_muncher.Bedwars;
import net.slqmy.block_muncher.types.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ArenaManager {
	private final List<Arena> arenas = new ArrayList<>();

	public ArenaManager(@NotNull final Bedwars plugin) {
		final FileConfiguration config = plugin.getConfig();
		final ConfigurationSection arenasList = config.getConfigurationSection("arenas");
		assert arenasList != null;

		for (final String key : arenasList.getKeys(false)) {
			final String worldName = config.getString("arenas." + key + ".world-name");
			assert worldName != null;

			arenas.add(
					new Arena(
							plugin,
							Integer.parseInt(key),
							new Location(
									Bukkit.getWorld(worldName),
									config.getDouble("arenas." + key + ".x"),
									config.getDouble("arenas." + key + ".y"),
									config.getDouble("arenas." + key + ".z"),
									(float) config.getDouble("arenas." + key + ".yaw"),
									(float) config.getDouble("arenas." + key + ".pitch")

							)));
		}
	}

	public List<Arena> getArenas() {
		return arenas;
	}

	public @Nullable Arena getArena(@NotNull final UUID uuid) {
		for (final Arena arena : arenas) {
			if (arena.getPlayers().contains(uuid)) {
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
