package me.daddychurchill.MaxiWorld;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CreateCMD implements CommandExecutor {
    private final MaxiWorld plugin;

    public CreateCMD(MaxiWorld plugin)
    {
        this.plugin = plugin;
    }

	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) 
    {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("maxiworld.command")) {
				player.sendMessage("Loading/creating MaxiWorld... This might take a moment...");
				player.teleport(plugin.getMaxiWorld().getSpawnLocation());
				return true;
			} else {
				sender.sendMessage("You do not have permission to use this command");
				return false;
			}
		} else {
			sender.sendMessage("This command is only usable by a player");
			return false;
		}
    }
}
