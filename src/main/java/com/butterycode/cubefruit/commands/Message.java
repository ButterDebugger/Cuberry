package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.Caboodle;
import com.butterycode.cubefruit.utils.DogTags;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Message extends CommandWrapper {

	private static HashMap<UUID, UUID> reply = new HashMap<>();

	// TODO: make it so not just players can message you

	public Message() {
		CommandRegistry messageCmd = new CommandRegistry(this, "message");
		messageCmd.addAliases("msg");
		messageCmd.setDescription("Send a private message to another player");

		CommandRegistry replyCmd = new CommandRegistry(this, "reply");
		replyCmd.addAliases("r");
		replyCmd.setDescription("Reply to the last player who messaged you");

		addRegistries(messageCmd, replyCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = Main.plugin().config();

		if (label.equalsIgnoreCase("message") || label.equalsIgnoreCase("msg")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!Caboodle.hasPermission(sender, "message")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(awesomeText.colorize("&3Usage: &7/message <player> <message>"));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player receiver = Bukkit.getPlayer(args[0]);
				assert receiver != null;

				String str = String.join(" ", Caboodle.splice(args, 0, 1));

				if (str.length() > 0) {
					sender.sendMessage(awesomeText.colorize("&6[&eYou &6-> &b" + receiver.getName() + "&6] &7" + str));
					receiver.sendMessage(awesomeText.colorize("&6[&b" + sender.getName() + " &6-> &eYou&6] &7" + str));

					if (config.getBoolean("commands.message.sound.enabled")) {
						Sound sound = Sound.valueOf(config.getString("commands.message.sound.name"));
						float volume = (float) config.getDouble("commands.message.sound.volume");
						float pitch = (float) config.getDouble("commands.message.sound.pitch");
						receiver.playSound(receiver.getLocation(), sound, volume, pitch);
					}

					reply.put(receiver.getUniqueId(), player.getUniqueId());
					return true;
				} else {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must provide a message to send."));
					return true;
				}
			} else {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player could not be found."));
				return true;
			}
		}
		if (label.equalsIgnoreCase("reply") || label.equalsIgnoreCase("r")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!Caboodle.hasPermission(sender, "reply")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(awesomeText.colorize("&3Usage: &7/reply <message>"));
				return true;
			}

			if (!reply.containsKey(player.getUniqueId())) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7There's no one for you to reply to."));
				return true;
			}

			if (DogTags.isOnline(reply.get(player.getUniqueId()))) {
				Player receiver = Bukkit.getPlayer(reply.get(player.getUniqueId()));
				assert receiver != null;

				String str = String.join(" ", args);

				if (str.length() > 0) {
					sender.sendMessage(awesomeText.colorize("&6[&eYou &6-> &b" + receiver.getName() + "&6] &7" + str));
					receiver.sendMessage(awesomeText.colorize("&6[&b" + sender.getName() + " &6-> &eYou&6] &7" + str));

					if (config.getBoolean("commands.message.sound.enabled")) {
						Sound sound = Sound.valueOf(config.getString("commands.message.sound.name"));
						float volume = (float) config.getDouble("commands.message.sound.volume");
						float pitch = (float) config.getDouble("commands.message.sound.pitch");
						receiver.playSound(receiver.getLocation(), sound, volume, pitch);
					}

					reply.put(receiver.getUniqueId(), player.getUniqueId());
					return true;
				} else {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must provide a message to send."));
					return true;
				}
			} else {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player could not be found."));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null;
	}
}
