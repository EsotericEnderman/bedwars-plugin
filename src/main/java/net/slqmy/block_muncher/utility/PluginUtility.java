package net.slqmy.block_muncher.utility;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PluginUtility {
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private static final String LOG_PREFIX = "[Block-Muncher]";
	private static final Pattern FORMAT_PATTERN = Pattern.compile(ChatColor.COLOR_CHAR + "[0-9a-fk-or]");

	public static void log(@Nullable final Object message) {
		final String messageString = clearFormatting(LOG_PREFIX + " " + (message == null ? "NULL" : message.toString()));

		LOGGER.info(messageString);
	}

	@NotNull
	public static String replaceAll(@NotNull final String input, @NotNull final Pattern pattern,
			@NotNull final String replaceString) {
		final Matcher matcher = pattern.matcher(input);
		return matcher.replaceAll(replaceString);
	}

	@NotNull
	public static String clearFormatting(@NotNull final String input) {
		return replaceAll(input, FORMAT_PATTERN, "");
	}
}
