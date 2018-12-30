package me.daddychurchill.MaxiWorld;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MaxiWorld extends JavaPlugin {

	public static final Logger log = Logger.getLogger("Minecraft.MaxiWorld");

	public MaxiWorld() {
		super();
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String name, String style) {
		return getChunkGenerator(name, style);
	}

	@Override
	public void onDisable() {

		// remember for the next time
		saveConfig();

		// tell the world we are out of here
		log.info(getDescription().getFullName() + " has been disabled");
	}

	@Override
	public void onEnable() {

		// figure out permissions and associated commands
		PluginManager pm = getServer().getPluginManager();
		pm.addPermission(new Permission("maxiworld.command", PermissionDefault.OP));
		addCommand("maxiworld", new CreateCMD(this));

		// configFile can be retrieved via getConfig()
		log.info(getDescription().getFullName() + " is enabled");
	}

	private void addCommand(String keyword, CommandExecutor exec) {
		PluginCommand cmd = getCommand(keyword);
		if (cmd == null || exec == null) {
			log.info("Cannot create command for " + keyword);
		} else {
			cmd.setExecutor(exec);
		}
	}

	// prime world support (loosely based on ExpansiveTerrain)
	public final static String WORLD_NAME = "MaxiWorld";
	private World maxiworldPrime = null;

	public World getMaxiWorld() {

		// created yet?
		if (maxiworldPrime == null) {

			// built yet?
			maxiworldPrime = Bukkit.getServer().getWorld(WORLD_NAME);
			if (maxiworldPrime == null) {

				// if neither then create/build it!
				WorldCreator creator = new WorldCreator(WORLD_NAME);
				creator.environment(World.Environment.NORMAL);
				creator.generator(getChunkGenerator(WORLD_NAME, ""));
				maxiworldPrime = Bukkit.getServer().createWorld(creator);
			}
		}
		return maxiworldPrime;
	}

	private ChunkGenerator getChunkGenerator(String name, String style) {
		return new ChunkCallback(new WorldConfig(this, name, style));
	}
}
