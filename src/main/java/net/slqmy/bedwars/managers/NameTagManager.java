package net.slqmy.bedwars.managers;

import net.slqmy.bedwars.enums.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class NameTagManager {
	public static void setNameTags(@NotNull final Player player) {
		for (final Team team : Team.values()) {
			if (player.getScoreboard().getTeam(team.name() + "_team") == null) {
				final org.bukkit.scoreboard.Team boardTeam = player.getScoreboard().registerNewTeam(team.name() + "_team");
				boardTeam.setPrefix(team.getColour() + team.getName() + " ");
			}
		}
	}

	public static void newTag(@NotNull final Player player, @NotNull final Team team) {
		for (final Player target : Bukkit.getOnlinePlayers()) {
			org.bukkit.scoreboard.Team teamTeam = target.getScoreboard().getTeam(team.name() + "_team");

			if (teamTeam == null) {
				teamTeam = target.getScoreboard().registerNewTeam(team.name() + "_team");
				teamTeam.setPrefix(team.getColour() + team.getName() + " ");
			}

			teamTeam.addEntry(player.getName());
		}
	}

	public static void removeTag(@NotNull final Player player) {
		for (final Player target : Bukkit.getOnlinePlayers()) {
			final org.bukkit.scoreboard.Team playerTeam = target.getScoreboard().getEntryTeam(player.getName());

			if (playerTeam != null) {
				playerTeam.removeEntry(player.getName());
			}
		}
	}
}
