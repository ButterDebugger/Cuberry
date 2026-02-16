package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.PaperCuberry;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.storage.DataStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Whois extends CommandWrapper {

	public Whois() {
		CommandRegistry whoisCmd = new CommandRegistry(this, "whois");
		whoisCmd.setDescription("Display player information");

		addRegistries(whoisCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		DataStorage playerData = PaperCuberry.data().getStorage("players.yml");

		if (label.equalsIgnoreCase("whois")) {
			if (!Caboodle.hasPermission(sender, "whois")) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/whois <player>"));
				return true;
			}

			OfflinePlayer player = Caboodle.getOfflinePlayer(args[0]);

			String uuid = player.getUniqueId().toString();

			if (!player.hasPlayedBefore()) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_never_joined", sender)));
				return true;
			}

			// TODO: health, hunger, exp, loc, playtime, has flight

			sender.sendMessage(AwesomeText.beautifyMessage(
				PaperCuberry.locale().getMessage("commands.whois.header", sender),
				Placeholder.unparsed("player_name", Objects.requireNonNull(player.getName()))
			));

			sender.sendMessage(beautifyEntry(sender, "username", player.getName()));
			sender.sendMessage(beautifyEntry(sender, "uuid", uuid));
			sender.sendMessage(beautifyEntry(sender, "is_operator", String.valueOf(player.isOp())));
			sender.sendMessage(beautifyEntry(sender, "ip_address", Optional.ofNullable(playerData.getString(uuid + ".ipaddress")).orElse("Unknown")));
			sender.sendMessage(beautifyEntry(sender, "is_whitelisted", String.valueOf(player.isWhitelisted())));
			sender.sendMessage(beautifyEntry(sender, "is_banned", String.valueOf(player.isBanned())));

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(player.getFirstPlayed());
			sender.sendMessage(beautifyEntry(sender, "first_join", calendar.getTime().toString()));

			if (player.isOnline()) {
				sender.sendMessage(beautifyEntry(sender, "is_online", "true"));
			} else {
				long timestamp = player.getLastSeen();
				String date = AwesomeText.parseTime((System.currentTimeMillis() - timestamp) / 1000d);
				sender.sendMessage(beautifyEntry(sender, "last_online", date + " ago"));
			}

			if (player.isOnline()) {
				Player onlinePlayer = (Player) player;

				Location loc = onlinePlayer.getLocation();
				sender.sendMessage(beautifyEntry(sender, "location", loc.getWorld().getName() + " " + Caboodle.round(loc.getX(), 3) + " " + Caboodle.round(loc.getY(), 3) + " " + Caboodle.round(loc.getZ(), 3)));
			} else {
				String stringLoc = playerData.getString(uuid + ".logoutlocation");

				if (stringLoc == null) {
					sender.sendMessage(beautifyEntry(sender, "logout_location", "Unknown"));
				} else {
					Location loc = Caboodle.parseLocation(stringLoc);
					sender.sendMessage(beautifyEntry(sender, "logout_location", loc.getWorld().getName() + " " + Caboodle.round(loc.getX(), 3) + " " + Caboodle.round(loc.getY(), 3) + " " + Caboodle.round(loc.getZ(), 3)));
				}
			}

			if (player.isOnline()) {
				Player onlinePlayer = (Player) player;

				sender.sendMessage(beautifyEntry(sender, "can_fly", String.valueOf(onlinePlayer.getAllowFlight())));
				sender.sendMessage(beautifyEntry(sender, "health", Math.ceil(onlinePlayer.getHealth()) + "/" + onlinePlayer.getAttribute(Attribute.MAX_HEALTH).getBaseValue()));
				sender.sendMessage(beautifyEntry(sender, "hunger", onlinePlayer.getFoodLevel() + "/20"));
				sender.sendMessage(beautifyEntry(sender, "saturation", String.valueOf(onlinePlayer.getSaturation())));
				sender.sendMessage(beautifyEntry(sender, "locale", onlinePlayer.locale().toString()));
				sender.sendMessage(beautifyEntry(sender, "view_distance", String.valueOf(onlinePlayer.getClientViewDistance())));
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

	private static Component beautifyEntry(CommandSender sender, String entry, String value) {
		return AwesomeText.beautifyMessage(
			PaperCuberry.locale().getMessage("commands.whois.entry." + entry, sender),
			Placeholder.unparsed("value", value)
		);
	}

}
