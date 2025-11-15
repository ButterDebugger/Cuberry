package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.DogTags;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Item extends CommandWrapper {

	// TODO: hideflags, head, attribute, CanPlaceOn, CanDestroy

	public Item() {
		CommandRegistry itemCmd = new CommandRegistry(this, "itemstack");
		itemCmd.addAliases("i");
		itemCmd.setDescription("Modify the attributes of items");

		addRegistries(itemCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("i") || label.equalsIgnoreCase("itemstack")) {
			if (!(sender instanceof Player player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

            if (!Caboodle.hasPermission(sender, "itemstack")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/" + label + " <arguments>"));
				return true;
			}

            if (args[0].equalsIgnoreCase("get")) {
                if (args.length > 1) {
                    Material material;
                    int count = 1;

                    if (args[1].equalsIgnoreCase("random")) {
                        Random rand = new Random();

                        List<String> itemlist = getListOfItems();

                        material = Caboodle.getMaterialByName(itemlist.get(rand.nextInt(itemlist.size())));
                    } else {
                        material = Caboodle.getMaterialByName(args[1]);
                    }

                    if (material == null) {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7Invalid material."));
                        return true;
                    }

                    ItemStack item = new ItemStack(material);

                    if (args.length > 2) {
                        if (!DogTags.isNumeric(args[2])) {
                            sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must enter a valid number."));
                            return true;
                        }

                        count = Integer.parseInt(args[2]);
                    }

                    item.setAmount(count);

                    Caboodle.giveItem(player, item);
                    sender.sendMessage(AwesomeText.beautifyMessage("<green><bold>»</bold> <gray>You have received <white><count> <item><gray>.", Placeholder.unparsed("count", String.valueOf(count)), Placeholder.component("item", AwesomeText.createItemHoverComponent(item))));
                    return true;
                } else {
                    sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/" + label + " get <material> [<count>]"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("count")) {
                if (args.length > 1) {
                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (item == null || item.getType().equals(Material.AIR)) {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
                        return true;
                    }

                    if (!DogTags.isNumeric(args[1])) {
                        sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
                        return true;
                    }

                    int count = (int) Float.parseFloat(args[1]);
                    item.setAmount(count);
					sender.sendMessage(AwesomeText.beautifyMessage("<green><bold>»</bold> <gray>Set the item count of <item> to <white><count><gray>.", Placeholder.unparsed("count", String.valueOf(count)), Placeholder.component("item", AwesomeText.createItemHoverComponent(item))));
                    return true;
                } else {
                    sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/" + label + " count <count>"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("material")) {
                if (args.length > 1) {
                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (item == null || item.getType().equals(Material.AIR)) {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
                        return true;
                    }

                    Material material = Caboodle.getMaterialByName(args[1]);

                    if (material == null) {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7\"" + args[1] + "\" is not a valid material"));
                        return true;
                    }

                    Component unmodifiedComponent = AwesomeText.createItemHoverComponent(item);
                    item.setType(material);

					sender.sendMessage(AwesomeText.beautifyMessage("<green><bold>»</bold> <gray>Changed item material of <item> to <material>.", Placeholder.component("item", unmodifiedComponent), Placeholder.component("material", AwesomeText.createMaterialHoverComponent(material))));
                    return true;
                } else {
                    sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/" + label + " material <material>"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("unbreakable")) {
                if (args.length > 1) {
                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (item == null || item.getType().equals(Material.AIR)) {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("false")) {
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setUnbreakable(false);
                        item.setItemMeta(itemMeta);

                        sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Item is no longer unbreakable"));
                        return true;
                    } else if (args[1].equalsIgnoreCase("true")) {
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setUnbreakable(true);
                        item.setItemMeta(itemMeta);

                        sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Item is now unbreakable"));
                        return true;
                    } else {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must enter a boolean value"));
                        return true;
                    }
                } else {
                    sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/" + label + " unbreakable <true|false>"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("rename")) {
                ItemStack item = player.getInventory().getItemInMainHand();

                if (item == null || item.getType().equals(Material.AIR)) {
                    sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
                    return true;
                }

                String str = String.join(" ", Caboodle.splice(args, 0, 1));
                ItemMeta itemMeta = item.getItemMeta();

                Component itemName = AwesomeText.beautifyMessage(str);
                itemMeta.displayName(itemName);

                item.setItemMeta(itemMeta);

                if (str.isEmpty()) {
                    sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Item name has been reset."));
                    return true;
                } else {
                    sender.sendMessage(AwesomeText.beautifyMessage("<green><bold>»</bold> <gray>Item name has been set to <white>\"<reset><item_name><white>\"<gray>.", Placeholder.component("item_name", itemName)));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("durability")) {
                if (args.length > 2) {
                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (item == null || item.getType().equals(Material.AIR)) {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
                        return true;
                    }

                    ItemMeta itemmeta = item.getItemMeta();
                    Damageable damage;
                    short maxDamage = item.getType().getMaxDurability();

                    if (itemmeta instanceof Damageable) {
                        damage = (Damageable) itemmeta;
                    } else {
                        sender.sendMessage(AwesomeText.beautifyMessage("<red>Error: <gray><material> is not damageable.", Placeholder.component("material", AwesomeText.createMaterialHoverComponent(item.getType()))));
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("damage")) {
                        if (!DogTags.isNumeric(args[2])) {
                            sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
                            return true;
                        }

                        short number = Short.parseShort(args[2]);

                        number = (short) Math.min(maxDamage, Math.max(0, number));

                        damage.setDamage(number);
                        item.setItemMeta(itemmeta);
                        sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Item durability has been set to &f" + (maxDamage - number) + "&7/&f" + maxDamage + "&7."));
                        return true;
                    } else if (args[1].equalsIgnoreCase("percentage")) {
                        if (!DogTags.isNumeric(args[2])) {
                            sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
                            return true;
                        }

                        short number = Short.parseShort(args[2]);

                        number = (short) Math.min(maxDamage, Math.max(0, Math.floor(maxDamage - maxDamage * (double) (number) / 100d)));

                        damage.setDamage(number);
                        item.setItemMeta(itemmeta);
                        sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Item durability has been set to &f" + (maxDamage - number) + "&7/&f" + maxDamage + "&7."));
                        return true;
                    } else if (args[1].equalsIgnoreCase("remaining")) {
                        if (!DogTags.isNumeric(args[2])) {
                            sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
                            return true;
                        }

                        short number = Short.parseShort(args[2]);

                        number = (short) Math.min(maxDamage, Math.max(0, maxDamage - number));

                        damage.setDamage(number);
                        item.setItemMeta(itemmeta);
                        sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Item durability has been set to &f" + (maxDamage - number) + "&7/&f" + maxDamage + "&7."));
                        return true;
                    } else {
                        sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
                        return true;
                    }
                } else {
                    sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/" + label + " durability <damage|percentage|remaining> <number>"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("modeldata")) {
                if (args.length > 1) {
                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (item == null || item.getType().equals(Material.AIR)) {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
                        return true;
                    }

                    if (!DogTags.isNumeric(args[1])) {
                        sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_number", sender)));
                        return true;
                    }

                    int number = (int) Float.parseFloat(args[1]);

                    ItemMeta itemmeta = item.getItemMeta();

                    itemmeta.setCustomModelData(number);

                    item.setItemMeta(itemmeta);

                    sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7The item's custom model data has been set to &f" + number));
                    return true;
                } else {
                    sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/" + label + " modeldata <number>"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("delete")) {
                ItemStack item = player.getInventory().getItemInMainHand();

                if (item == null || item.getType().equals(Material.AIR)) {
                    sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
                    return true;
                }

                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

                sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7The item you were holding has been removed."));
                return true;
            } else if (args[0].equalsIgnoreCase("enchant")) {
                if (args.length > 2) {
                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (item == null || item.getType().equals(Material.AIR)) {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
                        return true;
                    }

                    Enchantment enchantment = Caboodle.getEnchantmentByName(args[1]);

                    if (enchantment == null) {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7\"" + args[1] + "\" is not a valid enchantment"));
                        return true;
                    }

                    if (!DogTags.isNumeric(args[2])) {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must enter a valid number."));
                        return true;
                    }

                    int level = Integer.parseInt(args[2]);

                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.addEnchant(enchantment, level, true);
                    item.setItemMeta(itemMeta);

                    sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Item has been enchanted with &f" + enchantment.getKey().toString() + " " + AwesomeText.romanNumeral(level) + "&7."));
                    return true;
                } else {
                    sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/" + label + " enchant <enchantment> <level>"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("equip")) {
                if (args.length > 1) {
                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (item == null || item.getType().equals(Material.AIR)) {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be holding an item to modify in your Main hand."));
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("head")) {
                        ItemStack oldItem = player.getInventory().getItem(EquipmentSlot.HEAD);
                        player.getInventory().setItem(EquipmentSlot.HEAD, item);
                        player.getInventory().setItemInMainHand(oldItem);

                        sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Equipped item in your head slot."));
                        return true;
                    } else if (args[1].equalsIgnoreCase("chest")) {
                        ItemStack oldItem = player.getInventory().getItem(EquipmentSlot.CHEST);
                        player.getInventory().setItem(EquipmentSlot.CHEST, item);
                        player.getInventory().setItemInMainHand(oldItem);

                        sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Equipped item in your chest slot."));
                        return true;
                    } else if (args[1].equalsIgnoreCase("legs")) {
                        ItemStack oldItem = player.getInventory().getItem(EquipmentSlot.LEGS);
                        player.getInventory().setItem(EquipmentSlot.LEGS, item);
                        player.getInventory().setItemInMainHand(oldItem);

                        sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Equipped item in your legs slot."));
                        return true;
                    } else if (args[1].equalsIgnoreCase("feet")) {
                        ItemStack oldItem = player.getInventory().getItem(EquipmentSlot.FEET);
                        player.getInventory().setItem(EquipmentSlot.FEET, item);
                        player.getInventory().setItemInMainHand(oldItem);

                        sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Equipped item in your feet slot."));
                        return true;
                    } else {
                        sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7\"" + args[1] + "\" is not a valid equipment slot."));
                        return true;
                    }
                } else {
                    sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/" + label + " equip <head|chest|legs|feet>"));
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
		if (label.equalsIgnoreCase("i") || label.equalsIgnoreCase("itemstack") && Caboodle.hasPermission(sender, "itemstack")) {
			if (args.length == 1) {
				return Arrays.asList("count", "get", "material", "unbreakable", "rename", "durability", "modeldata", "delete", "enchant", "equip");
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("get")) {
					List<String> itemlist = getListOfItems();
					itemlist.add("random");
					return itemlist;
				} else if (args[0].equalsIgnoreCase("material")) {
					return getListOfItems();
				} else if (args[0].equalsIgnoreCase("unbreakable")) {
					return Arrays.asList("true", "false");
				} else if (args[0].equalsIgnoreCase("durability")) {
					return Arrays.asList("damage", "percentage", "remaining");
				} else if (args[0].equalsIgnoreCase("enchant")) {
					return getListOfEnchantments();
				} else if (args[0].equalsIgnoreCase("equip")) {
					return Arrays.asList("head", "chest", "legs", "feet");
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
