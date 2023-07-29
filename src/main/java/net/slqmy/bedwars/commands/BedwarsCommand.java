package net.slqmy.bedwars.commands;

import net.slqmy.bedwars.Bedwars;
import net.slqmy.bedwars.enums.GameState;
import net.slqmy.bedwars.managers.MessageManager;
import net.slqmy.bedwars.types.AbstractCommand;
import net.slqmy.bedwars.types.Arena;
import net.slqmy.bedwars.utility.ConfigurationUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class BedwarsCommand extends AbstractCommand {
	private final Bedwars plugin;

	public BedwarsCommand(@NotNull final Bedwars plugin) {
		super(
						"bedwars",
						"Join or leave a bedwars arena and see all the active arenas.",
						"/bedwars <join | leave | list> (arena)",
						new Integer[]{1, 2},
						new String[]{"bw"},
						"bedwars.bedwars",
						true
		);

		this.plugin = plugin;
	}

	@Override
	public boolean execute(@NotNull final CommandSender sender, final String @NotNull [] args) {
		final Player player = (Player) sender;

		if (args.length == 1) {
			if ("list".equalsIgnoreCase(args[0])) {
				player.sendMessage(" \n" + ChatColor.UNDERLINE + "Active Arenas:\n" + ChatColor.RESET + " ");

				for (final Arena arena : plugin.getArenaManager().getArenas()) {
					player.sendMessage(
									ChatColor.DARK_GRAY + "• " + ChatColor.RESET + "Arena " + ChatColor.UNDERLINE + arena.getID() + "\n"
													+ ChatColor.GRAY + " - " + ChatColor.RESET + "State: " + arena.getState().getName() + "\n"
													+ ChatColor.GRAY + " - " + ChatColor.RESET + "Players: " + ChatColor.YELLOW + ChatColor.UNDERLINE
													+ arena.getPlayers().size() + "\n" + ChatColor.RESET + " "
					);
				}
			} else if ("leave".equalsIgnoreCase(args[0])) {
				final Arena arena = plugin.getArenaManager().getArena(player);

				if (arena == null) {
					player.sendMessage(ChatColor.RED + "You are not in an arena!");
					return true;
				}

				player.sendMessage(MessageManager.getMessage("leave-arena", arena.getID()));

				arena.removePlayer(player);
			} else {
				return false;
			}
		} else if (args.length == 2) {
			if ("join".equalsIgnoreCase(args[0])) {
				final Arena playerArena = plugin.getArenaManager().getArena(player);

				if (playerArena != null) {
					player.sendMessage(ChatColor.RED + "You are already in an arena! Use " + ChatColor.UNDERLINE + "/bedwars leave" + ChatColor.RED + " to leave.");
					return true;
				}

				final int id;

				try {
					id = Integer.parseInt(args[1]);
				} catch (final NumberFormatException exception) {
					player.sendMessage(ChatColor.RED + "Invalid arena ID! The ID Must be " + ChatColor.UNDERLINE + " an integer above 0" + ChatColor.RED + "!");
					return false;
				}

				if (id <= 0 || id > plugin.getArenaManager().getArenas().size()) {
					player.sendMessage(ChatColor.RED + "That arena does not exist!");
					return false;
				}

				final Arena arena = plugin.getArenaManager().getArena(id);
				assert arena != null;

				if (arena.getState() == GameState.PLAYING) {
					player.sendMessage(ChatColor.RED + "There is already an active game going on in that arena!");
					return true;
				}

				if (arena.getPlayers().size() == ConfigurationUtility.getRequiredPlayers()) {
					player.sendMessage(ChatColor.RED + "That arena is full, sorry!");
					return true;
				}

				if (!arena.isWorldLoaded()) {
					player.sendMessage(ChatColor.RED + "That arena is being loaded right now, please wait!");
					return true;
				}

				arena.addPlayer(player);
				player.sendMessage(ChatColor.GREEN + "You have successfully been added to arena " + ChatColor.YELLOW
								+ ChatColor.UNDERLINE + "#" + id + ChatColor.GREEN + "!");
			} else {
				return false;
			}
		}

		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, final String @NotNull [] args) {
		if (args.length == 1 && "join".equalsIgnoreCase(args[0])) {
			final List<String> results = new ArrayList<>();

			for (final Arena arena : plugin.getArenaManager().getArenas()) {
				results.add(String.valueOf(arena.getID()));
			}

			return results;
		}

		return null;
	}
}
