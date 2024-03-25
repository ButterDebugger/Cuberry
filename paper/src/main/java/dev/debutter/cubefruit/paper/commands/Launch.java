package dev.debutter.cubefruit.paper.commands;

import dev.debutter.cubefruit.paper.Paper;
import dev.debutter.cubefruit.paper.commands.builder.CommandRegistry;
import dev.debutter.cubefruit.paper.commands.builder.CommandWrapper;
import dev.debutter.cubefruit.paper.utils.AwesomeText;
import dev.debutter.cubefruit.paper.utils.Caboodle;
import dev.debutter.cubefruit.paper.utils.DogTags;
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
		CommandRegistry launchCmd = new CommandRegistry(this, "launch");
		launchCmd.setDescription("Launch a player into the air");

		addRegistries(launchCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("launch")) {
			if (!Caboodle.hasPermission(sender, "launch")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			
			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

			Player player = (Player) sender;

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/launch <forward|backward|up|down> <number>"));
				return true;
			}

			if (args[0].equalsIgnoreCase("forward")) {
				if (args.length <= 1) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
					return true;
				}
				if (!DogTags.isNumeric(args[1])) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
					return true;
				}

				Entity vehicle = player.getVehicle();
				float speed = Float.parseFloat(args[1]);

				if (speed < 0) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.number_must_be_positive", sender)));
					return true;
				}

				sender.sendMessage(AwesomeText.beautifyMessage("<green><bold>»</bold> <gray>You have been launched forward."));

				if (vehicle == null) {
					player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().multiply(speed)));
				} else {
					vehicle.setVelocity(vehicle.getVelocity().add(player.getEyeLocation().getDirection().multiply(speed)));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("backward")) {
				if (args.length <= 1) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
					return true;
				}
				if (!DogTags.isNumeric(args[1])) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
					return true;
				}

				Entity vehicle = player.getVehicle();
				float speed = Float.parseFloat(args[1]);

				if (speed < 0) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.number_must_be_positive", sender)));
					return true;
				}

				sender.sendMessage(AwesomeText.beautifyMessage("<green><bold>»</bold> <gray>You have been launched backward."));

				if (vehicle == null) {
					player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().multiply(speed * -1)));
				} else {
					vehicle.setVelocity(vehicle.getVelocity().add(player.getEyeLocation().getDirection().multiply(speed * -1)));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("up")) {
				if (args.length <= 1) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
					return true;
				}
				if (!DogTags.isNumeric(args[1])) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
					return true;
				}

				Entity vehicle = player.getVehicle();
				float speed = Float.parseFloat(args[1]);

				if (speed < 0) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.number_must_be_positive", sender)));
					return true;
				}

				sender.sendMessage(AwesomeText.beautifyMessage("<green><bold>»</bold> <gray>You have been launched up."));

				if (vehicle == null) {
					player.setVelocity(player.getVelocity().add(new Vector(0, speed, 0)));
				} else {
					vehicle.setVelocity(vehicle.getVelocity().add(new Vector(0, speed, 0)));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("down")) {
				if (args.length <= 1) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
					return true;
				}
				if (!DogTags.isNumeric(args[1])) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
					return true;
				}

				Entity vehicle = player.getVehicle();
				float speed = Float.parseFloat(args[1]);

				if (speed < 0) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.number_must_be_positive", sender)));
					return true;
				}

				sender.sendMessage(AwesomeText.beautifyMessage("<green><bold>»</bold> <gray>You have been launched down."));

				if (vehicle == null) {
					player.setVelocity(player.getVelocity().add(new Vector(0, speed * -1, 0)));
				} else {
					vehicle.setVelocity(vehicle.getVelocity().add(new Vector(0, speed * -1, 0)));
				}
				return true;
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
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
