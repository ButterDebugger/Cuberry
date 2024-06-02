package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Back extends CommandWrapper implements Listener {

	private HashMap<UUID, Location> previousLocations = new HashMap<UUID, Location>();

	// TODO: add the ability to teleport others with the permission back.others

	public Back() {
		CommandRegistry backCmd = new CommandRegistry(this, "back");
		backCmd.setDescription("Teleport back to your location prior to teleporting");

		addRegistries(backCmd);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		Location from = event.getFrom();

		previousLocations.put(player.getUniqueId(), from);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		previousLocations.put(player.getUniqueId(), player.getLocation());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("back")) {
			if (!Caboodle.hasPermission(sender, "back")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

			Player player = (Player) sender;

			if (!previousLocations.containsKey(player.getUniqueId())) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have any place to return back to."));
				return true;
			}

			player.teleport(previousLocations.get(player.getUniqueId()));

			sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7You have been teleported back to your previous location."));
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("back") && Caboodle.hasPermission(sender, "back")) {
			return Collections.emptyList();
		}

		return null;
	}
}
