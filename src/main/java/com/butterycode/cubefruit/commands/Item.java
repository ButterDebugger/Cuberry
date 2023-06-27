package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.dogTags;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Item extends CommandWrapper {

	FileConfiguration config = Main.plugin.config();

	// TODO: hideflags, head, attribute, CanPlaceOn, CanDestroy

	public Item() {
		CommandRegistry itemCmd = new CommandRegistry(this, "item");
		itemCmd.addAliases("i", "itemstack");
		itemCmd.setDescription("Modify the attributes of items");

		addRegistries(itemCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("item") || label.equalsIgnoreCase("i") || label.equalsIgnoreCase("itemstack")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!caboodle.hasPermission(sender, "item")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(awesomeText.prettifyMessage("&3Usage: &7/" + label + " <arguments>"));
				return true;
			}
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("get")) {
					if (args.length > 1) {
						Material material;
						int count = 1;

						if (args[1].equalsIgnoreCase("random")) {
							Random rand = new Random();

							List<String> itemlist = getListOfItems();

							material = caboodle.getMaterialByName(itemlist.get(rand.nextInt(itemlist.size())));
						} else {
							material = caboodle.getMaterialByName(args[1]);
						}

						if (material == null) {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid material."));
							return true;
						}

						ItemStack item = new ItemStack(material);

						if (args.length > 2) {
							if (!dogTags.isNumeric(args[2])) {
								sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must enter a valid number."));
								return true;
							}

							count = Integer.parseInt(args[2]);
						}

						item.setAmount(count);

						caboodle.giveItem(player, item);
						sender.sendMessage(awesomeText.colorize("&a&l» &7You have been received &f" + count + " " + awesomeText.screamingSnakeCase(material.toString()) + "&7."));
						return true;
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&3Usage: &7/" + label + " get <material> [<count>]"));
						return true;
					}
				} else if (args[0].equalsIgnoreCase("count")) {
					if (args.length > 1) {
						ItemStack item = player.getInventory().getItemInMainHand();

						if (item == null || item.getType().equals(Material.AIR)) {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
							return true;
						}

						if (!dogTags.isNumeric(args[1])) {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must enter a number."));
							return true;
						}

						int count = (int) Float.parseFloat(args[1]);
						item.setAmount(count);
						sender.sendMessage(awesomeText.colorize("&a&l» &7Set the item count of &f" + awesomeText.screamingSnakeCase(item.getType().toString()) + "&7 to &f" + count + "&7."));
						return true;
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&3Usage: &7/" + label + " count <count>"));
						return true;
					}
				} else if (args[0].equalsIgnoreCase("material")) {
					if (args.length > 1) {
						ItemStack item = player.getInventory().getItemInMainHand();

						if (item == null || item.getType().equals(Material.AIR)) {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
							return true;
						}

						Material material = caboodle.getMaterialByName(args[1]);

						if (material == null) {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7\"" + args[1] + "\" is not a valid material"));
							return true;
						}

						item.setType(material);

						sender.sendMessage(awesomeText.colorize("&a&l» &7Changed the held item's material to &f" + awesomeText.screamingSnakeCase(material.toString()) + "&7."));
						return true;
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&3Usage: &7/" + label + " material <material>"));
						return true;
					}
				} else if (args[0].equalsIgnoreCase("unbreakable")) {
					if (args.length > 1) {
						ItemStack item = player.getInventory().getItemInMainHand();

						if (item == null || item.getType().equals(Material.AIR)) {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
							return true;
						}

						if (args[1].equals("false")) {
							ItemMeta itemMeta = item.getItemMeta();
						    itemMeta.setUnbreakable(false);
						    item.setItemMeta(itemMeta);

							sender.sendMessage(awesomeText.colorize("&a&l» &7Item is no longer unbreakable"));
							return true;
						} else if (args[1].equals("true")) {
							ItemMeta itemMeta = item.getItemMeta();
						    itemMeta.setUnbreakable(true);
						    item.setItemMeta(itemMeta);

							sender.sendMessage(awesomeText.colorize("&a&l» &7Item is now unbreakable"));
							return true;
						} else {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must enter a boolean value"));
							return true;
						}
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&3Usage: &7/" + label + " unbreakable <true|false>"));
						return true;
					}
				} else if (args[0].equalsIgnoreCase("rename")) {
					ItemStack item = player.getInventory().getItemInMainHand();

					if (item == null || item.getType().equals(Material.AIR)) {
						sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
						return true;
					}

					String str = String.join(" ", caboodle.splice(args, 0, 1));
					ItemMeta itemMeta = item.getItemMeta();

					String itemname = awesomeText.colorizeHex(str);
					itemMeta.setDisplayName(itemname);

					item.setItemMeta(itemMeta);

					if (str.equals("")) {
						sender.sendMessage(awesomeText.colorize("&a&l» &7Item name has been reset."));
						return true;
					} else {
						sender.sendMessage(awesomeText.colorize("&a&l» &7Item name has been set to \"" + itemname + "&7\"."));
						return true;
					}
				} else if (args[0].equalsIgnoreCase("durability")) {
					if (args.length > 2) {
						ItemStack item = player.getInventory().getItemInMainHand();

						if (item == null || item.getType().equals(Material.AIR)) {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
							return true;
						}

						ItemMeta itemmeta = item.getItemMeta();
						Damageable damage;
						short maxdamage = item.getType().getMaxDurability();

						if (itemmeta instanceof Damageable) {
							damage = (Damageable) itemmeta;
						} else {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7" + awesomeText.screamingSnakeCase(item.getType().toString()) + " is not damageable."));
							return true;
						}

						if (args[1].equals("damage")) {
							if (!dogTags.isNumeric(args[2])) {
								sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must enter a number."));
								return true;
							}

							short number = Short.parseShort(args[2]);

							number = (short) Math.min(maxdamage, Math.max(0, number));

							damage.setDamage(number);
							item.setItemMeta(itemmeta);
							sender.sendMessage(awesomeText.colorize("&a&l» &7Item durability has been set to &f" + (maxdamage - number) + "&7/&f" + maxdamage + "&7."));
							return true;
						} else if (args[1].equals("percentage")) {
							if (!dogTags.isNumeric(args[2])) {
								sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must enter a number."));
								return true;
							}

							short number = Short.parseShort(args[2]);

							number = (short) Math.min(maxdamage, Math.max(0, Math.floor(maxdamage - maxdamage * (double) (number) / 100d)));

							damage.setDamage(number);
							item.setItemMeta(itemmeta);
							sender.sendMessage(awesomeText.colorize("&a&l» &7Item durability has been set to &f" + (maxdamage - number) + "&7/&f" + maxdamage + "&7."));
							return true;
						} else if (args[1].equals("remaining")) {
							if (!dogTags.isNumeric(args[2])) {
								sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must enter a number."));
								return true;
							}

							short number = Short.parseShort(args[2]);

							number = (short) Math.min(maxdamage, Math.max(0, maxdamage - number));

							damage.setDamage(number);
							item.setItemMeta(itemmeta);
							sender.sendMessage(awesomeText.colorize("&a&l» &7Item durability has been set to &f" + (maxdamage - number) + "&7/&f" + maxdamage + "&7."));
							return true;
						} else {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
							return true;
						}
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&3Usage: &7/" + label + " durability <damage|percentage|remaining> <number>"));
						return true;
					}
				} else if (args[0].equalsIgnoreCase("modeldata")) {
					if (args.length > 1) {
						ItemStack item = player.getInventory().getItemInMainHand();

						if (item == null || item.getType().equals(Material.AIR)) {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
							return true;
						}

						if (!dogTags.isNumeric(args[1])) {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must enter a number."));
							return true;
						}

						int number = (int) Float.parseFloat(args[1]);

						ItemMeta itemmeta = item.getItemMeta();

						itemmeta.setCustomModelData(number);

						item.setItemMeta(itemmeta);

						sender.sendMessage(awesomeText.colorize("&a&l» &7The item's custom model data has been set to &f" + number));
						return true;
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&3Usage: &7/" + label + " modeldata <number>"));
						return true;
					}
				} else if (args[0].equalsIgnoreCase("delete")) {
					ItemStack item = player.getInventory().getItemInMainHand();

					if (item == null || item.getType().equals(Material.AIR)) {
						sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
						return true;
					}

					player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

					sender.sendMessage(awesomeText.colorize("&a&l» &7The item you were holding has been removed."));
					return true;
				} else if (args[0].equalsIgnoreCase("enchant")) {
					if (args.length > 2) {
						ItemStack item = player.getInventory().getItemInMainHand();

						if (item == null || item.getType().equals(Material.AIR)) {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
							return true;
						}

						Enchantment enchantment = caboodle.getEnchantmentByName(args[1]);

						if (enchantment == null) {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7\"" + args[1] + "\" is not a valid enchantment"));
							return true;
						}

						if (!dogTags.isNumeric(args[2])) {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must enter a valid number."));
							return true;
						}

						int level = Integer.parseInt(args[2]);

						ItemMeta itemMeta = item.getItemMeta();
					    itemMeta.addEnchant(enchantment, level, true);
					    item.setItemMeta(itemMeta);

						sender.sendMessage(awesomeText.colorize("&a&l» &7Item has been enchanted with &f" + enchantment.getKey().toString() + " " + awesomeText.romanNumeral(level) + "&7."));
						return true;
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&3Usage: &7/" + label + " enchant <enchantment> <level>"));
						return true;
					}
				} else {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("item") || label.equalsIgnoreCase("i") || label.equalsIgnoreCase("itemstack") && caboodle.hasPermission(sender, "item")) {
			if (args.length == 1) {
				return Arrays.asList("count", "get", "material", "unbreakable", "rename", "durability", "modeldata", "delete", "enchant");
			}
			if (args.length == 2) {
				if (args[0].equals("get")) {
					List<String> itemlist = getListOfItems();
					itemlist.add("random");
					return itemlist;
				} else if (args[0].equals("material")) {
					return getListOfItems();
				} else if (args[0].equals("unbreakable")) {
					return Arrays.asList("true", "false");
				} else if (args[0].equals("durability")) {
					return Arrays.asList("damage", "percentage", "remaining");
				} else if (args[0].equals("enchant")) {
					return getListOfEnchantments();
				}
			}

			return Collections.emptyList();
		}

		return null;
	}

	public List<String> getListOfItems() {
		List<String> items = new ArrayList<>();

		for (Material mat : Arrays.asList(Material.values())) {
			if (mat.isItem()) items.add(mat.toString().toLowerCase());
		}

		return items;
	}

	public List<String> getListOfEnchantments() {
		List<String> items = new ArrayList<>();

		for (Enchantment ech : Enchantment.values()) {
			items.add(ech.getKey().toString());
		}

		return items;
	}
}
