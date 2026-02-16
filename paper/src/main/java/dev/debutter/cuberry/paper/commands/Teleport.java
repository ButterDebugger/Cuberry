package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.PaperCuberry;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.DogTags;
import dev.debutter.cuberry.paper.utils.storage.DataStorage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
		DataStorage playerData = PaperCuberry.data().getStorage("players.yml");

		if (label.equalsIgnoreCase("tphere")) {
			if (!(sender instanceof Player player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_required", sender)));
				return true;
			}

            if (!Caboodle.hasPermission(sender, "tphere")) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/tphere <username>"));
				return true;
			}

			String username = args[0];

			if (!DogTags.isOnline(username)) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_not_online", sender)));
				return true;
			}

			Player otherPlayer = Bukkit.getPlayer(username);

			otherPlayer.teleport(player.getLocation());

			sender.sendMessage(AwesomeText.beautifyMessage(
				PaperCuberry.locale().getMessage("commands.teleport.someone_to_you", sender),
				Placeholder.unparsed("player_name", otherPlayer.getName())
			));
			return true;
		} else if (label.equalsIgnoreCase("tp2p")) {
			if (!(sender instanceof Player player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_required", sender)));
				return true;
			}

            if (!Caboodle.hasPermission(sender, "tp2p")) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/tp2p <username>"));
				return true;
			}

			String username = args[0];

			if (!DogTags.isOnline(username)) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_not_online", sender)));
				return true;
			}

			Player otherPlayer = Bukkit.getPlayer(username);

			player.teleport(otherPlayer.getLocation());

			sender.sendMessage(AwesomeText.beautifyMessage(
				PaperCuberry.locale().getMessage("commands.teleport.you_to_someone", sender),
				Placeholder.unparsed("player_name", otherPlayer.getName())
			));
			return true;
		} else if (label.equalsIgnoreCase("tpall")) {
			if (!Caboodle.hasPermission(sender, "tpall")) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player player)) {
					sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_required", sender)));
					return true;
				}

                Location location = player.getLocation();

				for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
					otherPlayer.teleport(location);
				}

				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.teleport.all_to_you", sender)));
				return true;
			} else {
				String username = args[0];

				if (!DogTags.isOnline(username)) {
					sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_not_online", sender)));
					return true;
				}

				Player player = Bukkit.getPlayer(username);
				Location location = player.getLocation();

				for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
					otherPlayer.teleport(location);
				}

				sender.sendMessage(AwesomeText.beautifyMessage(
					PaperCuberry.locale().getMessage("commands.teleport.all_to_someone", sender),
					Placeholder.unparsed("player_name", player.getName())
				));
				return true;
			}
		} else if (label.equalsIgnoreCase("tpoffline")) {
			if (!(sender instanceof Player player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_required", sender)));
				return true;
			}

            if (!Caboodle.hasPermission(sender, "tpoffline")) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/tpoffline <username>"));
				return true;
			}

			String username = args[0];
			OfflinePlayer otherPlayer = Caboodle.getOfflinePlayer(username);
			UUID otherUUID = otherPlayer.getUniqueId();

			if (!otherPlayer.hasPlayedBefore()) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_never_joined", sender)));
				return true;
			}

			if (otherPlayer.isOnline()) {
				String logoutLoc = playerData.getString(otherUUID + ".logoutlocation");

				if (logoutLoc == null) {
					sender.sendMessage(AwesomeText.beautifyMessage(
						PaperCuberry.locale().getMessage("commands.teleport.unknown_logout_location", sender),
						Placeholder.unparsed("player_name", Objects.requireNonNull(otherPlayer.getName()))
					));
					return true;
				}

				Location loc = Caboodle.parseLocation(logoutLoc);

				player.teleport(loc);
				sender.sendMessage(AwesomeText.beautifyMessage(
					PaperCuberry.locale().getMessage("commands.teleport.you_to_logout_location", sender),
					Placeholder.unparsed("player_name", Objects.requireNonNull(otherPlayer.getName()))
				));
				return true;
			} else {
				String logoutLoc = playerData.getString(otherUUID + ".logoutlocation");

				if (logoutLoc == null) {
					sender.sendMessage(AwesomeText.beautifyMessage(
						PaperCuberry.locale().getMessage("commands.teleport.unknown_logout_location", sender),
						Placeholder.unparsed("player_name", Objects.requireNonNull(otherPlayer.getName()))
					));
					return true;
				}

				Location loc = Caboodle.parseLocation(logoutLoc);

				player.teleport(loc);
				sender.sendMessage(AwesomeText.beautifyMessage(
					PaperCuberry.locale().getMessage("commands.teleport.you_to_logout_location", sender),
					Placeholder.unparsed("player_name", Objects.requireNonNull(otherPlayer.getName()))
				));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("tphere") && Caboodle.hasPermission(sender, "tphere")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("tp2p") && Caboodle.hasPermission(sender, "tp2p")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("tpall") && Caboodle.hasPermission(sender, "tpall")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("tpoffline") && Caboodle.hasPermission(sender, "tpoffline")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}

		return null;
	}
}
