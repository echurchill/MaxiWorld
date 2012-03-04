package me.daddychurchill.MaxiWorld;

import org.bukkit.configuration.file.FileConfiguration;

public class WorldConfig {
	private MaxiWorld plugin;
	private String worldname;
	private String worldstyle;
	private boolean biggerBlocks;
	
	public final static boolean defaultBiggerBlocks = false;
	
	public WorldConfig(MaxiWorld plugin, String name, String style) {
		super();
		
		this.plugin = plugin;
		this.worldname = name;
		this.worldstyle = style;
		
		// remember the globals
		boolean globalBiggerBlocks = defaultBiggerBlocks;
		
		// global read yet?
		FileConfiguration config = plugin.getConfig();
		config.options().header("MaxiWorld Global Options");
		config.addDefault("Global.BiggerBlocks", defaultBiggerBlocks);
		config.options().copyDefaults(true);
		plugin.saveConfig();
		
		// now read out the bits for real
		globalBiggerBlocks = config.getBoolean("Global.BiggerBlocks");
		
		// grab the world specific values else use the globals
		biggerBlocks = getWorldBoolean(config, "IncludeBiggerBlocks", globalBiggerBlocks);
	}

//	private int getWorldInt(FileConfiguration config, String option, int global) {
//		int result = global;
//		String path = worldname + "." + option;
//		if (config.isSet(path))
//			result = config.getInt(path);
//		return result;
//	}
	
//	private double getWorldDouble(FileConfiguration config, String option, double global) {
//		double result = global;
//		String path = worldname + "." + option;
//		if (config.isSet(path))
//			result = config.getDouble(path);
//		return result;
//	}
	
	private boolean getWorldBoolean(FileConfiguration config, String option, boolean global) {
		boolean result = global;
		String path = worldname + "." + option;
		if (config.isSet(path))
			result = config.getBoolean(path);
		return result;
	}
	
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
		return 3 * (biggerBlocks ? 8 : 4);
	}
	
	public boolean getBiggerBlocks() {
		return biggerBlocks;
	}
}
