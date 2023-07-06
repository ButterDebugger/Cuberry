package com.butterycode.cubefruit;

import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.DogTags;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class haltEvents implements Listener, CommandExecutor, TabCompleter {

	private static boolean haltEnabled(String halt, Player player, World world) {
		FileConfiguration config = Main.plugin().config();

		if (!config.getBoolean("halt." + halt + ".enabled")) { // Halt is not enabled
			return false;
		}

		if (config.get("halt." + halt + ".worlds") != null) { // World specific
			List<String> worlds = config.getStringList("halt." + halt + ".worlds");

			if (worlds.contains(world.getName().toLowerCase())) { // World is blocked
				if (player != null && config.getBoolean("halt." + halt + ".bypass")) { // Player bypass enabled
					if (caboodle.hasPermission(player, "halt.bypass")) {
						return false;
					}
				}
				return true;
			} else { // World is not blocked
				return false;
			}
		} else { // Not world specific
			if (player != null && config.getBoolean("halt." + halt + ".bypass")) { // Player bypass enabled
				if (caboodle.hasPermission(player, "halt.bypass")) {
					return false;
				}
			}
			return true;
		}
	}

	static boolean everythingFrozen = false;
	static HashMap<UUID, Boolean> playersFrozen = new HashMap<>();
	static HashMap<UUID, Boolean> worldsFrozen = new HashMap<>();

	public boolean getFreeze(Player player, World world) {
		if (everythingFrozen) {
			return true;
		}

		if (player != null) {
			if (!playersFrozen.containsKey(player.getUniqueId())) {
				playersFrozen.put(player.getUniqueId(), false);
			}

			if (playersFrozen.get(player.getUniqueId())) {
				return true;
			}
		}
		if (world != null) {
			if (!worldsFrozen.containsKey(world.getUID())) {
				worldsFrozen.put(world.getUID(), false);
			}

			if (worldsFrozen.get(world.getUID())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("freeze")) {
			if (!caboodle.hasPermission(sender, "halt.freeze")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
				return true;
			}
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("player")) {
					if (args.length > 1) {
						if (DogTags.isOnline(args[1])) {
							Player player = Bukkit.getPlayer(args[1]);
							UUID uuid = player.getUniqueId();

							if (!playersFrozen.containsKey(uuid)) {
								playersFrozen.put(uuid, false);
							}

							boolean frozen = playersFrozen.get(uuid);
							frozen = !frozen;

							playersFrozen.put(uuid, frozen);

							if (frozen) {
								sender.sendMessage(awesomeText.prettifyMessage("&7" + player.getName() + " is now frozen."));
								return true;
							} else {
								sender.sendMessage(awesomeText.prettifyMessage("&7" + player.getName() + " has been unfrozen."));
								return true;
							}
						} else {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player is not online."));
							return true;
						}
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
						return true;
					}
				} else if (args[0].equalsIgnoreCase("world")) {
					if (args.length > 1) {
						if (caboodle.getWorldNames().contains(args[1])) {
							World world = Bukkit.getWorld(args[1]);
							UUID uuid = world.getUID();

							if (!worldsFrozen.containsKey(uuid)) {
								worldsFrozen.put(uuid, false);
							}

							boolean frozen = worldsFrozen.get(uuid);
							frozen = !frozen;

							worldsFrozen.put(uuid, frozen);

							if (frozen) {
								sender.sendMessage(awesomeText.prettifyMessage("&7" + world.getName() + " is now frozen."));
								return true;
							} else {
								sender.sendMessage(awesomeText.prettifyMessage("&7" + world.getName() + " has been unfrozen."));
								return true;
							}
						} else {
							sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That world could not be found."));
							return true;
						}
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
						return true;
					}
				} else if (args[0].equalsIgnoreCase("everything")) {
					everythingFrozen = !everythingFrozen;

					if (everythingFrozen) {
						sender.sendMessage(awesomeText.prettifyMessage("&7Everything is now frozen."));
						return true;
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&7Everything has been unfrozen."));
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
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) return null; // Cancel if sender isn't a player

		if (command.getName().equalsIgnoreCase("freeze") && caboodle.hasPermission(sender, "halt.freeze")) {
			if (args.length == 1) {
				return Arrays.asList(new String[] {"player", "world", "everything"});
			}
			if (args[0].equalsIgnoreCase("player") && args.length == 2) {
				return caboodle.getOnlinePlayerNames();
			}
			if (args[0].equalsIgnoreCase("world") && args.length == 2) {
				return caboodle.getWorldNames();
			}
		}

		return null;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		World world = player.getWorld();

		if (getFreeze(player, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("interact", player, world)) {
			event.setCancelled(true);
		}

		if (haltEnabled("trample", player, world)) {
			if (action.equals(Action.PHYSICAL)) {
				Block block = event.getClickedBlock();

				if (block.getType().equals(Material.FARMLAND)) {
					event.setCancelled(true);
				}
			}
		}

		if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
			Block block = event.getClickedBlock();
			world = block.getWorld();

			if (haltEnabled("createportal", player, world)) {
				if (block.getType().equals(Material.END_PORTAL_FRAME)) {
					ItemStack item = player.getInventory().getItemInMainHand();

					if (item.getType().equals(Material.ENDER_EYE)) {
						event.setCancelled(true);

						if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.CREATIVE)) {
							EndPortalFrame frame = (EndPortalFrame) block.getBlockData();

					        if (!frame.hasEye()) {
					        	player.playSound(block.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1f, 1f);
					        }

					        frame.setEye(true);
					        block.setBlockData(frame);

					        if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					        	item.setAmount(item.getAmount() - 1);
					        }
						}
					}
				}
			}

			if (haltEnabled("takeflower", player, world)) {
				if (Tag.FLOWER_POTS.isTagged(block.getType())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		World world = block.getWorld();

		FileConfiguration config = Main.plugin().config();

		if (getFreeze(player, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("place", player, world)) {
			if (config.getBoolean("halt.place.placeablelist")) {
				String blockName = block.getType().toString().toLowerCase();

				if (!config.getStringList("halt.place.blocks").contains(blockName)) {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		Entity entity = event.getEntity();
		World world = entity.getWorld();

		if (entity instanceof Player) {
			Player player = (Player) entity;

			if (getFreeze(player, world)) {
				event.setCancelled(true);
				return;
			}

			if (haltEnabled("playerpickup", player, world)) {
				event.setCancelled(true);
			}
		} else {
			if (getFreeze(null, world)) {
				event.setCancelled(true);
				return;
			}

			if (haltEnabled("entitypickup", null, world)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();

		FileConfiguration config = Main.plugin().config();

		if (getFreeze(player, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("drop", player, world)) {
			if (config.getInt("halt.drop.dropvelocity") == -1) { // Cancel event normally
				event.setCancelled(true);
			} else if (player.getVelocity().getX() > config.getInt("halt.dropvelocity")) {
				event.setCancelled(true);
			} else if (player.getVelocity().getY() > config.getInt("halt.dropvelocity")) {
				event.setCancelled(true);
			} else if (player.getVelocity().getZ() > config.getInt("halt.dropvelocity")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockForm(BlockFormEvent event) {
		World world = event.getBlock().getWorld();

		if (getFreeze(null, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("form", null, world)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		World world = event.getBlock().getWorld();

		if (getFreeze(null, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("spread", null, world)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockGrow(BlockGrowEvent event) {
		World world = event.getBlock().getWorld();

		if (getFreeze(null, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("grow", null, world)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		World world = event.getBlock().getWorld();

		if (getFreeze(null, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("decay", null, world)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDestroy(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		World world = block.getWorld();

		FileConfiguration config = Main.plugin().config();

		if (getFreeze(player, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("break", player, world)) {
			if (config.getBoolean("halt.break.unbreakablelist")) {
				String blockName = block.getType().toString().toLowerCase();

				if (config.getStringList("halt.break.blocks").contains(blockName)) {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();

		if (getFreeze(player, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("durability", player, world)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		DamageCause cause = event.getCause();
		World world = entity.getWorld();

		FileConfiguration config = Main.plugin().config();

		if (entity instanceof Player) {
			Player player = (Player) entity;

			if (getFreeze(player, world)) {
				event.setCancelled(true);
				return;
			}

			if (haltEnabled("damage", player, world)) {
				event.setCancelled(true);
			}

			if (config.getBoolean("halt.damage.instantkill.enabled")) { // TODO: move to a tweak
				if (config.getStringList("halt.damage.instantkill.causes").contains(cause.toString().toLowerCase())) {
					event.setCancelled(true);
					player.setHealth(0);
				}
			}
		} else {
			if (getFreeze(null, world)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent event) {
		Entity entity = event.getEntity();
		World world = entity.getWorld();

		FileConfiguration config = Main.plugin().config();

		if (entity instanceof Player) {
			Player player = (Player) entity;

			if (getFreeze(player, world)) {
				event.setCancelled(true);
				return;
			}

			if (haltEnabled("hunger", player, world)) {
				if (config.getBoolean("halt.hunger.nolower")) {
					if (event.getFoodLevel() < player.getFoodLevel()) {
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
				}
			}
		} else {
			if (getFreeze(null, world)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	public static void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin(), new Runnable() {
			@Override
			public void run() {
				FileConfiguration config = Main.plugin().config();

				for (Player player : Bukkit.getOnlinePlayers()) {
					if (haltEnabled("hunger", player, player.getWorld()) && config.getBoolean("halt.hunger.feed")) {
						if (config.getBoolean("halt.hunger.nibble") && player.getInventory().getItemInMainHand().getType().isEdible() || player.getInventory().getItemInOffHand().getType().isEdible()) {
							player.setFoodLevel(19);
						} else {
							player.setFoodLevel(20);
						}
					}
				}
			}
		}, 0, 1);
	}

	@EventHandler
	public void onPortalCreate(PortalCreateEvent event) {
		World world = event.getWorld();

		if (getFreeze(null, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("createportal", null, world)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		World world = player.getWorld();

		FileConfiguration config = Main.plugin().config();

		if (getFreeze(player, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("inventoryinteract", player, world)) {
			List<String> listValues = config.getStringList("halt.inventoryinteract.types");
			String caseValue = event.getInventory().getType().toString().toLowerCase();

			switch (config.getString("halt.inventoryinteract.list-type")) {
				case "whitelist":
					if (!listValues.contains(caseValue)) {
						event.setCancelled(true);
					}
					break;
				case "blacklist":
					if (listValues.contains(caseValue)) {
						event.setCancelled(true);
					}
					break;
			}
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		World world = player.getWorld();

		FileConfiguration config = Main.plugin().config();

		if (getFreeze(player, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("inventoryinteract", player, world)) {
			List<String> listValues = config.getStringList("halt.inventoryinteract.types");
			String caseValue = event.getInventory().getType().toString().toLowerCase();

			switch (config.getString("halt.inventoryinteract.list-type")) {
				case "whitelist":
					if (!listValues.contains(caseValue)) {
						event.setCancelled(true);
					}
					break;
				case "blacklist":
					if (listValues.contains(caseValue)) {
						event.setCancelled(true);
					}
					break;
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		World world = player.getWorld();

		if (haltEnabled("deathdrops", player, world)) {
			event.getDrops().clear();
		}
	}

	@EventHandler
	public void onTotemUse(EntityResurrectEvent event) { // TODO: make into haltable event
		LivingEntity entity = event.getEntity();
		World world = entity.getWorld();

		if (entity instanceof Player) {
			Player player = (Player) entity;

			if (getFreeze(player, world)) {
				event.setCancelled(true);
				return;
			}
		} else {
			if (getFreeze(null, world)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();

		if (getFreeze(player, world)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onSwapHands(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();

		if (getFreeze(player, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("swaphands", player, world)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onToggleGlide(EntityToggleGlideEvent event) { // TODO: make into haltable event
		Entity entity = event.getEntity();
		World world = entity.getWorld();

		if (entity instanceof Player) {
			Player player = (Player) entity;

			if (getFreeze(player, world)) {
				event.setCancelled(true);
				return;
			}
		} else {
			if (getFreeze(null, world)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onToggleSwim(EntityToggleSwimEvent event) { // TODO: make into haltable event
		Entity entity = event.getEntity();
		World world = entity.getWorld();

		if (entity instanceof Player) {
			Player player = (Player) entity;

			if (getFreeze(player, world)) {
				event.setCancelled(true);
				return;
			}
		} else {
			if (getFreeze(null, world)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onToggleFlight(PlayerToggleFlightEvent event) { // TODO: make into haltable event
		Player player = event.getPlayer();
		World world = player.getWorld();

		if (getFreeze(player, world)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onToggleSneak(PlayerToggleSneakEvent event) { // TODO: make into haltable event
		Player player = event.getPlayer();
		World world = player.getWorld();

		if (getFreeze(player, world)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onAirChange(EntityAirChangeEvent event) { // TODO: make into haltable event
		Entity entity = event.getEntity();
		World world = entity.getWorld();

		if (entity instanceof Player) {
			Player player = (Player) entity;

			if (getFreeze(player, world)) {
				event.setCancelled(true);
				return;
			}
		} else {
			if (getFreeze(null, world)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) { // TODO: make into haltable event
		World world = event.getBlock().getWorld();

		if (getFreeze(null, world)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		World world = event.getEntity().getWorld();

		if (getFreeze(null, world)) {
			event.setCancelled(true);
			return;
		}

		if (haltEnabled("entityexplode", null, world)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		World world = entity.getWorld();

		if (damager instanceof Player) {
			Player player = (Player) damager;

			if (getFreeze(player, world)) {
				event.setCancelled(true);
				return;
			}

			if (entity instanceof ItemFrame || entity instanceof GlowItemFrame) {
				if (haltEnabled("destroyitemframe", player, world)) {
					event.setCancelled(true);
				}
			}
		} else {
			if (getFreeze(null, world)) {
				event.setCancelled(true);
				return;
			}

			if (entity instanceof ItemFrame || entity instanceof GlowItemFrame) {
				if (haltEnabled("destroyitemframe", null, world)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity remover = event.getRemover();
		World world = entity.getWorld();

		if (remover instanceof Player) {
			Player player = (Player) remover;

			if (getFreeze(player, world)) {
				event.setCancelled(true);
				return;
			}

			if (entity instanceof ItemFrame || entity instanceof GlowItemFrame) {
				if (haltEnabled("destroyitemframe", player, world)) {
					event.setCancelled(true);
				}
			}
		} else {
			if (getFreeze(null, world)) {
				event.setCancelled(true);
				return;
			}

			if (entity instanceof ItemFrame || entity instanceof GlowItemFrame) {
				if (haltEnabled("destroyitemframe", null, world)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		Player player = event.getPlayer();
		World world = entity.getWorld();

		if (getFreeze(player, world)) {
			event.setCancelled(true);
			return;
		}

		if (entity instanceof ItemFrame || entity instanceof GlowItemFrame) {
			if (haltEnabled("useitemframe", player, world)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		Entity entity = event.getEntity();
		World world = entity.getWorld();

		FileConfiguration config = Main.plugin().config();

		if (getFreeze(null, world)) {
			event.setCancelled(true);
			return;
		}

		if (entity instanceof ItemFrame || entity instanceof GlowItemFrame) {
			if (haltEnabled("placeitemframe", null, world)) {
				event.setCancelled(true);
			}
		}

		if (haltEnabled("entityspawn", null, world)) {
			List<String> listValues = config.getStringList("halt.entityspawn.entities");
			String caseValue = entity.getType().toString().toLowerCase();

			switch (config.getString("halt.entityspawn.list-type")) {
				case "whitelist":
					if (!listValues.contains(caseValue)) {
						event.setCancelled(true);
					}
					break;
				case "blacklist":
					if (listValues.contains(caseValue)) {
						event.setCancelled(true);
					}
					break;
			}
		}
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) { // TODO: test
		Block block = event.getBlock();
		World world = block.getWorld();
		Entity entity = event.getEntity();

		if (entity.getType().equals(EntityType.ENDERMAN)) {
			if (haltEnabled("endermanpickup", null, world)) {
				event.setCancelled(true);
			}
		}
	}
}
