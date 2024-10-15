package dev.enderman.minecraft.plugins.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import dev.enderman.minecraft.plugins.bedwars.commands.BedwarsCommand;
import dev.enderman.minecraft.plugins.bedwars.events.listeners.ConnectionListener;
import dev.enderman.minecraft.plugins.bedwars.events.listeners.GameListener;
import dev.enderman.minecraft.plugins.bedwars.managers.ArenaManager;
import dev.enderman.minecraft.plugins.bedwars.types.Arena;
import dev.enderman.minecraft.plugins.bedwars.utility.ConfigurationUtility;

public final class BedwarsPlugin extends JavaPlugin {
	private ArenaManager arenaManager;

	@Override
	public void onEnable() {
		// Setting up the configuration must happen before setting up the arena manager,
		// as the arena manager relies heavily on the config.yml file.
		ConfigurationUtility.setUpConfig(this);

		// Todo: add a scoreboard, boss bar, tab list.

		arenaManager = new ArenaManager(this);

		final PluginManager pluginManager = Bukkit.getPluginManager();

		pluginManager.registerEvents(new ConnectionListener(this), this);
		pluginManager.registerEvents(new GameListener(this), this);

		new BedwarsCommand(this);
	}

	@Override
	public void onDisable() {
		if (arenaManager != null) {
			for (final Arena arena : arenaManager.getArenas()) {
				arena.getVillager().remove();
			}
		}
	}

	public ArenaManager getArenaManager() {
		return arenaManager;
	}
}
