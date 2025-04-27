package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.storage.DataStorage;
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
		DataStorage playerData = Paper.data().getStorage("players.yml");

		if (label.equalsIgnoreCase("whois")) {
			if (!Caboodle.hasPermission(sender, "whois")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/whois <player>"));
				return true;
			}

			OfflinePlayer player = Caboodle.getOfflinePlayer(args[0]);

			String uuid = player.getUniqueId().toString();

			if (!player.hasPlayedBefore()) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7That player has never played on this server."));
				return true;
			}

			// TODO: health, hunger, exp, loc, playtime, has flight

			sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Whois &f" + args[0] + "&7?"));
			sender.sendMessage(AwesomeText.prettifyMessage("&7- Username: &f" + player.getName()));
			sender.sendMessage(AwesomeText.prettifyMessage("&7- UUID: &f" + uuid));
			sender.sendMessage(AwesomeText.prettifyMessage("&7- Operator: &f" + player.isOp()));
			sender.sendMessage(AwesomeText.prettifyMessage("&7- IP Address: &f" + Optional.ofNullable(playerData.getString(uuid + ".ipaddress")).orElse("Unknown")));
			sender.sendMessage(AwesomeText.prettifyMessage("&7- Whitelisted: &f" + player.isWhitelisted()));
			sender.sendMessage(AwesomeText.prettifyMessage("&7- Banned: &f" + player.isBanned()));

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(player.getFirstPlayed());
			sender.sendMessage(AwesomeText.prettifyMessage("&7- First Join: &f" + calendar.getTime().toString()));

			if (player.isOnline()) {
				sender.sendMessage(AwesomeText.prettifyMessage("&7- Online: &ftrue"));
			} else {
				long timestamp = player.getLastPlayed();
				String date = AwesomeText.parseTime((System.currentTimeMillis() - timestamp) / 1000d);
				sender.sendMessage(AwesomeText.prettifyMessage("&7- Last Online: &f" + date + " ago"));
			}

			if (player.isOnline()) {
				Player onlinePlayer = (Player) player;

				Location loc = onlinePlayer.getLocation();
				sender.sendMessage(AwesomeText.prettifyMessage("&7- Location: &f" + loc.getWorld().getName() + " " + Caboodle.round(loc.getX(), 3) + " " + Caboodle.round(loc.getY(), 3) + " " + Caboodle.round(loc.getZ(), 3)));
			} else {
				String stringLoc = playerData.getString(uuid + ".logoutlocation");

				if (stringLoc == null) {
					sender.sendMessage(AwesomeText.prettifyMessage("&7- Logout Location: &fUnknown"));
				} else {
					Location loc = Caboodle.parseLocation(stringLoc);
					sender.sendMessage(AwesomeText.prettifyMessage("&7- Logout Location: &f" + loc.getWorld().getName() + " " + Caboodle.round(loc.getX(), 3) + " " + Caboodle.round(loc.getY(), 3) + " " + Caboodle.round(loc.getZ(), 3)));
				}
			}

			if (player.isOnline()) {
				Player onlinePlayer = (Player) player;

				sender.sendMessage(AwesomeText.prettifyMessage("&7- Can Fly: &f" + onlinePlayer.getAllowFlight()));
				sender.sendMessage(AwesomeText.prettifyMessage("&7- Health: &f" + Math.ceil(onlinePlayer.getHealth()) + "/" + onlinePlayer.getAttribute(Attribute.MAX_HEALTH).getBaseValue()));
				sender.sendMessage(AwesomeText.prettifyMessage("&7- Hunger: &f" + onlinePlayer.getFoodLevel() + "/20"));
				sender.sendMessage(AwesomeText.prettifyMessage("&7- Saturation: &f" + onlinePlayer.getSaturation()));
				sender.sendMessage(AwesomeText.prettifyMessage("&7- Locale: &f" + onlinePlayer.getLocale()));
				sender.sendMessage(AwesomeText.prettifyMessage("&7- View Distance: &f" + onlinePlayer.getClientViewDistance()));
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("whois") && Caboodle.hasPermission(sender, "whois")) {
			if (args.length == 1) {
				return Caboodle.getOfflinePlayerNames();
			}

			return Collections.emptyList();
		}

		return null;
	}
}
