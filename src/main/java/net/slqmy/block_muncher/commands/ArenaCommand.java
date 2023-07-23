package net.slqmy.block_muncher.commands;

import net.slqmy.block_muncher.Bedwars;
import net.slqmy.block_muncher.enums.GameState;
import net.slqmy.block_muncher.types.Arena;
import net.slqmy.block_muncher.types.AbstractCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ArenaCommand extends AbstractCommand {
	private final Bedwars plugin;

	public ArenaCommand(@NotNull final Bedwars plugin) {
		super(
				"arena",
				"Join or leave a minigame arena and see all the active arenas.",
				"/arena <join | leave | list> (arena)",
				new Integer[] { 1, 2 },
				new String[] { "game" },
				"block_muncher.arena",
				true);

		this.plugin = plugin;
	}

	@Override
	public boolean execute(@NotNull final CommandSender sender, final String @NotNull [] args) {
		final Player player = (Player) sender;

		if (args.length == 1) {
			if ("list".equalsIgnoreCase(args[0])) {
				player.sendMessage(" \n" + ChatColor.UNDERLINE + "Active Arenas:\n ");

				for (final Arena arena : plugin.getArenaManager().getArenas()) {
					player.sendMessage(
							ChatColor.DARK_GRAY + "• " + ChatColor.RESET + "Arena " + ChatColor.UNDERLINE + arena.getID() + "\n"
									+ ChatColor.GRAY + " - " + ChatColor.RESET + "State: " + arena.getState().getName() + "\n"
									+ ChatColor.GRAY + " - " + ChatColor.RESET + "Players: " + ChatColor.YELLOW + ChatColor.UNDERLINE
									+ arena.getPlayers().size() + "\n ");
				}
			} else if ("leave".equalsIgnoreCase(args[0])) {
				final Arena arena = plugin.getArenaManager().getArena(player.getUniqueId());

				if (arena == null) {
					player.sendMessage(ChatColor.RED + "You are not in an arena!");
				} else {
					player.sendMessage(ChatColor.GREEN + "You have left arena " + ChatColor.YELLOW + "#" + ChatColor.UNDERLINE
							+ arena.getID() + ChatColor.GREEN + ".");

					arena.removePlayer(player);
				}
			} else {
				return false;
			}
		} else if (args.length == 2) {
			if ("join".equalsIgnoreCase(args[0])) {
				final Arena playerArena = plugin.getArenaManager().getArena(player.getUniqueId());

				if (playerArena == null) {
					final int id;

					try {
						id = Integer.parseInt(args[1]);
					} catch (final NumberFormatException exception) {
						player.sendMessage(ChatColor.RED + "Invalid arena ID! The ID Must be " + ChatColor.UNDERLINE
								+ " an integer above 0" + ChatColor.RED + "!");
						return false;
					}

					if (id > 0 && id <= plugin.getArenaManager().getArenas().size()) {
						final Arena arena = plugin.getArenaManager().getArena(id);
						assert arena != null;

						if (arena.getState() != GameState.PLAYING) {
							arena.addPlayer(player);
							player.sendMessage(ChatColor.GREEN + "You have successfully been added to arena " + ChatColor.YELLOW
									+ ChatColor.UNDERLINE + "#" + id + ChatColor.GREEN + "!");
						} else {
							player.sendMessage(ChatColor.RED + "There is already an active game going on in that arena!");
						}
					} else {
						player.sendMessage(ChatColor.RED + "That arena does not exist!");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You are already in an arena! Use " + ChatColor.UNDERLINE + "/arena leave"
							+ ChatColor.RED + " to leave.");
				}
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
