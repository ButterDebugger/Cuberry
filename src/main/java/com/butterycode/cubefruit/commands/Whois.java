package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.dataStorage;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Whois extends CommandWrapper {

	public Whois() {
		CommandRegistry whoisCmd = new CommandRegistry(this, "whois");
		whoisCmd.setDescription("Display player information");

		addRegistries(whoisCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		dataStorage playerData = Main.plugin().getData("players.yml");

		if (label.equalsIgnoreCase("whois")) {
			if (!caboodle.hasPermission(sender, "whois")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(awesomeText.prettifyMessage("&3Usage: &7/whois <player>"));
				return true;
			}

			OfflinePlayer player = caboodle.getOfflinePlayer(args[0]);

			String uuid = player.getUniqueId().toString();

			if (!player.hasPlayedBefore()) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player has never played on this server."));
				return true;
			}

			// TODO: health, hunger, exp, loc, playtime, has flight

			sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Whois &f" + args[0] + "&7?"));
			sender.sendMessage(awesomeText.prettifyMessage("&7- Username: &f" + player.getName()));
			sender.sendMessage(awesomeText.prettifyMessage("&7- UUID: &f" + uuid));
			sender.sendMessage(awesomeText.prettifyMessage("&7- Operator: &f" + player.isOp()));
			sender.sendMessage(awesomeText.prettifyMessage("&7- IP Address: &f" + Optional.ofNullable(playerData.getString(uuid + ".ipaddress")).orElse("Unknown")));
			sender.sendMessage(awesomeText.prettifyMessage("&7- Whitelisted: &f" + player.isWhitelisted()));
			sender.sendMessage(awesomeText.prettifyMessage("&7- Banned: &f" + player.isBanned()));

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(player.getFirstPlayed());
			sender.sendMessage(awesomeText.prettifyMessage("&7- First Join: &f" + calendar.getTime().toString()));

			if (player.isOnline()) {
				sender.sendMessage(awesomeText.prettifyMessage("&7- Online: &ftrue"));
			} else {
				long timestamp = player.getLastPlayed();
				String date = awesomeText.parseTime((System.currentTimeMillis() - timestamp) / 1000d);
				sender.sendMessage(awesomeText.prettifyMessage("&7- Last Online: &f" + date + " ago"));
			}

			if (player.isOnline()) {
				Player onlinePlayer = (Player) player;

				Location loc = onlinePlayer.getLocation();
				sender.sendMessage(awesomeText.prettifyMessage("&7- Location: &f" + loc.getWorld().getName() + " " + caboodle.round(loc.getX(), 3) + " " + caboodle.round(loc.getY(), 3) + " " + caboodle.round(loc.getZ(), 3)));
			} else {
				String stringLoc = playerData.getString(uuid + ".logoutlocation");

				if (stringLoc == null) {
					sender.sendMessage(awesomeText.prettifyMessage("&7- Logout Location: &fUnknown"));
				} else {
					Location loc = caboodle.parseLocation(stringLoc);
					sender.sendMessage(awesomeText.prettifyMessage("&7- Logout Location: &f" + loc.getWorld().getName() + " " + caboodle.round(loc.getX(), 3) + " " + caboodle.round(loc.getY(), 3) + " " + caboodle.round(loc.getZ(), 3)));
				}
			}

			if (player.isOnline()) {
				Player onlinePlayer = (Player) player;

				sender.sendMessage(awesomeText.prettifyMessage("&7- Can Fly: &f" + onlinePlayer.getAllowFlight()));
				sender.sendMessage(awesomeText.prettifyMessage("&7- Health: &f" + Math.ceil(onlinePlayer.getHealth()) + "/" + onlinePlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
				sender.sendMessage(awesomeText.prettifyMessage("&7- Hunger: &f" + onlinePlayer.getFoodLevel() + "/20"));
				sender.sendMessage(awesomeText.prettifyMessage("&7- Saturation: &f" + onlinePlayer.getSaturation()));
				sender.sendMessage(awesomeText.prettifyMessage("&7- Locale: &f" + onlinePlayer.getLocale()));
				sender.sendMessage(awesomeText.prettifyMessage("&7- View Distance: &f" + onlinePlayer.getClientViewDistance()));
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("whois") && caboodle.hasPermission(sender, "whois")) {
			if (args.length == 1) {
				return caboodle.getOfflinePlayerNames();
			}

			return Collections.emptyList();
		}

		return null;
	}
}
