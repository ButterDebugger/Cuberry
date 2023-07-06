package com.butterycode.cubefruit;

import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.dataStorage;
import com.butterycode.cubefruit.utils.DogTags;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Iterator;
import java.util.List;

public class lockedBlocks implements Listener, CommandExecutor {

	private static ItemStack masterKey;

	public static void start() {
		dataStorage blockData = Main.plugin().getData("blocks.yml");

		ItemStack temp = new ItemStack(Material.TRIPWIRE_HOOK);
		caboodle.setDisplayName(temp, awesomeText.prettifyMessage("&#FFB847Master Key"));
		caboodle.setItemTag(temp, "isMasterKey", PersistentDataType.BYTE, (byte) 1);
		caboodle.setItemFlags(temp, true, ItemFlag.HIDE_ENCHANTS);
		temp.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		masterKey = temp;
		
		// Resolve all locked blocks
		for (String key : blockData.getKeys()) {
			if (!blockData.exists(key + ".lock")) continue;

			Location blockLoc = caboodle.parseLocation(key);
			Block block = blockLoc.getBlock();
			
			resolveBlock(block);
		}
	}
	
	@EventHandler
	public void onBlockLock(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		FileConfiguration config = Main.plugin().config();
		
		// Validate item
		if (item == null) return;
//		if (!event.getHand().equals(EquipmentSlot.HAND)) return;
		if (!item.getType().equals(Material.TRIPWIRE_HOOK)) return;
		
		// Validate name of key
		String keyname = item.getItemMeta().getDisplayName();
		
		if (keyname.length() == 0) return;

		// Validate event
		if (!action.equals(Action.RIGHT_CLICK_BLOCK)) return;
		if (!player.isSneaking()) return;
		
		// Check if block is lockable
		Block block = event.getClickedBlock();

		if (!config.getStringList("locks.lockable").contains(block.getType().toString().toLowerCase())) return;
		
		// Do unlock and lock stuff...
		
		if (isBlockLocked(block)) {
			if (matchKey(block, item)) {
				unlockBlock(block);

				player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 0.5f, 1f);
				caboodle.sendActionbar(player, awesomeText.prettifyMessage(awesomeText.screamingSnakeCase(block.getType().toString()) + " has been unlocked."));

				event.setCancelled(true);
			} else {
				player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 0.5f, 1f);
				caboodle.sendActionbar(player, awesomeText.prettifyMessage(awesomeText.screamingSnakeCase(block.getType().toString()) + " cannot be unlocked."));

				event.setCancelled(true);
			}
		} else {
			if (isMasterKey(item)) {
				player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 0.5f, 1f);
				caboodle.sendActionbar(player, awesomeText.prettifyMessage(awesomeText.screamingSnakeCase(block.getType().toString()) + " cannot be locked."));

				event.setCancelled(true);
			} else {
				lockBlock(block, keyname);

				player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 0.5f, 1f);
				caboodle.sendActionbar(player, awesomeText.prettifyMessage(awesomeText.screamingSnakeCase(block.getType().toString()) + " has been locked."));

				event.setCancelled(true);
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = Main.plugin().config();

		if (label.equalsIgnoreCase("getmasterkey")) {
			if (!config.getBoolean("locks.master-key")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7This command is not enabled in the config."));
				return true;
			}
			
			if (!caboodle.hasPermission(sender, "locks.masterkey")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}
			
			Player player = (Player) sender;

			caboodle.giveItem(player, masterKey);

			sender.sendMessage(awesomeText.prettifyMessage("&7You have been given a master key."));
			return true;
		}
		return false;
	}
	
	private void lockBlock(Block block, String key) {
		List<Block> blockGroup = caboodle.getGroupedBlocks(block);
		dataStorage blockData = Main.plugin().getData("blocks.yml");
		
		for (Block b : blockGroup) {
			String stringLoc = caboodle.stringifyLocation(b);

			blockData.set(stringLoc + ".lock", key);
		}
	}
	
	private static void unlockBlock(Block block) {
		List<Block> blockGroup = caboodle.getGroupedBlocks(block);
		dataStorage blockData = Main.plugin().getData("blocks.yml");
		
		for (Block b : blockGroup) {
			String stringLoc = caboodle.stringifyLocation(b);

			blockData.remove(stringLoc + ".lock");
		}
	}
	
	private static boolean isBlockLocked(Block block) {
		List<Block> blockGroup = caboodle.getGroupedBlocks(block);
		dataStorage blockData = Main.plugin().getData("blocks.yml");
		
		for (Block b : blockGroup) {
			String stringLoc = caboodle.stringifyLocation(b);
			
			if (blockData.exists(stringLoc + ".lock")) {
				return true;
			}
		}
		
		return false;
	}
	
	private String getBlockKey(Block block) {
		String stringLoc = caboodle.stringifyLocation(block);
		dataStorage blockData = Main.plugin().getData("blocks.yml");
		
		if (blockData.exists(stringLoc + ".lock")) {
			return blockData.getString(stringLoc + ".lock");
		} else {
			return null;
		}
	}
	
	private boolean matchKey(Block block, ItemStack item) {
		FileConfiguration config = Main.plugin().config();

		if (item == null) return false;
		if (!item.getType().equals(Material.TRIPWIRE_HOOK)) return false;
		
		if (config.getBoolean("locks.master-key")) {
			if (item.getItemMeta().equals(masterKey.getItemMeta())) return true;
		}
		
		String blockkey = getBlockKey(block);
		String keyname = item.getItemMeta().getDisplayName();
		
		if (keyname.length() == 0) return false;
		
		return blockkey.equals(keyname);
	}
	
	private boolean isMasterKey(ItemStack item) {
		FileConfiguration config = Main.plugin().config();

		if (item == null) return false;
		if (!item.getType().equals(Material.TRIPWIRE_HOOK)) return false;
		
		if (!config.getBoolean("locks.master-key")) return false;
		return item.getItemMeta().equals(masterKey.getItemMeta());
	}
	
	private static void resolveBlock(Block block) {
		FileConfiguration config = Main.plugin().config();

		if (!isBlockLocked(block)) return;
		
		if (block.isEmpty() || !config.getStringList("locks.lockable").contains(block.getType().toString().toLowerCase())) {
			unlockBlock(block);
		}
	}
	
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		resolveBlock(event.getBlock());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();

		if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
			Block block = event.getClickedBlock();
			
			if (isBlockLocked(block)) {
				if (!matchKey(block, item)) {
					player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 0.5f, 1f);
					caboodle.sendActionbar(player, awesomeText.prettifyMessage(awesomeText.screamingSnakeCase(block.getType().toString()) + " is locked!"));

					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		Block block = event.getBlock();
		Block blockAbove = block.getRelative(BlockFace.UP);
		FileConfiguration config = Main.plugin().config();
		
		if (isBlockLocked(block)) {
			if (config.getBoolean("locks.indestructable")) {
				event.setCancelled(true);
			} else {
				unlockBlock(block);
			}
		} else if (isBlockLocked(blockAbove) && DogTags.isSupportedFromBelow(blockAbove)) {
			if (config.getBoolean("locks.indestructable")) {
				event.setCancelled(true);
			} else {
				unlockBlock(blockAbove);
			}
		}
	}

	@EventHandler
	public void onDestroy(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Block blockAbove = block.getRelative(BlockFace.UP);
		FileConfiguration config = Main.plugin().config();

		if (isBlockLocked(block)) {
			if (config.getBoolean("locks.indestructable")) {
				player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 0.5f, 1f);
				caboodle.sendActionbar(player, awesomeText.prettifyMessage(awesomeText.screamingSnakeCase(block.getType().toString()) + " is locked!"));

				event.setCancelled(true);
			} else {
				unlockBlock(block);
			}
		} else if (isBlockLocked(blockAbove) && DogTags.isSupportedFromBelow(blockAbove)) {
			if (config.getBoolean("locks.indestructable")) {
				player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 0.5f, 1f);
				caboodle.sendActionbar(player, awesomeText.prettifyMessage("The block above is locked!"));

				event.setCancelled(true);
			} else {
				unlockBlock(blockAbove);
			}
		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		Iterator<Block> iter = event.blockList().iterator();
		FileConfiguration config = Main.plugin().config();

		while (iter.hasNext()) {
			Block block = iter.next();
			Block blockAbove = block.getRelative(BlockFace.UP);

			if (isBlockLocked(block)) {
				if (config.getBoolean("locks.indestructable")) {
					iter.remove();
				} else {
					unlockBlock(block);
				}
			} else if (isBlockLocked(blockAbove) && DogTags.isSupportedFromBelow(blockAbove)) {
				if (config.getBoolean("locks.indestructable")) {
					iter.remove();
				} else {
					unlockBlock(blockAbove);
				}
			}
		}
	}

	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent event) { // TODO: protect the contents of the block
//		InventoryHolder holder = event.getSource().getHolder();
	}

	@EventHandler
	public void onPistonPush(BlockPistonExtendEvent event) {
		List<Block> blocks = event.getBlocks();
		FileConfiguration config = Main.plugin().config();

		for (Block block : blocks) {
			Block blockAbove = block.getRelative(BlockFace.UP);
			
			if (isBlockLocked(block)) {
				if (config.getBoolean("locks.indestructable")) {
					event.setCancelled(true);
					break;
				} else {
					unlockBlock(block);
				}
			} else if (isBlockLocked(blockAbove) && DogTags.isSupportedFromBelow(blockAbove)) {
				if (config.getBoolean("locks.indestructable")) {
					event.setCancelled(true);
					break;
				} else {
					unlockBlock(blockAbove);
				}
			}
		}
	}

	@EventHandler
	public void onRedstonePower(BlockRedstoneEvent event) {
		Block block = event.getBlock();
		FileConfiguration config = Main.plugin().config();

		if (isBlockLocked(block)) {
			if (config.getBoolean("locks.indestructable")) {
				event.setNewCurrent(0);
			}
		}
	}

	@EventHandler
	public void onPistonPull(BlockPistonRetractEvent event) {
		FileConfiguration config = Main.plugin().config();

		for (Block block : event.getBlocks()) {
			Block blockAbove = block.getRelative(BlockFace.UP);
			
			if (isBlockLocked(block)) {
				if (config.getBoolean("locks.indestructable")) {
					event.setCancelled(true);
					break;
				} else {
					unlockBlock(block);
				}
			} else if (isBlockLocked(blockAbove) && DogTags.isSupportedFromBelow(blockAbove)) {
				if (config.getBoolean("locks.indestructable")) {
					event.setCancelled(true);
					break;
				} else {
					unlockBlock(blockAbove);
				}
			}
		}
	}
}
