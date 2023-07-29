package net.slqmy.bedwars.managers;

import net.slqmy.bedwars.Bedwars;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class MessageManager {
	private static YamlConfiguration messages;

	public static void setUpLanguageFile(@NotNull final Bedwars plugin) {
		final File file = new File(plugin.getDataFolder(), "messages.yml");

		if (!file.exists()) {
			plugin.saveResource("messages.yml", false);
		}

		messages = YamlConfiguration.loadConfiguration(file);
	}

	public static @NotNull String getMessage(@NotNull final String messageKey) {
		final String message = messages.getString(messageKey);
		assert message != null;

		return ChatColor.translateAlternateColorCodes('&', message);
	}
}
