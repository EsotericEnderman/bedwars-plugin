package net.slqmy.block_muncher;

import net.slqmy.block_muncher.commands.ArenaCommand;
import net.slqmy.block_muncher.events.listeners.ConnectionListener;
import net.slqmy.block_muncher.events.listeners.GameListener;
import net.slqmy.block_muncher.managers.ArenaManager;
import net.slqmy.block_muncher.utility.ConfigurationUtility;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockMuncher extends JavaPlugin {
	private static final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();

	private ArenaManager arenaManager;

	@Override
	public void onEnable() {
		// Setting up the configuration must happen before setting up the arena manager,
		// as the arena manager relies heavily on the config.yml file.
		ConfigurationUtility.setUpConfig(this);

		arenaManager = new ArenaManager(this);

		PLUGIN_MANAGER.registerEvents(new ConnectionListener(this), this);
		PLUGIN_MANAGER.registerEvents(new GameListener(this), this);

		new ArenaCommand(this);
	}

	public ArenaManager getArenaManager() {
		return arenaManager;
	}
}
