package dev.debutter.cubefruit.paper.commands;

import dev.debutter.cubefruit.paper.Paper;
import dev.debutter.cubefruit.paper.commands.builder.CommandRegistry;
import dev.debutter.cubefruit.paper.commands.builder.CommandWrapper;
import dev.debutter.cubefruit.paper.utils.AwesomeText;
import dev.debutter.cubefruit.paper.utils.Caboodle;
import dev.debutter.cubefruit.paper.utils.DogTags;
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
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

			Player player = (Player) sender;

			if (!Caboodle.hasPermission(sender, "speed")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/speed <number>"));
				return true;
			}

			if (player.isFlying()) {
				if (!DogTags.isNumeric(args[0])) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
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
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
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
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

			Player player = (Player) sender;

			if (!(Caboodle.hasPermission(sender, "speed") || Caboodle.hasPermission(sender, "speed.walk"))) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/walkspeed <number>"));
				return true;
			}

            if (!DogTags.isNumeric(args[0])) {
                sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
                return true;
            }

            float number = Float.parseFloat(args[0]);
            number = Math.max(0, number);
            number = Math.min(10, number);

            player.setWalkSpeed(number / 10);

            sender.sendMessage(AwesomeText.colorize("&a&l» &7Set walking speed to &f" + number + "&7."));
            return true;
        }

		if (label.equalsIgnoreCase("flyspeed")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

			Player player = (Player) sender;

			if (!(Caboodle.hasPermission(sender, "speed") || Caboodle.hasPermission(sender, "speed.fly"))) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/flyspeed <number>"));
				return true;
			}

            if (!DogTags.isNumeric(args[0])) {
                sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
                return true;
            }

            float number = Float.parseFloat(args[0]);
            number = Math.max(0, number);
            number = Math.min(10, number);

            player.setFlySpeed(number / 10);

            sender.sendMessage(AwesomeText.colorize("&a&l» &7Set flying speed to &f" + number + "&7."));
            return true;
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
