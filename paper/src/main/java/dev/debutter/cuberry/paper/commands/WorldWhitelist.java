package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.DogTags;
import dev.debutter.cuberry.paper.utils.storage.DataStorage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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

		if (!event.getFrom().getWorld().equals(to)) {
			if (isPlayerNotWhitelisted(player, to)) {
				event.setCancelled(true);

				if (checkForNotification(player, to)) {
					player.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("worldwhitelist.block_message", player), player, Placeholder.unparsed("world_name", to.getName())));
				}
			}
		}
	}

	private boolean isPlayerNotWhitelisted(Player player, World world) {
		DataStorage doubleData = Paper.data().getStorage("data.yml");

		if (!doubleData.getBoolean("worlds." + world.getName() + ".whitelist.enabled")) return false;

		return !doubleData.listContains("worlds." + world.getName() + ".whitelist.players", player.getUniqueId().toString());
	}

	private boolean checkForNotification(Player player, World world) {
		String notificationID = Instant.now().getEpochSecond() + ";" + world.getName() + ";" + player.getUniqueId();

		if (notificationCooldown.contains(notificationID)) return false;

		notificationCooldown.add(notificationID);
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		DataStorage doubleData = Paper.data().getStorage("data.yml");

		if (label.equalsIgnoreCase("worldwhitelist")) {
			if (!Caboodle.hasPermission(sender, "worldwhitelist")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/worldwhitelist <on|off|add|remove|list> <world> [<player>]"));
				return true;
			}

			if (args[0].equalsIgnoreCase("on")) {
				if (args.length > 1) {
					World world = Bukkit.getWorld(args[1]);

					if (world != null) {
						doubleData.set("worlds." + world.getName() + ".whitelist.enabled", true);
						sender.sendMessage(AwesomeText.colorize("&a&l» &7Whitelist for &f" + world.getName() + "&7 is now turned on."));
						return true;
					} else {
						sender.sendMessage(AwesomeText.colorize("&cError: &7World could not be found."));
						return true;
					}
				} else {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("off")) {
				if (args.length > 1) {
					World world = Bukkit.getWorld(args[1]);

					if (world != null) {
						doubleData.set("worlds." + world.getName() + ".whitelist.enabled", false);
						sender.sendMessage(AwesomeText.colorize("&a&l» &7Whitelist for &f" + world.getName() + "&7 is now turned off."));
						return true;
					} else {
						sender.sendMessage(AwesomeText.colorize("&cError: &7World could not be found."));
						return true;
					}
				} else {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("add")) {
				if (args.length > 1) {
					World world = Bukkit.getWorld(args[1]);

					if (world != null) {
						if (args.length > 2) {
							OfflinePlayer player;
							if (DogTags.isUUID(args[2])) {
								player = Caboodle.getOfflinePlayer(UUID.fromString(args[2]));
							} else {
								player = Caboodle.getOfflinePlayer(args[2]);
							}

							if (player != null) {
								String playerName = player.getName() == null ? args[2] : player.getName();

								if (!doubleData.listContains("worlds." + world.getName() + ".whitelist.players", player.getUniqueId().toString())) {
									doubleData.addToList("worlds." + world.getName() + ".whitelist.players", player.getUniqueId().toString());
									sender.sendMessage(AwesomeText.colorize("&a&l» &f" + playerName + "&7 has been added to &f" + world.getName() + "&7's whitelist."));
									return true;
								} else {
									sender.sendMessage(AwesomeText.colorize("&cError: &f" + playerName + "&7 is already in &f" + world.getName() + "&7's whitelist."));
									return true;
								}
							} else {
								sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_online", sender)));
								return true;
							}
						} else {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						}
					} else {
						sender.sendMessage(AwesomeText.colorize("&cError: &7World could not be found."));
						return true;
					}
				} else {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length > 1) {
					World world = Bukkit.getWorld(args[1]);

					if (world != null) {
						if (args.length > 2) {
							OfflinePlayer player;
							if (DogTags.isUUID(args[2])) {
								player = Caboodle.getOfflinePlayer(UUID.fromString(args[2]));
							} else {
								player = Caboodle.getOfflinePlayer(args[2]);
							}

							if (player != null) {
								String playerName = player.getName() == null ? args[2] : player.getName();

								if (doubleData.listContains("worlds." + world.getName() + ".whitelist.players", player.getUniqueId().toString())) {
									doubleData.removeFromList("worlds." + world.getName() + ".whitelist.players", player.getUniqueId().toString());
									sender.sendMessage(AwesomeText.colorize("&a&l» &f" + playerName + "&7 has been removed from &f" + world.getName() + "&7's whitelist."));
									return true;
								} else {
									sender.sendMessage(AwesomeText.colorize("&cError: &f" + playerName + "&7 is already not in &f" + world.getName() + "&7's whitelist."));
									return true;
								}
							} else {
								sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_online", sender)));
								return true;
							}
						} else {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						}
					} else {
						sender.sendMessage(AwesomeText.colorize("&cError: &7World could not be found."));
						return true;
					}
				} else {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
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
							sender.sendMessage(AwesomeText.colorize("&a&l» &7There are &f0&7 players in &f" + world.getName() + "&7's whitelist."));
						} else {
							sender.sendMessage(AwesomeText.colorize("&a&l» &7There are &f" + nameList.size() + "&7 players in &f" + world.getName() + "&7's whitelist: &f" + String.join("&7, &f", nameList) + "&7."));
						}
						return true;
					} else {
						sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
						return true;
					}
				} else {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
					return true;
				}
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		DataStorage doubleData = Paper.data().getStorage("data.yml");

		if (label.equalsIgnoreCase("worldwhitelist") && Caboodle.hasPermission(sender, "worldwhitelist")) {
			if (args.length == 1) {
				return Arrays.asList("on", "off", "add", "remove", "list");
			}
			if (args.length == 2) {
				return Caboodle.getWorldNames();
			}
			if (args.length == 3 && args[0].equals("add")) {
				return Caboodle.getOnlinePlayerNames();
			}
			if (args.length == 3 && args[0].equals("remove")) {
				List<String> uuidList = doubleData.getStringList("worlds." + args[1] + ".whitelist.players");
				List<String> nameList = new ArrayList<>();

				for (String uuid : uuidList) {
					String name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
					nameList.add(name == null ? uuid : name);
				}

				return nameList.size() == 0 ? Caboodle.getOnlinePlayerNames() : nameList;
			}

			return Collections.emptyList();
		}

		return null;
	}
}
