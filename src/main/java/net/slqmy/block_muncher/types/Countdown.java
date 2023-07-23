package net.slqmy.block_muncher.types;

import net.slqmy.block_muncher.BlockMuncher;
import net.slqmy.block_muncher.enums.GameState;
import net.slqmy.block_muncher.utility.ConfigurationUtility;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public final class Countdown extends BukkitRunnable {
	private final BlockMuncher plugin;
	private final Arena arena;

	private int countdownSeconds;

	public Countdown(@NotNull final BlockMuncher plugin, @NotNull final Arena arena) {
		this.plugin = plugin;
		this.arena = arena;

		this.countdownSeconds = ConfigurationUtility.getCountdownSeconds();
	}

	public void start() {
		arena.setState(GameState.COUNTDOWN);

		runTaskTimer(plugin, 0, 20);
	}

	@Override
	public void run() {
		if (countdownSeconds == 0) {
			arena.start();

			cancel();
			return;
		}

		if (countdownSeconds <= 10 || countdownSeconds % 15 == 0) {
			arena.sendTitle(ChatColor.YELLOW.toString() + countdownSeconds);
			arena.sendMessage(
					ChatColor.YELLOW + "The game will start in " + ChatColor.RED + countdownSeconds
							+ ChatColor.YELLOW + " second" + (countdownSeconds == 1 ? "" : "s") + ".");
		}

		countdownSeconds--;
	}
}
