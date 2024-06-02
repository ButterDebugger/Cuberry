package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.DogTags;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tpa extends CommandWrapper {

	private static Map<String, RequestType> requests = new HashMap<>();

	private enum RequestType {
		TPA,
		TPAHERE
	}

	// TODO: when players leave game, remove them from requests
	// TODO: have tab complete autofill incoming requesters
	// TODO: prevent sending a request to yourself

	public Tpa() {
		CommandRegistry tpaCmd = new CommandRegistry(this, "tpa");
		tpaCmd.setDescription("Request to teleport to another player");

		CommandRegistry tpahereCmd = new CommandRegistry(this, "tpahere");
		tpahereCmd.setDescription("Request another player to teleport to you");

		CommandRegistry tpacceptCmd = new CommandRegistry(this, "tpaccept");
		tpacceptCmd.setDescription("Accept others teleport requests");

		CommandRegistry tpadenyCmd = new CommandRegistry(this, "tpadeny");
		tpadenyCmd.setDescription("Deny others teleport requests");

		CommandRegistry tpacancelCmd = new CommandRegistry(this, "tpacancel");
		tpacancelCmd.setDescription("Cancel your teleport requests");

		addRegistries(tpaCmd, tpahereCmd, tpacceptCmd, tpadenyCmd, tpacancelCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
			return true;
		}

		Player player = (Player) sender;

		if (label.equalsIgnoreCase("tpa")) {
			if (!Caboodle.hasPermission(sender, "tpa")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/tpa <username>"));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player otherPlayer = Bukkit.getPlayer(args[0]);
				String requestKey = player.getName() + ";" + otherPlayer.getName();

				requests.put(requestKey, RequestType.TPA);
				player.sendMessage(AwesomeText.colorize("&a&l» &7Request sent!"));
				otherPlayer.sendMessage(AwesomeText.colorize("&a&l» &f" + player.getName() + "&7 has sent you a tpa request to teleport to you."));
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_found", sender)));
			}
			return true;
		} else if (label.equalsIgnoreCase("tpahere")) {
			if (!Caboodle.hasPermission(sender, "tpahere")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/tpahere <username>"));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player otherPlayer = Bukkit.getPlayer(args[0]);
				String requestKey = player.getName() + ";" + otherPlayer.getName();

				requests.put(requestKey, RequestType.TPAHERE);
				player.sendMessage(AwesomeText.colorize("&a&l» &7Request sent!"));
				otherPlayer.sendMessage(AwesomeText.colorize("&a&l» &f" + player.getName() + "&7 has sent you a tpa request to teleport to them."));
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_found", sender)));
			}
			return true;
		} else if (label.equalsIgnoreCase("tpaccept")) {
			if (!Caboodle.hasPermission(sender, "tpaccept")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/tpaccept <username>"));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player otherPlayer = Bukkit.getPlayer(args[0]);
				String requestKey = otherPlayer.getName() + ";" + player.getName();

				if (requests.containsKey(requestKey)) {
					RequestType req = requests.get(requestKey);

					switch (req) {
						case TPA:
							sender.sendMessage(AwesomeText.colorize("&a&l» &f" + otherPlayer.getName() + "&7 has been teleported to you."));
							otherPlayer.teleport(player.getLocation());
							otherPlayer.playSound(otherPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1f);
							break;
						case TPAHERE:
							sender.sendMessage(AwesomeText.colorize("&a&l» &7You have been teleported to &f" + otherPlayer.getName() + "&7."));
							player.teleport(otherPlayer.getLocation());
							player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1f);
							break;
					}

					requests.remove(requestKey);
				} else {
					sender.sendMessage(AwesomeText.colorize("&cError: &7You do not have any requests from that player."));
				}
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_found", sender)));
			}
			return true;
		} else if (label.equalsIgnoreCase("tpadeny")) {
			if (!Caboodle.hasPermission(sender, "tpadeny")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/tpadeny <username>"));
				return true;
			}

			String requestKey = args[0] + ";" + player.getName();

			if (requests.containsKey(requestKey)) {
				requests.remove(requestKey);
				sender.sendMessage(AwesomeText.colorize("&a&l» &7You have denied the request."));
			} else {
				sender.sendMessage(AwesomeText.colorize("&cError: &7You do not have any requests from that player."));
			}
			return true;
		} else if (label.equalsIgnoreCase("tpacancel")) {
			if (!Caboodle.hasPermission(sender, "tpacancel")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/tpacancel <username>"));
				return true;
			}

			String requestKey = player.getName() + ";" + args[0];

			if (requests.containsKey(requestKey)) {
				requests.remove(requestKey);
				sender.sendMessage(AwesomeText.colorize("&a&l» &7You have cancelled the request."));
			} else {
				sender.sendMessage(AwesomeText.colorize("&cError: &7You did not send any requests to that player."));
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("tpa") && Caboodle.hasPermission(sender, "tpa")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("tpahere") && Caboodle.hasPermission(sender, "tpahere")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("tpaccept") && Caboodle.hasPermission(sender, "tpaccept")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("tpadeny") && Caboodle.hasPermission(sender, "tpadeny")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("tpacancel") && Caboodle.hasPermission(sender, "tpacancel")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}

		return null;
	}
}
