package com.butterycode.cubefruit;

import java.util.Arrays;
import java.util.List;

import com.butterycode.cubefruit.utils.awesomeText;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.dataStorage;
import com.butterycode.cubefruit.utils.dogTags;

public class playerLives implements Listener, CommandExecutor, TabCompleter {

	static FileConfiguration config = Main.plugin().config();
	dataStorage playerData = Main.plugin().getData("players.yml");

	public static void start() {
//		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin(), new Runnable() {
//			@Override
//			public void run() {
//				for (Player player : Bukkit.getOnlinePlayers()) {
//					// nothing here
//				}
//			}
//		}, 0, 1);
	}

	public void setLives(Player player, int amount) {
		playerData.set(player.getUniqueId() + ".lives", amount);
	}

	public int getLives(Player player) {
		if (playerData.existsNot(player.getUniqueId() + ".lives")) {
			playerData.set(player.getUniqueId() + ".lives", config.getInt("lives.max"));
		}
		return playerData.getInteger(player.getUniqueId() + ".lives");
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity().getPlayer();
		Entity killer = event.getEntity().getKiller();

		setLives(player, Math.max(0, getLives(player) - 1));

		if (killer instanceof Player) {
			Player murderer = (Player) killer;

			if (config.getBoolean("lives.lifesteal")) {
				setLives(murderer, getLives(murderer) + 1);
				murderer.sendMessage(awesomeText.colorize("&7You have gained one life from killing " + player.getName()));
			}
		}

		if (getLives(player) == 0) {
			if (event.getDeathMessage().equals(player.getName() + " died")) {
				event.setDeathMessage(player.getName() + " has lost their remaining lives due to a heart attack");
			} else {
				event.setDeathMessage(event.getDeathMessage() + " and lost all of their lives");
			}

			Location deathloc = player.getLocation();
			player.setGameMode(GameMode.SPECTATOR);

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin(), () -> {
				caboodle.respawn(player);
				player.teleport(deathloc);
			}, 0);
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();

		if (getLives(player) > 0) {
			player.sendMessage(awesomeText.colorize("&7You now have " + getLives(player) + " lives left."));
		} else {
			player.sendMessage(awesomeText.colorize("&7You no lives left."));
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("revive")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (args.length == 0) {
				sender.sendMessage(awesomeText.colorize("&cUsage: &7/revive <player>"));
				return true;
			}

			if (dogTags.isOnline(args[0])) {
				Player receiver = Bukkit.getPlayer(args[0]);

				if (receiver.equals(player)) {
					sender.sendMessage(awesomeText.colorize("&cError: &7You cannot revive yourself."));
					return true;
				}

				if (getLives(player) == 0) {
					sender.sendMessage(awesomeText.colorize("&cError: &7You don't have any lives to give away."));
					return true;
				}

				if (getLives(receiver) == 0 && receiver.getGameMode().equals(GameMode.SPECTATOR)) {
					if (receiver.getBedSpawnLocation() == null) {
						receiver.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
					} else {
						receiver.teleport(player.getBedSpawnLocation());
					}
					receiver.setGameMode(Bukkit.getDefaultGameMode());
				}

				if (getLives(player) == 1) {
					player.setHealth(0);
				} else {
					setLives(player, getLives(player) - 1);
				}

				setLives(receiver, getLives(receiver) + 1);

				sender.sendMessage(awesomeText.colorize("&7You now have " + getLives(player) + " lives remaining."));
				receiver.sendMessage(awesomeText.colorize("&7" + player.getName() + " has given you one of their lives."));

				receiver.playSound(receiver.getLocation(), Sound.ITEM_TOTEM_USE, 0.5f, 1f);
				return true;
			} else {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player could not be found."));
			}
		}
		if (label.equalsIgnoreCase("lives")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (args.length == 0) {
				if (caboodle.hasPermission(sender, "admin")) {
					sender.sendMessage(awesomeText.colorize("&cUsage: &7/lives <get|set> <player> [<number>]"));
				} else {
					sender.sendMessage(awesomeText.colorize("&cUsage: &7/lives <get> <player>"));
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("get")) {
				if (args.length > 1) {
					if (dogTags.isOnline(args[1])) {
						Player other = Bukkit.getPlayer(args[1]);

						sender.sendMessage(awesomeText.colorize("&7" + other.getName() + " currently has " + getLives(other) + " lives."));
						return true;
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player could not be found."));
						return true;
					}
				} else {
					sender.sendMessage(awesomeText.colorize("&7You currently have " + getLives(player) + " lives."));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("set")) {
				if (!caboodle.hasPermission(sender, "admin")) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
					return true;
				}

				if (args.length > 1) {
					if (dogTags.isOnline(args[1])) {
						Player other = Bukkit.getPlayer(args[1]);

						if (args.length > 2) {
							if (dogTags.isNumeric(args[2])) {
								int number = Math.max(0, Integer.valueOf(args[2]));

								setLives(other, number);

								sender.sendMessage(awesomeText.colorize("&7" + other.getName() + " now has " + number + " lives."));
								return true;
							} else {
								sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must enter a number."));
								return true;
							}
						} else {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
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
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) return null; // Cancel if sender isn't a player

		if (command.getName().equalsIgnoreCase("lives")) {
			if (args.length == 1) {
				if (caboodle.hasPermission(sender, "admin")) {
					return Arrays.asList(new String[] {"get", "set"});
				} else {
					return Arrays.asList(new String[] {"get"});
				}
			}
		}

		return null;
	}
}
