package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.DataStorage;
import com.butterycode.cubefruit.utils.DogTags;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.time.Instant;
import java.util.*;

public class WorldWhitelist extends CommandWrapper implements Listener {

	private List<String> notificationCooldown = new ArrayList<>();

	public WorldWhitelist() {
		CommandRegistry worldWhitelistCmd = new CommandRegistry(this, "worldwhitelist");
		worldWhitelistCmd.setDescription("Whitelist players to travel to certain worlds");

		addRegistries(worldWhitelistCmd);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		World to = event.getTo().getWorld();
		DataStorage doubleData = Main.plugin().getData("data.yml");

		if (!event.getFrom().getWorld().equals(to)) { // TODO: test this condition
			if (doubleData.getBoolean("worlds." + to.getName() + ".whitelist.enabled")) {
				if (!doubleData.listContains("worlds." + to.getName() + ".whitelist.players", player.getUniqueId().toString())) {
					event.setCancelled(true);

					String notificationID = Instant.now().getEpochSecond() + ";" + to.getName() + ";" + player.getUniqueId();
					if (!notificationCooldown.contains(notificationID)) {
						player.sendMessage(awesomeText.colorize("&cError: &7You cannot travel to &f" + to.getName() + "&7 because you are not whitelisted."));
						notificationCooldown.add(notificationID);
					}
				}
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		DataStorage doubleData = Main.plugin().getData("data.yml");

		if (label.equalsIgnoreCase("worldwhitelist")) {
			if (!caboodle.hasPermission(sender, "worldwhitelist")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(awesomeText.colorize("&3Usage: &7/worldwhitelist <on|off|add|remove|list> <world> [<player>]"));
				return true;
			}

			if (args[0].equalsIgnoreCase("on")) {
				if (args.length > 1) {
					World world = Bukkit.getWorld(args[1]);

					if (world != null) {
						doubleData.set("worlds." + world.getName() + ".whitelist.enabled", true);
						sender.sendMessage(awesomeText.colorize("&a&l» &7Whitelist for &f" + world.getName() + "&7 is now turned on."));
						return true;
					} else {
						sender.sendMessage(awesomeText.colorize("&cError: &7World could not be found."));
						return true;
					}
				} else {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("off")) {
				if (args.length > 1) {
					World world = Bukkit.getWorld(args[1]);

					if (world != null) {
						doubleData.set("worlds." + world.getName() + ".whitelist.enabled", false);
						sender.sendMessage(awesomeText.colorize("&a&l» &7Whitelist for &f" + world.getName() + "&7 is now turned off."));
						return true;
					} else {
						sender.sendMessage(awesomeText.colorize("&cError: &7World could not be found."));
						return true;
					}
				} else {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("add")) {
				if (args.length > 1) {
					World world = Bukkit.getWorld(args[1]);

					if (world != null) {
						if (args.length > 2) {
							OfflinePlayer player;
							if (DogTags.isUUID(args[2])) {
								player = caboodle.getOfflinePlayer(UUID.fromString(args[2]));
							} else {
								player = caboodle.getOfflinePlayer(args[2]);
							}

							if (player != null) {
								String playerName = player.getName() == null ? args[2] : player.getName();

								if (!doubleData.listContains("worlds." + world.getName() + ".whitelist.players", player.getUniqueId().toString())) {
									doubleData.addToList("worlds." + world.getName() + ".whitelist.players", player.getUniqueId().toString());
									sender.sendMessage(awesomeText.colorize("&a&l» &f" + playerName + "&7 has been added to &f" + world.getName() + "&7's whitelist."));
									return true;
								} else {
									sender.sendMessage(awesomeText.colorize("&cError: &f" + playerName + "&7 is already in &f" + world.getName() + "&7's whitelist."));
									return true;
								}
							} else {
								sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player could not be found."));
								return true;
							}
						} else {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
							return true;
						}
					} else {
						sender.sendMessage(awesomeText.colorize("&cError: &7World could not be found."));
						return true;
					}
				} else {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length > 1) {
					World world = Bukkit.getWorld(args[1]);

					if (world != null) {
						if (args.length > 2) {
							OfflinePlayer player;
							if (DogTags.isUUID(args[2])) {
								player = caboodle.getOfflinePlayer(UUID.fromString(args[2]));
							} else {
								player = caboodle.getOfflinePlayer(args[2]);
							}

							if (player != null) {
								String playerName = player.getName() == null ? args[2] : player.getName();

								if (doubleData.listContains("worlds." + world.getName() + ".whitelist.players", player.getUniqueId().toString())) {
									doubleData.removeFromList("worlds." + world.getName() + ".whitelist.players", player.getUniqueId().toString());
									sender.sendMessage(awesomeText.colorize("&a&l» &f" + playerName + "&7 has been removed from &f" + world.getName() + "&7's whitelist."));
									return true;
								} else {
									sender.sendMessage(awesomeText.colorize("&cError: &f" + playerName + "&7 is already not in &f" + world.getName() + "&7's whitelist."));
									return true;
								}
							} else {
								sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player could not be found."));
								return true;
							}
						} else {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
							return true;
						}
					} else {
						sender.sendMessage(awesomeText.colorize("&cError: &7World could not be found."));
						return true;
					}
				} else {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (args.length > 1) {
					World world = Bukkit.getWorld(args[1]);

					if (world != null) {
						List<String> uuidList = doubleData.getStringList("worlds." + world.getName() + ".whitelist.players");
						List<String> nameList = new ArrayList<>();

						for (String uuid : uuidList) {
							String name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
							nameList.add(name == null ? uuid : name);
						}

						if (nameList.size() == 0) {
							sender.sendMessage(awesomeText.colorize("&a&l» &7There are &f0&7 players in &f" + world.getName() + "&7's whitelist."));
						} else {
							sender.sendMessage(awesomeText.colorize("&a&l» &7There are &f" + nameList.size() + "&7 players in &f" + world.getName() + "&7's whitelist: &f" + String.join("&7, &f", nameList) + "&7."));
						}
						return true;
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
						return true;
					}
				} else {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
					return true;
				}
			} else {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		DataStorage doubleData = Main.plugin().getData("data.yml");

		if (label.equalsIgnoreCase("worldwhitelist") && caboodle.hasPermission(sender, "worldwhitelist")) {
			if (args.length == 1) {
				return Arrays.asList("on", "off", "add", "remove", "list");
			}
			if (args.length == 2) {
				return caboodle.getWorldNames();
			}
			if (args.length == 3 && args[0].equals("add")) {
				return caboodle.getOnlinePlayerNames();
			}
			if (args.length == 3 && args[0].equals("remove")) {
				List<String> uuidList = doubleData.getStringList("worlds." + args[1] + ".whitelist.players");
				List<String> nameList = new ArrayList<>();

				for (String uuid : uuidList) {
					String name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
					nameList.add(name == null ? uuid : name);
				}

				return nameList.size() == 0 ? caboodle.getOnlinePlayerNames() : nameList;
			}

			return Collections.emptyList();
		}

		return null;
	}
}
