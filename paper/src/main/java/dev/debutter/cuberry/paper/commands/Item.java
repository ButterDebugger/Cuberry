package dev.debutter.cuberry.paper.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.debutter.cuberry.paper.PaperCuberry;
import dev.debutter.cuberry.paper.commands.arguments.PlayerArmorSlot;
import dev.debutter.cuberry.paper.commands.arguments.PlayerArmorSlotArgument;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Item {

	// TODO: hide flags, custom player head, attribute, CanPlaceOn, CanDestroy, item model, item model data

    public static LiteralCommandNode<CommandSourceStack> command = Commands.literal("itemstack")
        .requires(sender -> sender.getSender().hasPermission("cuberry.itemstack"))
        .then(Commands.literal("get")
            .then(Commands.argument("item", ArgumentTypes.itemStack())
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    Entity executor = ctx.getSource().getExecutor();

                    // Cancel if the executor is not a player
                    if (!(executor instanceof Player player)) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                        return SINGLE_SUCCESS;
                    }

                    // Get the item argument
                    ItemStack item = ctx.getArgument("item", ItemStack.class);

                    // Give the item to the player
                    Caboodle.giveItem(player, item);

                    sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                        "commands.itemstack.recieve_item",
                        Placeholder.unparsed("amount", String.valueOf(1)),
                        Placeholder.component("item", AwesomeText.createItemHoverComponent(item))
                    ));
                    return SINGLE_SUCCESS;
                })
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        Entity executor = ctx.getSource().getExecutor();

                        // Cancel if the executor is not a player
                        if (!(executor instanceof Player player)) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                            return SINGLE_SUCCESS;
                        }

                        // Get the arguments
                        ItemStack item = ctx.getArgument("item", ItemStack.class);
                        int amount = ctx.getArgument("amount", Integer.class);

                        // Give the item to the player
                        Caboodle.giveItem(player, item, amount);

                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                            "commands.itemstack.recieve_item",
                            Placeholder.unparsed("amount", String.valueOf(amount)),
                            Placeholder.component("item", AwesomeText.createItemHoverComponent(item))
                        ));
                        return SINGLE_SUCCESS;
                    })
                )
            )
        )
        .then(Commands.literal("count")
            .then(Commands.argument("amount", IntegerArgumentType.integer(0, 99))
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    Entity executor = ctx.getSource().getExecutor();

                    // Cancel if the executor is not a player
                    if (!(executor instanceof Player player)) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                        return SINGLE_SUCCESS;
                    }

                    // Cancel if the player is not holding an item
                    if (player.getInventory().getItemInMainHand().isEmpty()) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.missing_item_in_main_hand"));
                        return SINGLE_SUCCESS;
                    }

                    // Get the item and arguments
                    ItemStack item = player.getInventory().getItemInMainHand();
                    int amount = ctx.getArgument("amount", Integer.class);

                    // Set the amount of the item
                    item.setAmount(amount);

                    sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                        "commands.itemstack.set_item_count",
                        Placeholder.unparsed("amount", String.valueOf(amount)),
                        Placeholder.component("item", AwesomeText.createItemHoverComponent(item))
                    ));
                    return SINGLE_SUCCESS;
                })
            )
        )
        .then(Commands.literal("material")
            .then(Commands.argument("type", ArgumentTypes.resource(RegistryKey.ITEM))
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    Entity executor = ctx.getSource().getExecutor();

                    // Cancel if the executor is not a player
                    if (!(executor instanceof Player player)) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                        return SINGLE_SUCCESS;
                    }

                    // Cancel if the player is not holding an item
                    if (player.getInventory().getItemInMainHand().isEmpty()) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.missing_item_in_main_hand"));
                        return SINGLE_SUCCESS;
                    }

                    // Get the item and arguments
                    ItemStack item = player.getInventory().getItemInMainHand();
                    ItemType itemType = ctx.getArgument("type", ItemType.class);
                    Material material = itemType.createItemStack().getType();

                    // Change the item type
                    Component unmodifiedItemComponent = AwesomeText.createItemHoverComponent(item);

                    player.getInventory().setItemInMainHand(item.withType(material));

                    sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                        "commands.itemstack.set_item_material",
                        Placeholder.component("item", unmodifiedItemComponent),
                        Placeholder.component("material", AwesomeText.createMaterialHoverComponent(material))
                    ));
                    return SINGLE_SUCCESS;
                })
            )
        )
        .then(Commands.literal("unbreakable")
            .executes(ctx -> {
                CommandSender sender = ctx.getSource().getSender();
                Entity executor = ctx.getSource().getExecutor();

                // Cancel if the executor is not a player
                if (!(executor instanceof Player player)) {
                    sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                    return SINGLE_SUCCESS;
                }

                // Cancel if the player is not holding an item
                if (player.getInventory().getItemInMainHand().isEmpty()) {
                    sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.missing_item_in_main_hand"));
                    return SINGLE_SUCCESS;
                }

                // Get the item and arguments
                ItemStack item = player.getInventory().getItemInMainHand();
                ItemMeta itemMeta = item.getItemMeta();

                Component unmodifiedItemComponent = AwesomeText.createItemHoverComponent(item);

                if (itemMeta.isUnbreakable()) {
                    // Make item breakable
                    itemMeta.setUnbreakable(false);
                    item.setItemMeta(itemMeta);

                    sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                        "commands.itemstack.set_item_breakable",
                        Placeholder.component("item", unmodifiedItemComponent)
                    ));
                } else {
                    // Make item unbreakable
                    itemMeta.setUnbreakable(true);
                    item.setItemMeta(itemMeta);

                    sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                        "commands.itemstack.set_item_unbreakable",
                        Placeholder.component("item", unmodifiedItemComponent)
                    ));
                }

                return SINGLE_SUCCESS;
            })
            .then(Commands.argument("enabled", BoolArgumentType.bool())
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    Entity executor = ctx.getSource().getExecutor();

                    // Cancel if the executor is not a player
                    if (!(executor instanceof Player player)) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                        return SINGLE_SUCCESS;
                    }

                    // Cancel if the player is not holding an item
                    if (player.getInventory().getItemInMainHand().isEmpty()) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.missing_item_in_main_hand"));
                        return SINGLE_SUCCESS;
                    }

                    // Get the item and arguments
                    ItemStack item = player.getInventory().getItemInMainHand();
                    ItemMeta itemMeta = item.getItemMeta();
                    boolean enabled = ctx.getArgument("enabled", Boolean.class);

                    Component unmodifiedItemComponent = AwesomeText.createItemHoverComponent(item);

                    if (enabled) {
                        // Make item unbreakable
                        itemMeta.setUnbreakable(true);
                        item.setItemMeta(itemMeta);

                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                            "commands.itemstack.set_item_unbreakable",
                            Placeholder.component("item", unmodifiedItemComponent)
                        ));
                    } else {
                        // Make item breakable
                        itemMeta.setUnbreakable(false);
                        item.setItemMeta(itemMeta);

                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                            "commands.itemstack.set_item_breakable",
                            Placeholder.component("item", unmodifiedItemComponent)
                        ));
                    }

                    return SINGLE_SUCCESS;
                })
            )
        )
        .then(Commands.literal("rename")
            .then(Commands.argument("name", StringArgumentType.greedyString()	)
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    Entity executor = ctx.getSource().getExecutor();

                    // Cancel if the executor is not a player
                    if (!(executor instanceof Player player)) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                        return SINGLE_SUCCESS;
                    }

                    // Cancel if the player is not holding an item
                    if (player.getInventory().getItemInMainHand().isEmpty()) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.missing_item_in_main_hand"));
                        return SINGLE_SUCCESS;
                    }

                    // Get the item and arguments
                    ItemStack item = player.getInventory().getItemInMainHand();
                    String name = ctx.getArgument("name", String.class);

                    Component nameComponent = AwesomeText.beautifyMessage(name);

                    // Set the item's display name
                    Component unmodifiedItemComponent = AwesomeText.createItemHoverComponent(item);

                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.displayName(nameComponent);
                    item.setItemMeta(itemMeta);

                    sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                        "commands.itemstack.set_item_display_name",
                        Placeholder.component("item_name", nameComponent),
                        Placeholder.component("item", unmodifiedItemComponent)
                    ));
                    return SINGLE_SUCCESS;
                })
            )
        )
        .then(Commands.literal("durability")
            .then(Commands.literal("remaining")
                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                    .executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        Entity executor = ctx.getSource().getExecutor();

                        // Cancel if the executor is not a player
                        if (!(executor instanceof Player player)) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                            return SINGLE_SUCCESS;
                        }

                        // Cancel if the player is not holding an item
                        if (player.getInventory().getItemInMainHand().isEmpty()) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.missing_item_in_main_hand"));
                            return SINGLE_SUCCESS;
                        }

                        // Get the item and arguments
                        ItemStack item = player.getInventory().getItemInMainHand();
                        int amount = ctx.getArgument("amount", Integer.class);
                        ItemMeta itemmeta = item.getItemMeta();

                        Component unmodifiedItemComponent = AwesomeText.createItemHoverComponent(item);

                        // Cancel if the item is not damageable
                        if (!(itemmeta instanceof Damageable damageable)) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                                "commands.itemstack.item_not_damageable",
                                Placeholder.component("item", unmodifiedItemComponent)
                            ));
                            return SINGLE_SUCCESS;
                        }

                        int maxDamage = damageable.hasMaxDamage() ? damageable.getMaxDamage() : item.getType().getMaxDurability();

                        if (maxDamage <= 0) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                                "commands.itemstack.item_not_damageable",
                                Placeholder.component("item", unmodifiedItemComponent)
                            ));
                            return SINGLE_SUCCESS;
                        }

                        // Calculate and set the damage
                        int clampedAmount = Math.min(maxDamage, Math.max(0, maxDamage - amount));

                        damageable.setDamage(clampedAmount);
                        item.setItemMeta(itemmeta);

                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                            "commands.itemstack.set_item_durability",
                            Placeholder.unparsed("damage", String.valueOf(maxDamage - clampedAmount)),
                            Placeholder.unparsed("max_damage", String.valueOf(maxDamage)),
                            Placeholder.component("item", unmodifiedItemComponent)
                        ));
                        return SINGLE_SUCCESS;
                    })
                )
            )
            .then(Commands.literal("percentage")
                .then(Commands.argument("percent", DoubleArgumentType.doubleArg(0.0d, 100.0d))
                    .executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        Entity executor = ctx.getSource().getExecutor();

                        // Cancel if the executor is not a player
                        if (!(executor instanceof Player player)) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                            return SINGLE_SUCCESS;
                        }

                        // Cancel if the player is not holding an item
                        if (player.getInventory().getItemInMainHand().isEmpty()) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.missing_item_in_main_hand"));
                            return SINGLE_SUCCESS;
                        }

                        // Get the item and arguments
                        ItemStack item = player.getInventory().getItemInMainHand();
                        double percent = ctx.getArgument("percent", Double.class);
                        ItemMeta itemmeta = item.getItemMeta();

                        Component unmodifiedItemComponent = AwesomeText.createItemHoverComponent(item);

                        // Cancel if the item is not damageable
                        if (!(itemmeta instanceof Damageable damageable)) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                                "commands.itemstack.item_not_damageable",
                                Placeholder.component("item", unmodifiedItemComponent)
                            ));
                            return SINGLE_SUCCESS;
                        }

                        int maxDamage = damageable.hasMaxDamage() ? damageable.getMaxDamage() : item.getType().getMaxDurability();

                        if (maxDamage <= 0) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                                "commands.itemstack.item_not_damageable",
                                Placeholder.component("item", unmodifiedItemComponent)
                            ));
                            return SINGLE_SUCCESS;
                        }

                        // Calculate and set the damage
                        int clampedAmount = (int) Math.min(maxDamage, Math.max(0.0d, Math.floor(maxDamage - maxDamage * percent / 100.0d)));

                        damageable.setDamage(clampedAmount);
                        item.setItemMeta(itemmeta);

                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                            "commands.itemstack.set_item_durability",
                            Placeholder.unparsed("damage", String.valueOf(maxDamage - clampedAmount)),
                            Placeholder.unparsed("max_damage", String.valueOf(maxDamage)),
                            Placeholder.component("item", unmodifiedItemComponent)
                        ));
                        return SINGLE_SUCCESS;
                    })
                )
            )
            .then(Commands.literal("damage")
                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                    .executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        Entity executor = ctx.getSource().getExecutor();

                        // Cancel if the executor is not a player
                        if (!(executor instanceof Player player)) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                            return SINGLE_SUCCESS;
                        }

                        // Cancel if the player is not holding an item
                        if (player.getInventory().getItemInMainHand().isEmpty()) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.missing_item_in_main_hand"));
                            return SINGLE_SUCCESS;
                        }

                        // Get the item and arguments
                        ItemStack item = player.getInventory().getItemInMainHand();
                        int amount = ctx.getArgument("amount", Integer.class);
                        ItemMeta itemmeta = item.getItemMeta();

                        Component unmodifiedItemComponent = AwesomeText.createItemHoverComponent(item);

                        // Cancel if the item is not damageable
                        if (!(itemmeta instanceof Damageable damageable)) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                                "commands.itemstack.item_not_damageable",
                                Placeholder.component("item", unmodifiedItemComponent)
                            ));
                            return SINGLE_SUCCESS;
                        }

                        int maxDamage = damageable.hasMaxDamage() ? damageable.getMaxDamage() : item.getType().getMaxDurability();

                        if (maxDamage <= 0) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                                "commands.itemstack.item_not_damageable",
                                Placeholder.component("item", unmodifiedItemComponent)
                            ));
                            return SINGLE_SUCCESS;
                        }

                        // Calculate and set the damage
                        int clampedAmount = Math.min(maxDamage, Math.max(0, amount));

                        damageable.setDamage(clampedAmount);
                        item.setItemMeta(itemmeta);

                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                            "commands.itemstack.set_item_durability",
                            Placeholder.unparsed("damage", String.valueOf(maxDamage - clampedAmount)),
                            Placeholder.unparsed("max_damage", String.valueOf(maxDamage)),
                            Placeholder.component("item", unmodifiedItemComponent)
                        ));
                        return SINGLE_SUCCESS;
                    })
                )
            )
        )
        .then(Commands.literal("delete")
            .executes(ctx -> {
                CommandSender sender = ctx.getSource().getSender();
                Entity executor = ctx.getSource().getExecutor();

                // Cancel if the executor is not a player
                if (!(executor instanceof Player player)) {
                    sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                    return SINGLE_SUCCESS;
                }

                // Cancel if the player is not holding an item
                if (player.getInventory().getItemInMainHand().isEmpty()) {
                    sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.missing_item_in_main_hand"));
                    return SINGLE_SUCCESS;
                }

                // Remove the item in the player's main hand
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

                sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.delete_item"));
                return SINGLE_SUCCESS;
            })
        )
        .then(Commands.literal("enchant")
            .then(Commands.argument("enchantment", ArgumentTypes.resource(RegistryKey.ENCHANTMENT))
                .then(Commands.argument("level", IntegerArgumentType.integer(0, 2567))
                    .executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        Entity executor = ctx.getSource().getExecutor();

                        // Cancel if the executor is not a player
                        if (!(executor instanceof Player player)) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                            return SINGLE_SUCCESS;
                        }

                        // Cancel if the player is not holding an item
                        if (player.getInventory().getItemInMainHand().isEmpty()) {
                            sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.missing_item_in_main_hand"));
                            return SINGLE_SUCCESS;
                        }

                        // Get the item and arguments
                        ItemStack item = player.getInventory().getItemInMainHand();
                        Enchantment enchantment = ctx.getArgument("enchantment", Enchantment.class);
                        int level = ctx.getArgument("level", Integer.class);

                        // Add the enchantment
                        Component unmodifiedItemComponent = AwesomeText.createItemHoverComponent(item);

                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.addEnchant(enchantment, level, true);
                        item.setItemMeta(itemMeta);

                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                            "commands.itemstack.add_item_enchantment",
                            Placeholder.component("enchantment", enchantment.description()),
                            Placeholder.component("item", unmodifiedItemComponent),
                            Placeholder.unparsed("level", AwesomeText.romanNumeral(level))
                        ));
                        return SINGLE_SUCCESS;
                    })
                )
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    Entity executor = ctx.getSource().getExecutor();

                    // Cancel if the executor is not a player
                    if (!(executor instanceof Player player)) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                        return SINGLE_SUCCESS;
                    }

                    // Cancel if the player is not holding an item
                    if (player.getInventory().getItemInMainHand().isEmpty()) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.missing_item_in_main_hand"));
                        return SINGLE_SUCCESS;
                    }

                    // Get the item and arguments
                    ItemStack item = player.getInventory().getItemInMainHand();
                    Enchantment enchantment = ctx.getArgument("enchantment", Enchantment.class);
                    int level = 1;

                    // Add the enchantment
                    Component unmodifiedItemComponent = AwesomeText.createItemHoverComponent(item);

                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.addEnchant(enchantment, level, true);
                    item.setItemMeta(itemMeta);

                    sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(
                        "commands.itemstack.add_item_enchantment",
                        Placeholder.component("enchantment", enchantment.description()),
                        Placeholder.component("item", unmodifiedItemComponent),
                        Placeholder.unparsed("level", AwesomeText.romanNumeral(level))
                    ));
                    return SINGLE_SUCCESS;
                })
            )
        )
        .then(Commands.literal("equip")
            .then(Commands.argument("slot", new PlayerArmorSlotArgument())
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    Entity executor = ctx.getSource().getExecutor();

                    // Cancel if the executor is not a player
                    if (!(executor instanceof Player player)) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.player_required"));
                        return SINGLE_SUCCESS;
                    }

                    // Cancel if the player is not holding an item
                    if (player.getInventory().getItemInMainHand().isEmpty()) {
                        sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage("commands.itemstack.missing_item_in_main_hand"));
                        return SINGLE_SUCCESS;
                    }

                    // Get the item and arguments
                    ItemStack item = player.getInventory().getItemInMainHand();
                    PlayerArmorSlot slot = ctx.getArgument("slot", PlayerArmorSlot.class);
                    EquipmentSlot equipmentSlot = slot.getEquipmentSlot();

                    // Equip the item and store the old one
                    ItemStack oldItem = player.getInventory().getItem(equipmentSlot);

                    player.getInventory().setItem(equipmentSlot, item);

                    // Send the correct message
                    sender.sendMessage(PaperCuberry.locale().getBeautifiedMessage(switch (slot) {
                        case HEAD -> "commands.itemstack.equip_item_on_head";
                        case CHEST -> "commands.itemstack.equip_item_on_chest";
                        case LEGS -> "commands.itemstack.equip_item_on_legs";
                        case FEET -> "commands.itemstack.equip_item_on_feet";
                    },
                        Placeholder.component("item", AwesomeText.createItemHoverComponent(item))
                    ));

                    // Set the main hand item to the old equipment item
                    player.getInventory().setItemInMainHand(oldItem);
                    return SINGLE_SUCCESS;
                })
            )
        )
        .build();

}
