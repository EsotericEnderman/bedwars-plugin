package net.slqmy.bedwars_plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.slqmy.bedwars_plugin.commands.BedwarsCommand;
import net.slqmy.bedwars_plugin.events.listeners.ConnectionListener;
import net.slqmy.bedwars_plugin.events.listeners.GameListener;
import net.slqmy.bedwars_plugin.managers.ArenaManager;
import net.slqmy.bedwars_plugin.managers.MessageManager;
import net.slqmy.bedwars_plugin.types.Arena;
import net.slqmy.bedwars_plugin.utility.ConfigurationUtility;

public final class BedwarsPlugin extends JavaPlugin {
	private ArenaManager arenaManager;

	@Override
	public void onEnable() {
		// Setting up the configuration must happen before setting up the arena manager,
		// as the arena manager relies heavily on the config.yml file.
		ConfigurationUtility.setUpConfig(this);
		MessageManager.setUpLanguageFile(this);

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
