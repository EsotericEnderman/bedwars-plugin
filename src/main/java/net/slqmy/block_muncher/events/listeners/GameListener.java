package net.slqmy.block_muncher.events.listeners;

import net.slqmy.block_muncher.BlockMuncher;
import net.slqmy.block_muncher.enums.GameState;
import net.slqmy.block_muncher.types.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public final class GameListener implements Listener {
	private final BlockMuncher plugin;

	public GameListener(@NotNull final BlockMuncher plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(@NotNull final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		final Arena arena = plugin.getArenaManager().getArena(player.getUniqueId());

		if (arena != null && arena.getState() == GameState.PLAYING) {
			arena.getGame().addPoint(player);
		}
	}
}
