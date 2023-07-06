package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.utils.AwesomeText;
import com.butterycode.cubefruit.utils.Caboodle;
import com.butterycode.cubefruit.utils.DogTags;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Speed extends CommandWrapper {

	public Speed() {
		CommandRegistry speedCmd = new CommandRegistry(this, "speed");
		speedCmd.setDescription("Change your walk or fly speed");

		CommandRegistry walkspeedCmd = new CommandRegistry(this, "walkspeed");
		walkspeedCmd.setDescription("Change your walk speed");

		CommandRegistry flyspeedCmd = new CommandRegistry(this, "flyspeed");
		flyspeedCmd.setDescription("Change your fly speed");

		addRegistries(speedCmd, walkspeedCmd, flyspeedCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("speed")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!Caboodle.hasPermission(sender, "speed")) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.prettifyMessage("&3Usage: &7/speed <number>"));
				return true;
			}

			if (player.isFlying()) {
				if (!DogTags.isNumeric(args[0])) {
					sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must enter a number."));
					return true;
				}

				float number = Float.parseFloat(args[0]);
				number = Math.max(0, number);
				number = Math.min(10, number);

				player.setFlySpeed(number / 10);

				sender.sendMessage(AwesomeText.colorize("&a&l» &7Set flying speed to &f" + number + "&7."));
				return true;
			} else {
				if (!DogTags.isNumeric(args[0])) {
					sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must enter a number."));
					return true;
				}

				float number = Float.parseFloat(args[0]);
				number = Math.max(0, number);
				number = Math.min(10, number);

				player.setWalkSpeed(number / 10);

				sender.sendMessage(AwesomeText.colorize("&a&l» &7Set walking speed to &f" + number + "&7."));
				return true;
			}
		}

		if (label.equalsIgnoreCase("walkspeed")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!(Caboodle.hasPermission(sender, "speed") || Caboodle.hasPermission(sender, "speed.walk"))) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.prettifyMessage("&3Usage: &7/walkspeed <number>"));
				return true;
			}
			if (args.length > 0) {
				if (!DogTags.isNumeric(args[0])) {
					sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must enter a number."));
					return true;
				}

				float number = Float.parseFloat(args[0]);
				number = Math.max(0, number);
				number = Math.min(10, number);

				player.setWalkSpeed(number / 10);

				sender.sendMessage(AwesomeText.colorize("&a&l» &7Set walking speed to &f" + number + "&7."));
				return true;
			}
		}

		if (label.equalsIgnoreCase("flyspeed")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!(Caboodle.hasPermission(sender, "speed") || Caboodle.hasPermission(sender, "speed.fly"))) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.prettifyMessage("&3Usage: &7/flyspeed <number>"));
				return true;
			}
			if (args.length > 0) {
				if (!DogTags.isNumeric(args[0])) {
					sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must enter a number."));
					return true;
				}

				float number = Float.parseFloat(args[0]);
				number = Math.max(0, number);
				number = Math.min(10, number);

				player.setFlySpeed(number / 10);

				sender.sendMessage(AwesomeText.colorize("&a&l» &7Set flying speed to &f" + number + "&7."));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("speed") && Caboodle.hasPermission(sender, "speed")) {
			if (args.length == 1) {
				Player player = (Player) sender;

				if (player.isFlying()) {
					return Arrays.asList("0", "1", "10");
				} else {
					return Arrays.asList("0", "2", "10");
				}
			}

			return Collections.emptyList();
		}

		if (label.equalsIgnoreCase("walkspeed") && (Caboodle.hasPermission(sender, "speed") || Caboodle.hasPermission(sender, "speed.walk"))) {
			if (args.length == 1) {
				return Arrays.asList("0", "2", "10");
			}

			return Collections.emptyList();
		}

		if (label.equalsIgnoreCase("flyspeed") && (Caboodle.hasPermission(sender, "speed") || Caboodle.hasPermission(sender, "speed.fly"))) {
			if (args.length == 1) {
				return Arrays.asList("0", "1", "10");
			}

			return Collections.emptyList();
		}

		return null;
	}
}
