package dev.enderman.minecraft.plugins.games.bedwars.utility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import dev.enderman.minecraft.plugins.games.bedwars.BedwarsPlugin;

public final class ConfigurationUtility {
	private static FileConfiguration config;

	public static void setUpConfig(@NotNull final BedwarsPlugin plugin) {
		config = plugin.getConfig();
		plugin.saveDefaultConfig();
	}

	public static int getRequiredPlayers() {
		return config.getInt("required-players");
	}

	public static int getCountdownSeconds() {
		return config.getInt("countdown-seconds");
	}

	public static @NotNull Location getLobbySpawn() {
		final String worldName = config.getString("lobby-spawn.world-name");
		assert worldName != null;

		return new Location(
						Bukkit.getWorld(worldName),
						config.getDouble("lobby-spawn.x"),
						config.getDouble("lobby-spawn.y"),
						config.getDouble("lobby-spawn.z"),
						(float) config.getDouble("lobby-spawn.yaw"),
						(float) config.getDouble("lobby-spawn.pitch")
		);
	}
}
