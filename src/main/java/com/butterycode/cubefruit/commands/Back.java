package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
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
			if (!caboodle.hasPermission(sender, "back")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!previousLocations.containsKey(player.getUniqueId())) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have any place to return back to."));
				return true;
			}

			player.teleport(previousLocations.get(player.getUniqueId()));

			sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7You have been teleported back to your previous location."));
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("back") && caboodle.hasPermission(sender, "back")) {
			return Collections.emptyList();
		}

		return null;
	}
}
