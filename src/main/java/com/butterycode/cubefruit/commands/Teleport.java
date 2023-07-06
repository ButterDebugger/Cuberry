package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.dataStorage;
import com.butterycode.cubefruit.utils.DogTags;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Teleport extends CommandWrapper {

	public Teleport() {
		CommandRegistry tphereCmd = new CommandRegistry(this, "tphere");
		tphereCmd.setDescription("Teleport a player to you");

		CommandRegistry tp2pCmd = new CommandRegistry(this, "tp2p");
		tp2pCmd.setDescription("Teleport to a player");

		CommandRegistry tpallCmd = new CommandRegistry(this, "tpall");
		tpallCmd.setDescription("Teleport all online players to you");

		CommandRegistry tpofflineCmd = new CommandRegistry(this, "tpoffline");
		tpofflineCmd.setDescription("Teleport to a player's last known location");

		addRegistries(tphereCmd, tp2pCmd, tpallCmd, tpofflineCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		dataStorage playerData = Main.plugin().getData("players.yml");

		if (label.equalsIgnoreCase("tphere")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!caboodle.hasPermission(sender, "tphere")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(awesomeText.colorize("&3Usage: &7/tphere <username>"));
				return true;
			}

			String username = args[0];

			if (!DogTags.isOnline(username)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player is not online."));
				return true;
			}

			Player otherPlayer = Bukkit.getPlayer(username);

			otherPlayer.teleport(player.getLocation());

			sender.sendMessage(awesomeText.prettifyMessage("&a&l» &f" + otherPlayer.getName() + "&7 has been teleported to you."));
			return true;
		} else if (label.equalsIgnoreCase("tp2p")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!caboodle.hasPermission(sender, "tp2p")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(awesomeText.colorize("&3Usage: &7/tp2p <username> [<username>]"));
				return true;
			}

			String username = args[0];

			if (!DogTags.isOnline(username)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player is not online."));
				return true;
			}

			Player otherPlayer = Bukkit.getPlayer(username);

			player.teleport(otherPlayer.getLocation());

			sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7You have been teleported to &f" + otherPlayer.getName() + "&7."));
			return true;
		} else if (label.equalsIgnoreCase("tpall")) {
			if (!caboodle.hasPermission(sender, "tpall")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
					return true;
				}

				Player player = (Player) sender;
				Location location = player.getLocation();

				for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
					otherPlayer.teleport(location);
				}

				sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7All players have been teleported to you."));
				return true;
			} else {
				String username = args[0];

				if (!DogTags.isOnline(username)) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player is not online."));
					return true;
				}

				Player player = Bukkit.getPlayer(username);
				Location location = player.getLocation();

				for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
					otherPlayer.teleport(location);
				}

				sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7All players have been teleported to &f" + player.getName() + "&7."));
				return true;
			}
		} else if (label.equalsIgnoreCase("tpoffline")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!caboodle.hasPermission(sender, "tpoffline")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(awesomeText.colorize("&3Usage: &7/tpoffline <username>"));
				return true;
			}

			String username = args[0];
			OfflinePlayer otherPlayer = caboodle.getOfflinePlayer(username);
			UUID otherUUID = otherPlayer.getUniqueId();

			if (!otherPlayer.hasPlayedBefore()) {
				sender.sendMessage(awesomeText.colorize("&cError: &f" + otherPlayer.getName() + "&7 has never played before."));
				return true;
			}

			if (otherPlayer.isOnline()) {
				String logoutLoc = playerData.getString(otherUUID + ".logoutlocation");

				if (logoutLoc == null) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &f" + otherPlayer.getName() + "&7's logout location is not known."));
					return true;
				}

				Location loc = caboodle.parseLocation(logoutLoc);

				player.teleport(loc);
				sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7You have been teleported to &f" + otherPlayer.getName() + "&7's last logout location."));
				return true;
			} else {
				String logoutLoc = playerData.getString(otherUUID + ".logoutlocation");

				if (logoutLoc == null) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &f" + otherPlayer.getName() + "&7's logout location is not known."));
					return true;
				}

				Location loc = caboodle.parseLocation(logoutLoc);

				player.teleport(loc);
				sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7You have been teleported to &f" + otherPlayer.getName() + "&7's logout location."));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("tphere") && caboodle.hasPermission(sender, "tphere")) {
			if (args.length == 1) {
				return caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("tp2p") && caboodle.hasPermission(sender, "tp2p")) {
			if (args.length == 1) {
				return caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("tpall") && caboodle.hasPermission(sender, "tpall")) {
			if (args.length == 1) {
				return caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("tpoffline") && caboodle.hasPermission(sender, "tpoffline")) {
			if (args.length == 1) {
				return caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}

		return null;
	}
}
