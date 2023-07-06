package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.Caboodle;
import com.butterycode.cubefruit.utils.DogTags;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Launch extends CommandWrapper {

	public Launch() {
		CommandWrapper.CommandRegistry launchCmd = new CommandRegistry(this, "launch");
		launchCmd.setDescription("Launch a player into the air");

		addRegistries(launchCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("launch")) {
			if (!Caboodle.hasPermission(sender, "launch")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (args.length == 0) {
				sender.sendMessage(awesomeText.prettifyMessage("&3Usage: &7/launch <forward|backward|up|down> <number>"));
				return true;
			}

			if (args[0].equalsIgnoreCase("forward")) {
				if (args.length <= 1) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
					return true;
				}
				if (!DogTags.isNumeric(args[1])) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid number."));
					return true;
				}

				Entity vehicle = player.getVehicle();
				float speed = Float.parseFloat(args[1]);

				if (speed < 0) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Number cannot be negative."));
					return true;
				}

				sender.sendMessage(awesomeText.colorize("&a&l» &7You have been launched forward."));

				if (vehicle == null) {
					player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().multiply(speed)));
				} else {
					vehicle.setVelocity(vehicle.getVelocity().add(player.getEyeLocation().getDirection().multiply(speed)));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("backward")) {
				if (args.length <= 1) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
					return true;
				}
				if (!DogTags.isNumeric(args[1])) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid number."));
					return true;
				}

				Entity vehicle = player.getVehicle();
				float speed = Float.parseFloat(args[1]);

				if (speed < 0) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Number cannot be negative."));
					return true;
				}

				sender.sendMessage(awesomeText.colorize("&a&l» &7You have been launched backward."));

				if (vehicle == null) {
					player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().multiply(speed * -1)));
				} else {
					vehicle.setVelocity(vehicle.getVelocity().add(player.getEyeLocation().getDirection().multiply(speed * -1)));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("up")) {
				if (args.length <= 1) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
					return true;
				}
				if (!DogTags.isNumeric(args[1])) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid number."));
					return true;
				}

				Entity vehicle = player.getVehicle();
				float speed = Float.parseFloat(args[1]);

				if (speed < 0) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Number cannot be negative."));
					return true;
				}

				sender.sendMessage(awesomeText.colorize("&a&l» &7You have been launched up."));

				if (vehicle == null) {
					player.setVelocity(player.getVelocity().add(new Vector(0, speed, 0)));
				} else {
					vehicle.setVelocity(vehicle.getVelocity().add(new Vector(0, speed, 0)));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("down")) {
				if (args.length <= 1) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
					return true;
				}
				if (!DogTags.isNumeric(args[1])) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid number."));
					return true;
				}

				Entity vehicle = player.getVehicle();
				float speed = Float.parseFloat(args[1]);

				if (speed < 0) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Number cannot be negative."));
					return true;
				}

				sender.sendMessage(awesomeText.colorize("&a&l» &7You have been launched down."));

				if (vehicle == null) {
					player.setVelocity(player.getVelocity().add(new Vector(0, speed * -1, 0)));
				} else {
					vehicle.setVelocity(vehicle.getVelocity().add(new Vector(0, speed * -1, 0)));
				}
				return true;
			} else {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("launch") && Caboodle.hasPermission(sender, "launch")) {
			if (args.length == 1) {
				return Arrays.asList("forward", "backward", "up", "down");
			}

			return Collections.emptyList();
		}

		return null;
	}
}
