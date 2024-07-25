package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.storage.DataStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Collections;
import java.util.List;

public class Home extends CommandWrapper implements Listener {

	public Home() {
		CommandRegistry homeCmd = new CommandRegistry(this, "home");
		homeCmd.setDescription("Teleport to your home");

		CommandRegistry sethomeCmd = new CommandRegistry(this, "sethome");
		sethomeCmd.setDescription("Set your home to your current location");

		CommandRegistry delhomeCmd = new CommandRegistry(this, "delhome");
		delhomeCmd.setDescription("Remove your home");

		addRegistries(homeCmd, sethomeCmd, delhomeCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		DataStorage playerData = Paper.data().getStorage("players.yml");

		if (label.equalsIgnoreCase("home")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

			Player player = (Player)sender;

			if (!Caboodle.hasPermission(sender, "home")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length != 0) {
				sender.sendMessage(AwesomeText.colorize("&cUsage: &7/home"));
				return true;
			}

			if (!playerData.exists(player.getUniqueId() + ".home")) {
				sender.sendMessage(AwesomeText.colorize("&cError: &7You do not have a home set."));
				return true;
			}

			Location homeLoc = Caboodle.parseLocation(playerData.getString(player.getUniqueId() + ".home"));

			sender.sendMessage(AwesomeText.colorize("&a&l� &7You have been teleported home."));
			player.teleport(homeLoc);
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1f);
			return true;
		} else if (label.equalsIgnoreCase("sethome")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

			Player player = (Player) sender;

			if (!Caboodle.hasPermission(sender, "sethome")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length != 0) {
				sender.sendMessage(AwesomeText.colorize("&cUsage: &7/sethome"));
				return true;
			}

			sender.sendMessage(AwesomeText.colorize("&a&l� &7Your home has been set."));
			playerData.set(player.getUniqueId() + ".home", Caboodle.stringifyLocation(player));
			return true;
		} else if (label.equalsIgnoreCase("delhome")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

			Player player = (Player)sender;

			if (!Caboodle.hasPermission(sender, "delhome")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length != 0) {
				sender.sendMessage(AwesomeText.colorize("&cUsage: &7/delhome"));
				return true;
			}

			if (playerData.get(player.getUniqueId() + ".home") == null) {
				sender.sendMessage(AwesomeText.colorize("&cError: &7You don't already have a home."));
				return true;
			} else {
				playerData.remove(player.getUniqueId() + ".home");
				sender.sendMessage(AwesomeText.colorize("&a&l� &7Your home has been deleted."));
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		DataStorage playerData = Paper.data().getStorage("players.yml");

		if (Paper.plugin().getConfig().getBoolean("commands.spawn.spawn-on-death")) {
			if (!playerData.exists(player.getUniqueId() + ".home") || player.getBedLocation() != null) return;

			Location homeLoc = Caboodle.parseLocation(playerData.getString(player.getUniqueId() + ".home"));

			Bukkit.getScheduler().scheduleSyncDelayedTask(Paper.plugin(), () -> {
				player.teleport(homeLoc);
			}, 2L);
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("home") && Caboodle.hasPermission(sender, "home")) {
			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("sethome") && Caboodle.hasPermission(sender, "sethome")) {
			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("delhome") && Caboodle.hasPermission(sender, "delhome")) {
			return Collections.emptyList();
		}

		return null;
	}
}
