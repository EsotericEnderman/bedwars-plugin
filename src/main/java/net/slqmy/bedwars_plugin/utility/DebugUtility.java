package net.slqmy.bedwars_plugin.utility;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class DebugUtility {
	private static final Logger LOGGER = Bukkit.getLogger();

	public static void log(@Nullable final Object... values) {
		for (final Object value : values) {
			LOGGER.log(
							Level.WARNING, PluginUtility.getLogPrefix() + " [DEBUG] " + (value == null ? "NULL" : value.toString())
			);
		}
	}
}
