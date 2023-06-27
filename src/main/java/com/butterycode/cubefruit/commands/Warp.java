package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.dataStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Warp extends CommandWrapper {

	dataStorage warps = Main.plugin.getData("warps.yml");

	public Warp() {
		CommandRegistry warpCmd = new CommandRegistry(this, "warp");
		warpCmd.setDescription("Warp to the specified location");

		CommandRegistry delwarpCmd = new CommandRegistry(this, "delwarp");
		delwarpCmd.setDescription("Delete the specified warp");

		CommandRegistry setwarpCmd = new CommandRegistry(this, "setwarp");
		setwarpCmd.setDescription("Create a new warp");

		CommandRegistry listwarpsCmd = new CommandRegistry(this, "listwarps");
		listwarpsCmd.setDescription("List all warps");

		addRegistries(warpCmd, delwarpCmd, setwarpCmd, listwarpsCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("warp")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player)sender;

			if (!caboodle.hasPermission(sender, "warp")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(awesomeText.colorize("&3Usage: &7/warp <name>"));
				return true;
			}

			String name = args[0];

			if (warps.existsNot(name)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Warp does not exist."));
				return true;
			}

			String worldName = warps.getString(name + ".world");
			double x = warps.getDouble(name + ".x");
			double y = warps.getDouble(name + ".y");
			double z = warps.getDouble(name + ".z");
			float yaw = (float) warps.getDouble(name + ".yaw");
			float pitch = (float) warps.getDouble(name + ".pitch");

			World world = Bukkit.getWorld(worldName);

			if (world == null) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7An error has occurred with this warps world."));
				return true;
			}

			Location loc = new Location(world, x, y, z, yaw, pitch);

			player.teleport(loc);
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1f);

			sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7You have been warped to " + name + "."));
			return true;
		} else if (label.equalsIgnoreCase("delwarp")) {
			if (!caboodle.hasPermission(sender, "delwarp")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(awesomeText.colorize("&3Usage: &7/delwarp <name>"));
				return true;
			}

			String name = args[0];

			if (warps.existsNot(name)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Warp does not exist."));
				return true;
			}

			warps.remove(name);

			sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Warp has been deleted."));
			return true;
		} else if (label.equalsIgnoreCase("setwarp")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!caboodle.hasPermission(sender, "setwarp")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(awesomeText.colorize("&3Usage: &7/setwarp <name>"));
				return true;
			}

			String name = args[0];
			Location loc = player.getLocation();

			warps.set(name + ".world", loc.getWorld().getName());
			warps.set(name + ".x", loc.getX());
			warps.set(name + ".y", loc.getY());
			warps.set(name + ".z", loc.getZ());
			warps.set(name + ".yaw", loc.getYaw());
			warps.set(name + ".pitch", loc.getPitch());

			sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Warp has been set."));
			return true;
		} else if (label.equalsIgnoreCase("listwarps")) {
			if (!caboodle.hasPermission(sender, "listwarps")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			List<String> warpsList = warps.getKeys();

			if (warpsList.size() == 0) {
				sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7There are no warps."));
			} else {
				String warpsMsg = "&a&l» &7Warps:";

				for (String warpKey : warpsList) {
					warpsMsg += "\n&7- &f" + warpKey;
				}

				sender.sendMessage(awesomeText.prettifyMessage(warpsMsg));
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("warp") && caboodle.hasPermission(sender, "warp")) {
			if (args.length == 1) {
				return warps.getKeys();
			}

			return Collections.emptyList();
		}
		if (command.getName().equalsIgnoreCase("setwarp") && caboodle.hasPermission(sender, "setwarp")) {
			return Collections.emptyList();
		}
		if (command.getName().equalsIgnoreCase("delwarp") && caboodle.hasPermission(sender, "delwarp")) {
			if (args.length == 1) {
				return warps.getKeys();
			}

			return Collections.emptyList();
		}

		return null;
	}
}
