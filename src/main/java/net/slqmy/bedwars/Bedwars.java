package net.slqmy.bedwars;

import net.slqmy.bedwars.commands.BedwarsCommand;
import net.slqmy.bedwars.events.listeners.ConnectionListener;
import net.slqmy.bedwars.events.listeners.GameListener;
import net.slqmy.bedwars.managers.ArenaManager;
import net.slqmy.bedwars.managers.MessageManager;
import net.slqmy.bedwars.types.Arena;
import net.slqmy.bedwars.utility.ConfigurationUtility;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Bedwars extends JavaPlugin {
	private static final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();

	private ArenaManager arenaManager;

	@Override
	public void onEnable() {
		// Setting up the configuration must happen before setting up the arena manager,
		// as the arena manager relies heavily on the config.yml file.
		ConfigurationUtility.setUpConfig(this);
		MessageManager.setUpLanguageFile(this);

		// Todo: add a scoreboard, boss bar, tab list, sidebar, and coloured messages.

		arenaManager = new ArenaManager(this);

		PLUGIN_MANAGER.registerEvents(new ConnectionListener(this), this);
		PLUGIN_MANAGER.registerEvents(new GameListener(this), this);

		new BedwarsCommand(this);
	}

	@Override
	public void onDisable() {
		for (final Arena arena : arenaManager.getArenas()) {
			arena.getVillager().remove();
		}
	}

	public ArenaManager getArenaManager() {
		return arenaManager;
	}
}
