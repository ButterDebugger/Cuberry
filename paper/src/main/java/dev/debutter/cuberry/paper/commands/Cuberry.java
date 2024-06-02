package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.DogTags;
import dev.debutter.cuberry.paper.utils.TooManyParticles;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static net.kyori.adventure.text.Component.translatable;

public class Cuberry extends CommandWrapper {

	private static ArrayList<String> tempworlds = new ArrayList<>();
	private static HashMap<Player, Boolean> lagmonitor = new HashMap<>();

	public Cuberry() {
		CommandRegistry cuberryCmd = new CommandRegistry(this, "cuberry");
		cuberryCmd.addAliases("cubefruit", "fruit", "cf", "cb");
		cuberryCmd.setDescription("The Cuberry command");

		addRegistries(cuberryCmd);
	}

	@Override
	@SuppressWarnings({ "deprecation", "null" })
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = Paper.plugin().getConfig();

		if (
			label.equalsIgnoreCase("cuberry") ||
			label.equalsIgnoreCase("cubefruit") ||
			label.equalsIgnoreCase("fruit") ||
			label.equalsIgnoreCase("cf") ||
			label.equalsIgnoreCase("cb")
		) {
			if (!Caboodle.hasPermission(sender, "admin")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/" + label.toLowerCase() + " <arguments>"));
				return true;
			}

			if (args[0].equalsIgnoreCase("reload")) {
				sender.sendMessage(AwesomeText.prettifyMessage("&b[&3&l!&b] &aPlugin has been Reloaded!"));
				Paper.plugin().reload();
				return true;
			} else if (args[0].equalsIgnoreCase("test")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
					return true;
				}
				Player player = (Player) sender;

				sender.sendMessage(AwesomeText.prettifyMessage("&7--- &astart of test&7 ---"));

				sender.sendMessage(AwesomeText.beautifyMessage("<gold>running particle test", player));
				TooManyParticles.test();

				sender.sendMessage(AwesomeText.prettifyMessage("&7--- &cend  of  test&7 ---"));
				return true;
			} else if (args[0].equalsIgnoreCase("debug")) {
				if (!config.getBoolean("debug")) {
					sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7This feature is not enabled in the config."));
					return true;
				}

				if (args.length <= 1) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
					return true;
				} else if (args[1].equalsIgnoreCase("player")) {
					if (args.length < 3) {
						sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
						return true;
					} else if (args[2].equalsIgnoreCase("sethunger")) {
						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						} else if (DogTags.isOnline(args[3])) {
							Player other = Bukkit.getPlayer(args[3]);

							if (args.length < 5) {
								sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
								return true;
							} else if (DogTags.isNumeric(args[4])) {
								int number = Math.min(Integer.parseInt(args[4]), 20);

								other.setFoodLevel(number);
								sender.sendMessage(AwesomeText.colorize("&b" + other.getName() + "&3's hunger has been set to &b" + number));
								return true;
							} else {
								sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
								return true;
							}
						} else {
							sender.sendMessage(AwesomeText.colorize(config.getString("messages.playernotfound")));
							return true;
						}
					} else if (args[2].equalsIgnoreCase("sethealth")) {
						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						} else if (DogTags.isOnline(args[3])) {
							Player other = Bukkit.getPlayer(args[3]);

							if (args.length < 5) {
								sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
								return true;
							} else if (DogTags.isNumeric(args[4])) {
								double number = Math.min(Integer.parseInt(args[4]), other.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());

								other.setHealth(number);
								sender.sendMessage(AwesomeText.colorize("&b" + other.getName() + "&3's health has been set to &b" + number));
								return true;
							} else {
								sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
								return true;
							}
						} else {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_found", sender)));
							return true;
						}
					} else if (args[2].equalsIgnoreCase("hide")) {
						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						} else if (DogTags.isOnline(args[3])) {
							Player hidden = Bukkit.getPlayer(args[3]);

							if (args.length < 5) {
								sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
								return true;
							} else if (DogTags.isOnline(args[4])) {
								Player other = Bukkit.getPlayer(args[4]);
								other.hidePlayer(Paper.plugin(), hidden);
								sender.sendMessage(AwesomeText.colorize("&b" + hidden.getName() + " &3is now hidden from &b" + other.getName()));
								return true;
							} else {
								sender.sendMessage(AwesomeText.colorize(config.getString("messages.playernotfound")));
								return true;
							}
						} else {
							sender.sendMessage(AwesomeText.colorize(config.getString("messages.playernotfound")));
							return true;
						}
					} else if (args[2].equalsIgnoreCase("show")) {
						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						} else if (DogTags.isOnline(args[3])) {
							Player shown = Bukkit.getPlayer(args[3]);

							if (args.length < 5) {
								sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
								return true;
							} else if (DogTags.isOnline(args[4])) {
								Player other = Bukkit.getPlayer(args[4]);
								other.showPlayer(Paper.plugin(), shown);
								sender.sendMessage(AwesomeText.colorize("b" + shown.getName() + " &3is now visible to &b" + other.getName()));
								return true;
							} else {
								sender.sendMessage(AwesomeText.colorize(config.getString("messages.playernotfound")));
								return true;
							}
						} else {
							sender.sendMessage(AwesomeText.colorize(config.getString("messages.playernotfound")));
							return true;
						}
					} else if (args[2].equalsIgnoreCase("dupeinventory")) {
						if (!(sender instanceof Player)) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
							return true;
						}
						Player player = (Player) sender;

						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						} else if (DogTags.isOnline(args[3])) {
							Player other = Bukkit.getPlayer(args[3]);

							Inventory inventory = Bukkit.createInventory(null, 45, other.getName() + "'s Inventory");

							inventory.setContents(other.getInventory().getContents());

							player.openInventory(inventory);
						} else {
							sender.sendMessage(AwesomeText.colorize(config.getString("messages.playernotfound")));
							return true;
						}
					} else if (args[2].equalsIgnoreCase("dupeenderchest")) {
						if (!(sender instanceof Player)) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
							return true;
						}
						Player player = (Player) sender;

						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						} else if (DogTags.isOnline(args[3])) {
							Player other = Bukkit.getPlayer(args[3]);

							Inventory inventory = Bukkit.createInventory(null, 27, other.getName() + "'s Ender Chest");

							inventory.setContents(other.getEnderChest().getContents());

							player.openInventory(inventory);
						} else {
							sender.sendMessage(AwesomeText.colorize(config.getString("messages.playernotfound")));
							return true;
						}
					} else if (args[2].equalsIgnoreCase("setarrowsinbody")) {
						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						} else if (DogTags.isOnline(args[3])) {
							Player other = Bukkit.getPlayer(args[3]);

							if (args.length < 5) {
								sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
								return true;
							} else if (DogTags.isNumeric(args[4])) {
								int number = Integer.parseInt(args[4]);

								other.setArrowsInBody(number);
								sender.sendMessage(AwesomeText.colorize("&b" + other.getName() + "&3 now has &b" + number + "&3 arrows in their body"));
								return true;
							} else {
								sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
								return true;
							}
						} else {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_found", sender)));
							return true;
						}
					} else if (args[2].equalsIgnoreCase("ride")) {
						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						} else if (DogTags.isOnline(args[3])) {
							Player rider = Bukkit.getPlayer(args[3]);

							if (args.length < 5) {
								sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
								return true;
							} else if (DogTags.isOnline(args[4])) {
								Player other = Bukkit.getPlayer(args[4]);
								other.setPassenger(rider);
								sender.sendMessage(AwesomeText.colorize("&b" + rider.getName() + " &3is now riding &b" + other.getName()));
								return true;
							} else {
								sender.sendMessage(AwesomeText.colorize(config.getString("messages.playernotfound")));
								return true;
							}
						} else {
							sender.sendMessage(AwesomeText.colorize(config.getString("messages.playernotfound")));
							return true;
						}
					} else if (args[2].equalsIgnoreCase("swap")) {
						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						} else if (DogTags.isOnline(args[3])) {
							Player player = Bukkit.getPlayer(args[3]);

							if (args.length < 5) {
								sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
								return true;
							} else if (DogTags.isOnline(args[4])) {
								Player other = Bukkit.getPlayer(args[4]);

								Location playerLoc = player.getLocation();
								Vector playerVel = player.getVelocity();
								player.teleport(other.getLocation());
								player.setVelocity(other.getVelocity());
								other.teleport(playerLoc);
								other.setVelocity(playerVel);

								sender.sendMessage(AwesomeText.colorize("&b" + player.getName() + " &3has swapped positions with &b" + other.getName()));
								return true;
							} else {
								sender.sendMessage(AwesomeText.colorize(config.getString("messages.playernotfound")));
								return true;
							}
						} else {
							sender.sendMessage(AwesomeText.colorize(config.getString("messages.playernotfound")));
							return true;
						}
					} else if (args[2].equalsIgnoreCase("dropmainhand")) {
						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						} else if (DogTags.isOnline(args[3])) {
							Player other = Bukkit.getPlayer(args[3]);

							ItemStack item = other.getInventory().getItemInMainHand();
							if (item == null || item.getType().isAir()) {
								sender.sendMessage(AwesomeText.colorize("&b" + other.getName() + " &3has nothing in their Main hand."));
							} else {
								other.getWorld().dropItem(other.getLocation(), item);
								other.getInventory().setItemInMainHand(null);

								sender.sendMessage(AwesomeText.colorize("&b" + other.getName() + " &3has dropped the item in their Main hand."));
							}
						} else {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_found", sender)));
							return true;
						}
					} else if (args[2].equalsIgnoreCase("dropoffhand")) {
						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						} else if (DogTags.isOnline(args[3])) {
							Player other = Bukkit.getPlayer(args[3]);

							ItemStack item = other.getInventory().getItemInOffHand();
							if (item == null || item.getType().isAir()) {
								sender.sendMessage(AwesomeText.colorize("&b" + other.getName() + " &3has nothing in their off hand."));
							} else {
								other.getWorld().dropItem(other.getLocation(), item);
								other.getInventory().setItemInOffHand(null);

								sender.sendMessage(AwesomeText.colorize("&b" + other.getName() + " &3has dropped the item in their off hand."));
							}
						} else {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_found", sender)));
							return true;
						}
					} else if (args[2].equalsIgnoreCase("dropoffhand")) {
						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						} else if (DogTags.isOnline(args[3])) {
							Player other = Bukkit.getPlayer(args[3]);

							ItemStack item = other.getInventory().getItemInOffHand();
							if (item == null || item.getType().isAir()) {
								sender.sendMessage(AwesomeText.colorize("&b" + other.getName() + " &3has nothing in their off hand."));
							} else {
								other.getWorld().dropItem(other.getLocation(), item);
								other.getInventory().setItemInOffHand(null);

								sender.sendMessage(AwesomeText.colorize("&b" + other.getName() + " &3has dropped the item in their off hand."));
							}
						} else {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_found", sender)));
							return true;
						}
					}
				} else if (args[1].equalsIgnoreCase("server")) {
					if (args.length < 3) {
						sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
						return true;
					} else if (args[2].equalsIgnoreCase("address")) {
						sender.sendMessage(AwesomeText.colorize("&3Server address is &b" + Bukkit.getIp() + ":" + Bukkit.getPort()));
						return true;
					} else if (args[2].equalsIgnoreCase("shutdown")) {
						sender.sendMessage(AwesomeText.colorize("&3Server is shutting down."));
						Bukkit.shutdown();
						return true;
					} else if (args[2].equalsIgnoreCase("restart")) {
						sender.sendMessage(AwesomeText.colorize("&3Server is restarting."));
						Bukkit.getServer().spigot().restart();
						return true;
					} else if (args[2].equalsIgnoreCase("version")) {
						sender.sendMessage(AwesomeText.colorize("&3Server version is &b" + Bukkit.getVersion()));
						sender.sendMessage(AwesomeText.colorize("&3Bukkit version is &b" + Bukkit.getBukkitVersion()));
						return true;
					} else if (args[2].equalsIgnoreCase("lagmonitor")) {
						if (!(sender instanceof Player)) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
							return true;
						}
						Player player = (Player) sender;

						if (args.length < 4) {
							lagmonitor.put(player, !lagmonitor.get(player));

							if (lagmonitor.get(player)) {
								sender.sendMessage(AwesomeText.colorize("&a&l» &7Lag monitor has been toggled on."));
							} else {
								sender.sendMessage(AwesomeText.colorize("&a&l» &7Lag monitor has been toggled off."));
							}
							return true;
						} else if (args[3].equalsIgnoreCase("on")) {
							lagmonitor.put(player, true);

							sender.sendMessage(AwesomeText.colorize("&a&l» &7Lag monitor has been turned on."));
							return true;
						} else if (args[3].equalsIgnoreCase("off")) {
							lagmonitor.put(player, false);

							sender.sendMessage(AwesomeText.colorize("&a&l» &7Lag monitor has been turned off."));
							return true;
						} else {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						}
					} else {
						sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
						return true;
					}
				} else if (args[1].equalsIgnoreCase("chunk")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
						return true;
					}

					Player player = (Player) sender;
					World world = player.getLocation().getWorld();
					Chunk chunk = player.getLocation().getBlock().getChunk();

					if (args.length < 3) {
						sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
						return true;
					} else if (args[2].equalsIgnoreCase("unload")) {
						chunk.unload(true);
						sender.sendMessage(AwesomeText.colorize("&3Chunk has been unloaded."));
						return true;
					} else if (args[2].equalsIgnoreCase("isslime")) {
						if (chunk.isSlimeChunk()) {
							sender.sendMessage(AwesomeText.colorize("&3Chunk is a slime chunk."));
						} else {
							sender.sendMessage(AwesomeText.colorize("&3Chunk is a not slime chunk."));
						}
						return true;
					} else if (args[2].equalsIgnoreCase("regenerate")) {
						try {
							world.regenerateChunk(chunk.getX(), chunk.getZ());

							sender.sendMessage(AwesomeText.colorize("&3Chunk has been regenerated."));
							return true;
						} catch (UnsupportedOperationException e) {
//								sender.sendMessage(AwesomeText.colorize("&cNot supported in this Minecraft version!"));
//								return true;

							String tempworldname = "_temp-" + world.getName();
							World tempworld;
							if (Bukkit.getWorld(tempworldname) != null) {
								tempworld = Bukkit.getWorld(tempworldname);
							} else {
								WorldCreator c = new WorldCreator(tempworldname);
								c.seed(world.getSeed());
								tempworld = c.createWorld();
							}
							Chunk tempchunk = tempworld.getChunkAt(chunk.getX(), chunk.getZ());

							for (Block block : Caboodle.getChunkBlocks(tempchunk)) {
								Block oldblock = world.getBlockAt(block.getX(), block.getY(), block.getZ());
								BlockState oldstate = oldblock.getState();

								// TODO: clone nbt tags

								oldstate.setType(block.getType());
								oldstate.setBlockData(block.getBlockData());
								oldstate.update(true);
							}

							tempworlds.add(tempworldname);

							sender.sendMessage(AwesomeText.colorize("&3Chunk has been regenerated."));
							return true;
						}
					} else if (args[2].equalsIgnoreCase("unloadall")) {
						ArrayList<Chunk> chunks = new ArrayList<>();

						for (Chunk c : world.getLoadedChunks()) {
							boolean result = c.unload(true);
							if (result) {
								chunks.add(c);
							}
						}
						sender.sendMessage(AwesomeText.colorize("&b" + chunks.size() + " &3chunks have been unloaded."));
						return true;
					} else {
						sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
						return true;
					}
				} else if (args[1].equalsIgnoreCase("world")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
						return true;
					}

					Player player = (Player) sender;
					World world = player.getLocation().getWorld();

					if (args.length < 3) {
						sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
						return true;
					}

					if (args.length >= 4 && !(args[2].equalsIgnoreCase("load") || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("delete"))) { // Get optional world argument
						world = Bukkit.getWorld(args[3]);

						if (world == null) {
							sender.sendMessage(AwesomeText.colorize("&cError: &7That world could not be found."));
							return true;
						}
					}

					if (args[2].equalsIgnoreCase("unload")) {
						boolean result = Bukkit.unloadWorld(world, true);

						if (result) {
							sender.sendMessage(AwesomeText.colorize("&f" + world.getName() + " &7has been unloaded."));
						} else {
							sender.sendMessage(AwesomeText.colorize("&f" + world.getName() + " &7could not be unloaded."));
						}
						return true;
					} else if (args[2].equalsIgnoreCase("load")) {
						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						}

						World w = Bukkit.createWorld(new WorldCreator(args[3]));
						if (w == null) {
							sender.sendMessage(AwesomeText.colorize("&cError: &7Failed to load &f" + w.getName()));
						} else {
							sender.sendMessage(AwesomeText.colorize("&a&l» &f" + w.getName() + " &7has been loaded."));
						}
						return true;
					} else if (args[2].equalsIgnoreCase("save")) {
						world.save();
						sender.sendMessage(AwesomeText.colorize("&a&l» &f" + world.getName() + " &7has been saved."));
						return true;
					} else if (args[2].equalsIgnoreCase("backup")) {
						world.save();

						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
						LocalDateTime date = LocalDateTime.now();
						String savename = "./backups/" + world.getName() + "_" + date.format(formatter);
						Caboodle.copyWorld(world.getWorldFolder(), new File(savename));

						sender.sendMessage(AwesomeText.colorize("&a&l» &7Backup saved at &f" + savename));
						return true;
					} else if (args[2].equalsIgnoreCase("enablepvp")) {
						world.setPVP(true);
						sender.sendMessage(AwesomeText.colorize("&a&l» &7PVP has been enabled in &f" + world.getName()));
						return true;
					} else if (args[2].equalsIgnoreCase("disablepvp")) {
						world.setPVP(false);
						sender.sendMessage(AwesomeText.colorize("&a&l» &7PVP has been disabled in &f" + world.getName()));
						return true;
					} else if (args[2].equalsIgnoreCase("list")) {
						sender.sendMessage(AwesomeText.colorize("&a&l» &7World list:"));

						for (World w : Bukkit.getWorlds()) {
							String stringType = "";

							if (w.getEnvironment().equals(Environment.NORMAL)) {
								stringType = "&a" + w.getEnvironment().toString();
							} else if (w.getEnvironment().equals(Environment.NETHER)) {
								stringType = "&c" + w.getEnvironment().toString();
							} else if (w.getEnvironment().equals(Environment.THE_END)) {
								stringType = "&e" + w.getEnvironment().toString();
							} else {
								stringType = "&d" + w.getEnvironment().toString();
							}

							sender.sendMessage(AwesomeText.colorize("&f" + w.getName() + " - " + stringType));
						}
						return true;
					} else if (args[2].equalsIgnoreCase("delete")) {
						if (args.length < 4) {
							sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
							return true;
						}

						if (!Caboodle.getAllWorldNames().contains(args[3])) {
							sender.sendMessage(AwesomeText.colorize("&cError: &7That world could not be found."));
							return true;
						} else if (Caboodle.getUnloadedWorldNames().contains(args[3])) {
							Caboodle.deleteWorld(args[3]);
							sender.sendMessage(AwesomeText.colorize("&f" + args[3] + " &7has been deleted."));
							return true;
						} else {
							sender.sendMessage(AwesomeText.colorize("&cError: &7The world has to be unloaded first"));
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
		}

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return null; // Cancel if sender isn't a player

		if (
			(
				label.equalsIgnoreCase("cuberry") ||
				label.equalsIgnoreCase("cubefruit") ||
				label.equalsIgnoreCase("fruit") ||
				label.equalsIgnoreCase("cf") ||
				label.equalsIgnoreCase("cb")
			) &&
			Caboodle.hasPermission(sender, "admin")
		) {
			if (args.length == 1) {
				return Arrays.asList("reload", "debug", "test");
			}
			if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
				return Arrays.asList("server", "world", "chunk", "player");
			}
			if (args.length == 3 && args[0].equalsIgnoreCase("debug")) {
				if (args[1].equalsIgnoreCase("server")) {
					return Arrays.asList("address", "shutdown", "restart", "version", "lagmonitor");
				}
				if (args[1].equalsIgnoreCase("world")) {
					return Arrays.asList("save", "unload", "load", "backup", "enablepvp", "disablepvp", "list", "delete");
				}
				if (args[1].equalsIgnoreCase("chunk")) {
					return Arrays.asList("unload", "isslime", "regenerate", "unloadall");
				}
				if (args[1].equalsIgnoreCase("player")) {
					return Arrays.asList("sethunger", "sethealth", "hide", "show", "dupeinventory", "dupeenderchest", "setarrowsinbody", "ride", "swap", "dropmainhand", "dropoffhand");
				}
			}
			if (args.length == 4 && args[0].equals("debug")) {
				if (args[1].equalsIgnoreCase("world") && !(args[2].equalsIgnoreCase("load") || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("delete"))) {
					return Caboodle.getWorldNames();
				}
				if (args[1].equalsIgnoreCase("world") && args[2].equalsIgnoreCase("load") || args[2].equalsIgnoreCase("delete")) {
					if (Caboodle.getUnloadedWorldNames().size() == 0) {
						return Caboodle.getAllWorldNames();
					} else {
						return Caboodle.getUnloadedWorldNames();
					}
				}
				if (args[2].equalsIgnoreCase("lagmonitor")) {
					return Arrays.asList("on", "off");
				}
			}

//			return Collections.emptyList();
		}

		return null;
	}

	public static void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Paper.plugin(), new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (lagmonitor.get(player) == null) {
						lagmonitor.put(player, false);
					}

					if (lagmonitor.get(player)) {
						String tpsString;
						double tps = Caboodle.round(Caboodle.getTps(), 2);
						if (tps >= 18) {
							tpsString = "&7TPS: &a" + tps;
						} else if (tps >= 16) {
							tpsString = "&7TPS: &e" + tps;
						} else {
							tpsString = "&7TPS: &c" + tps;
						}

						Runtime r = Runtime.getRuntime();
						long memUsed = (r.totalMemory() - r.freeMemory()) / 1048576;
						long memMax = r.maxMemory() / 1048576;
						double memValue = ((double) memUsed / (double) memMax) * 100;
						String memString = "&7Memory&e: &a" + Caboodle.round(memValue, 1) + "%";

						String chunkString = "&7Chunks&e: &a" + Caboodle.getTotalChunksLoaded();

						String entityString = "&7Entities&e: &a" + Caboodle.getTotalEntities();

						String pingString;
						int ping = player.getPing();
						if (ping < 150) {
							pingString = "&7Ping&e: &a&#00FF21" + ping + "&7ms";
						} else if (ping < 300) {
							pingString = "&7Ping&e: &e&#F3FF00" + ping + "&7ms";
						} else if (ping < 600) {
							pingString = "&7Ping&e: &6&#FFA500" + ping + "&7ms";
						} else if (ping < 1000) {
							pingString = "&7Ping&e: &c&#FF4300" + ping + "&7ms";
						} else {
							pingString = "&7Ping&e: &4&#910003" + ping + "&7ms";
						}

						Caboodle.sendActionbar(player, AwesomeText.prettifyMessage(tpsString + " " + memString + " " + chunkString + " " + entityString + " " + pingString, player));
					}
				}
			}
		}, 0, 1);
	}

	public static void end() {
		for (String worldname : tempworlds) {
			World world = Bukkit.getWorld(worldname);

			if (world != null) {
				Bukkit.unloadWorld(worldname, true);
				Caboodle.deleteWorld(worldname);
			}
		}
	}

}
