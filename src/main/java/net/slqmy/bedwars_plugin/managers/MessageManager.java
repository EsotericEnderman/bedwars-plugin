package net.slqmy.bedwars_plugin.managers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import net.slqmy.bedwars_plugin.BedwarsPlugin;

import java.io.File;

public final class MessageManager {
	private static YamlConfiguration messages;

	public static void setUpLanguageFile(@NotNull final BedwarsPlugin plugin) {
		final File file = new File(plugin.getDataFolder(), "messages.yml");

		if (!file.exists()) {
			plugin.saveResource("messages.yml", false);
		}

		messages = YamlConfiguration.loadConfiguration(file);
	}

	public static @NotNull String getMessage(@NotNull final String messageKey, @NotNull final Object @NotNull ... placeholderValues) {
		final String message = messages.getString(messageKey);
		assert message != null;

		String[] parts = message.split("\\{\\w+}");
		StringBuilder stringBuilder = new StringBuilder(parts[0]);

		for (int i = 1; i < parts.length; i++) {
			stringBuilder.append(placeholderValues[i - 1]);
		}

		return ChatColor.translateAlternateColorCodes('&', stringBuilder.toString());
	}
}
