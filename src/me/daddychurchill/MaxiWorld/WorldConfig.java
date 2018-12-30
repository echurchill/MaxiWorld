package me.daddychurchill.MaxiWorld;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class WorldConfig {
	private MaxiWorld plugin;
	private String worldname;
	private String worldstyle;
	private int blockSize;

	public final static int defaultBlockSize = 8;

	public WorldConfig(MaxiWorld plugin, String name, String style) {
		super();

		this.plugin = plugin;
		this.worldname = name;
		this.worldstyle = style;

		// remember the globals
		int globalBlockSize = defaultBlockSize;

		// global read yet?
		FileConfiguration config = plugin.getConfig();
		config.options().header("MaxiWorld Global Options");
		config.addDefault("Global.BlockSize", defaultBlockSize);
		config.options().copyDefaults(true);
		plugin.saveConfig();

		// now read out the bits for real
		globalBlockSize = config.getInt("Global.BlockSize");

		// grab the world specific values else use the globals
		blockSize = getWorldInt(config, "BlockSize", globalBlockSize);
	}

	private int getWorldInt(FileConfiguration config, String option, int global) {
		int result = global;
		String path = worldname + "." + option;
		if (config.isSet(path))
			result = config.getInt(path);
		return result;
	}

//	private double getWorldDouble(FileConfiguration config, String option, double global) {
//		double result = global;
//		String path = worldname + "." + option;
//		if (config.isSet(path))
//			result = config.getDouble(path);
//		return result;
//	}

//	private boolean getWorldBoolean(FileConfiguration config, String option, boolean global) {
//		boolean result = global;
//		String path = worldname + "." + option;
//		if (config.isSet(path))
//			result = config.getBoolean(path);
//		return result;
//	}

	public MaxiWorld getPlugin() {
		return plugin;
	}

	public String getWorldname() {
		return worldname;
	}

	public String getWorldstyle() {
		return worldstyle;
	}

	public int getStreetLevel() {
		return 4;
	}

	public int getBlockSize(World world) {

		// validate blockSize
		if (blockSize <= 4)
			blockSize = 4;
		else if (blockSize <= 8)
			blockSize = 8;
//		else if (world.getMaxHeight() >= 256)
//			blockSize = 16;
		else
			blockSize = 8;

		return blockSize;
	}
}
