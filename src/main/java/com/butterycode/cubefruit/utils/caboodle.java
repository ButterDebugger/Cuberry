package com.butterycode.cubefruit.utils;

import com.butterycode.cubefruit.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.block.data.type.Chest;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

public class caboodle implements Listener {

	/**
	 *   Quick and Simplified Methods
	 */

	public static String stringifyLocation(Location location) {
		return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ();
	}

	public static String stringifyLocation(Block block) {
		return block.getWorld().getName() + ";" + block.getX() + ";" + block.getY() + ";" + block.getZ();
	}

	public static String stringifyLocation(Player player) {
		Location location = player.getLocation();
		return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
	}

	public static Location parseLocation(String string) {
		String[] locArgs = string.split(";");
		World world = Bukkit.getWorld(locArgs[0]);
		Vector xyz = new Vector(Double.parseDouble(locArgs[1]), Double.parseDouble(locArgs[2]), Double.parseDouble(locArgs[3]));

		getLowestBlockLocation(new Location(Bukkit.getWorld("world"), 1, 1, 1));

		if (locArgs.length == 4) { // Location: world, x, y, z
			return new Location(world, xyz.getX(), xyz.getY(), xyz.getZ());
		} else if (locArgs.length == 6) { // Player Location: world, x, y, z, yaw, pitch
			return new Location(world, xyz.getX(), xyz.getY(), xyz.getZ(), Float.parseFloat(locArgs[4]), Float.parseFloat(locArgs[5]));
		} else {
			return null;
		}
	}

	@Deprecated // TODO: recode and use loc.getWorld().getMinHeight()
	public static Location getLowestBlockLocation(Location loc) {
		if (loc.getBlockY() <= 1) return null;
		Location next = loc.clone().add(0, -1, 0);

		if (next.getBlock().isEmpty()) {
			loc = getLowestBlockLocation(next);
		}

		return loc;
	}

	@Deprecated // TODO: recode and use loc.getWorld().getMaxHeight()
	public static Location getHighestBlockLocation(Location loc) {
		if (loc.getBlockY() <= 1) return null;
		Location next = loc.clone().add(0, 1, 0);

		if (!next.getBlock().isEmpty()) {
			loc = getHighestBlockLocation(next);
		}

		return loc;
	}

	public static Iterator<Vector> line(Vector point1, Vector point2, double space) {
		double distance = point1.distance(point2);

		Vector p1 = point1.clone();
		Vector p2 = point2.clone();

		Vector vector = p2.subtract(p1).normalize().multiply(space);
		ArrayList<Vector> points = new ArrayList<>();

		for (double length = 0; length < distance; p1.add(vector)) {
			points.add(p1.clone());
			length += space;
		}

		return points.iterator();
	}

	@SuppressWarnings("deprecation")
	public static OfflinePlayer getOfflinePlayer(String username) {
		if (dogTags.isOnline(username)) {
			return Bukkit.getPlayer(username);
		} else {
			return Bukkit.getOfflinePlayer(username);
		}
	}

	public static OfflinePlayer getOfflinePlayer(UUID uuid) {
		if (dogTags.isOnline(uuid)) {
			return Bukkit.getPlayer(uuid);
		} else {
			return Bukkit.getOfflinePlayer(uuid);
		}
	}

	public static int randomInteger(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

	public static double randomDouble(double min, double max) {
		return new Random().nextDouble() * (max - min) + min;
	}

	public static float randomFloat(float min, float max) {
		return new Random().nextFloat() * (max - min) + min;
	}

	public static String randomListItem(List<String> list) {
		return list.get(new Random().nextInt(list.size()));
	}

	public static boolean chance(double percent) {
		double random = randomDouble(0, 100);
		if (random <= percent) {
			return true;
		} else {
			return false;
		}
	}

	public static String[] slice(String[] args, int startIndex, int endIndex) {
		String[] slicedArray = new String[endIndex - startIndex];

		for (int i = 0; i < slicedArray.length; i++) {
			slicedArray[i] = args[startIndex + i];
		}

		return slicedArray;
	}

	public static List<Block> getRegionBlocks(World world, Location loc1, Location loc2) {
		List<Block> blocks = new ArrayList<>();

		int x1 = loc1.getBlockX();
		int y1 = loc1.getBlockY();
		int z1 = loc1.getBlockZ();

		int x2 = loc2.getBlockX();
		int y2 = loc2.getBlockY();
		int z2 = loc2.getBlockZ();

		int lowestX = Math.min(x1, x2);
		int lowestY = Math.min(y1, y2);
		int lowestZ = Math.min(z1, z2);

		int highestX = Math.max(x1, x2);
		int highestY = Math.max(y1, y2);
		int highestZ = Math.max(z1, z2);

		for (int x = lowestX; x <= highestX; x++) {
			for (int y = lowestY; y <= highestY; y++) {
				for (int z = lowestZ; z <= highestZ; z++) {
					Location loc = new Location(world, x, y, z);
					blocks.add(loc.getBlock());
				}
			}
		}

		return blocks;
	}

	public static String getCardinalDirection(Entity entity) { // TODO: retest and change return to an enum
		double rotation = (entity.getLocation().getYaw() - 90.0F) % 360.0F;

		if (rotation < 0.0D) rotation += 360.0D;
		if ((0.0D <= rotation) && (rotation < 45.0D))
			return "W";
		if ((45.0D <= rotation) && (rotation < 135.0D))
			return "N";
		if ((135.0D <= rotation) && (rotation < 225.0D))
			return "E";
		if ((225.0D <= rotation) && (rotation < 315.0D))
			return "S";
		if ((315.0D <= rotation) && (rotation < 360.0D)) {
			return "W";
		}
		return null;
	}

	public static void sendActionbar(Player player, String string) {
//		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(string));
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(string));
	}

	public static Object getMetadata(Metadatable object, String key) {
		List<MetadataValue> values = object.getMetadata(key);

		if (values.isEmpty()) {
			return null;
		}

		for (MetadataValue value : values) {
			if (value.getOwningPlugin().equals(Main.plugin)) {
				return value.value();
			}
		}

		return null;
	}

	public static boolean hasMetadata(Metadatable object, String key) {
		return getMetadata(object, key) != null;
	}

	public static void setMetadata(Metadatable object, String key, Object value) {
		object.setMetadata(key, new FixedMetadataValue(Main.plugin, value));
	}

	public static void removeMetadata(Metadatable object, String key) {
		object.removeMetadata(key, Main.plugin);
	}

	public static void respawn(Player player) {
		player.spigot().respawn();
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> player.spigot().respawn(), 1L);
	}

	public static void log(String string) {
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(string);
	}

	public enum LogType {
		RAW,
		INFO,
		WARN,
		SEVERE
	}

	public static void log(Plugin plugin, String string, LogType logType) {
		Logger logger = plugin.getLogger();

		switch (logType) {
			case RAW:
				log(string);
				break;
			case INFO:
				logger.info(string);
				break;
			case WARN:
				logger.warning(string);
				break;
			case SEVERE:
				logger.severe(string);
				break;
		}
	}

	public static void sendConsole(String string) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string);
	}

	public static void broadcast(String string) {
		Bukkit.broadcastMessage(string);
	}

	public static void dustParticle(Player player, Location location, int amount, Vector offset, Color color, int size) {
		player.spawnParticle(Particle.REDSTONE, location, amount, offset.getX(), offset.getY(), offset.getZ(), new Particle.DustOptions(color, size));
	}

	public static Player getRandomPlayer() {
		ArrayList<Player> allPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
		int random = new Random().nextInt(allPlayers.size());
		return allPlayers.get(random);
	}

	@Deprecated // TODO: remove me
	public static String getStringTag(ItemStack item, String tagname) {
		if (item == null) return null;
		NamespacedKey key = new NamespacedKey(Main.plugin, tagname);
		ItemMeta itemMeta = item.getItemMeta();
		PersistentDataContainer container;

		try {
			container = itemMeta.getPersistentDataContainer();
		} catch (NullPointerException e) {
			return null;
		}

		if (container.has(key, PersistentDataType.STRING)) {
			return container.get(key, PersistentDataType.STRING);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void setItemTag(ItemStack item, String tagname, @SuppressWarnings("rawtypes") PersistentDataType datatype, Object value) {
		NamespacedKey key = new NamespacedKey(Main.plugin, tagname);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.getPersistentDataContainer().set(key, datatype, value);
		item.setItemMeta(itemMeta);
	}

	@SuppressWarnings("unchecked")
	public static Object getItemTag(ItemStack item, String tagname, @SuppressWarnings("rawtypes") PersistentDataType datatype) {
		NamespacedKey key = new NamespacedKey(Main.plugin, tagname);
		ItemMeta itemMeta = item.getItemMeta();
		PersistentDataContainer container;

		try {
			container = itemMeta.getPersistentDataContainer();
		} catch (NullPointerException err) {
			return null;
		}

		if (container.has(key, datatype)) {
			return container.get(key, datatype);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean hasItemTag(ItemStack item, String tagname, @SuppressWarnings("rawtypes") PersistentDataType datatype) {
		NamespacedKey key = new NamespacedKey(Main.plugin, tagname);
		ItemMeta itemMeta = item.getItemMeta();
		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		return container.has(key, datatype);
	}

	public static void setSlots(Inventory menu, int start, int end, ItemStack item) {
		int index = start;
		while (index <= end) {
			menu.setItem(index, item);
			index++;
		}
	}

	public static void setLoreLine(ItemStack item, int line, String string) {
		ItemMeta itemmeta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();

		if (itemmeta.getLore() != null) for (String oldlore : itemmeta.getLore()) {
			lore.add(oldlore);
		}

		while (true) {
			try {
				lore.set(line, string);
				break;
			} catch (IndexOutOfBoundsException e) {
				lore.add("");
			}
		}

		itemmeta.setLore(lore);
		item.setItemMeta(itemmeta);
	}

	public static void setDisplayName(ItemStack item, String string) {
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(string);
		item.setItemMeta(itemmeta);
	}

	public static void setItemFlags(ItemStack item, boolean toggle, ItemFlag... flags) {
		ItemMeta itemmeta = item.getItemMeta();
		if (toggle) {
			itemmeta.addItemFlags(flags);
		} else {
			itemmeta.removeItemFlags(flags);
		}
		item.setItemMeta(itemmeta);
	}

	public static List<Block> getGroupedBlocks(Block block) { // TODO: not fully tested
		List<Block> blocks = new ArrayList<>();
		BlockData blockData = block.getBlockData();
		
		blocks.add(block);
		
		if (blockData instanceof Bisected) {
			Bisected bisected = (Bisected) blockData;
			
			if (dogTags.isTwoBlocksTall(block.getType())) {
				if (bisected.getHalf().equals(Half.BOTTOM)) {
					Block otherBlock = block.getRelative(BlockFace.UP);
					BlockData otherBlockData = otherBlock.getBlockData();
					
					if (block.getType().equals(otherBlock.getType()) && otherBlockData instanceof Bisected) {
						Bisected otherBisected = (Bisected) otherBlockData;
						
						if (otherBisected.getHalf().equals(Half.TOP)) {
							blocks.add(otherBlock);
						}
					}
				} else {
					Block otherBlock = block.getRelative(BlockFace.DOWN);
					BlockData otherBlockData = otherBlock.getBlockData();
					
					if (block.getType().equals(otherBlock.getType()) && otherBlockData instanceof Bisected) {
						Bisected otherBisected = (Bisected) otherBlockData;
						
						if (otherBisected.getHalf().equals(Half.BOTTOM)) {
							blocks.add(otherBlock);
						}
					}
				}
			}
		}
		
		if (blockData instanceof Bed) {
			Bed bed = (Bed) blockData;

			if (bed.getPart().equals(Part.FOOT)) {
				Block otherBlock = block.getRelative(bed.getFacing());
				BlockData otherBlockData = otherBlock.getBlockData();
				
				if (block.getType().equals(otherBlock.getType()) && otherBlockData instanceof Bed) {
					Bed otherBed = (Bed) otherBlockData;
					
					if (otherBed.getPart().equals(Part.HEAD)) {
						blocks.add(otherBlock);
					}
				}
			} else {
				Block otherBlock = block.getRelative(bed.getFacing().getOppositeFace());
				BlockData otherBlockData = otherBlock.getBlockData();
				
				if (block.getType().equals(otherBlock.getType()) && otherBlockData instanceof Bed) {
					Bed otherBed = (Bed) otherBlockData;
					
					if (otherBed.getPart().equals(Part.FOOT)) {
						blocks.add(otherBlock);
					}
				}
			}
		}
		
		if (blockData instanceof Chest) {
			Chest chest = (Chest) blockData;
			
			if (!chest.getType().equals(Chest.Type.SINGLE)) {
				if (chest.getType().equals(Chest.Type.LEFT)) {
					Block otherBlock = null;
					
					switch (chest.getFacing()) {
					case NORTH:
						otherBlock = block.getRelative(BlockFace.EAST);
						break;
					case EAST:
						otherBlock = block.getRelative(BlockFace.SOUTH);
						break;
					case SOUTH:
						otherBlock = block.getRelative(BlockFace.WEST);
						break;
					case WEST:
						otherBlock = block.getRelative(BlockFace.NORTH);
						break;
					default:
						break;
					}
					
					if (otherBlock != null) {
						BlockData otherBlockData = otherBlock.getBlockData();
						
						if (block.getType().equals(otherBlock.getType()) && otherBlockData instanceof Chest) {
							Chest otherChest = (Chest) otherBlockData;
							
							if (otherChest.getType().equals(Chest.Type.RIGHT)) {
								blocks.add(otherBlock);
							}
						}
					}
				} else {
					Block otherBlock = null;
					
					switch (chest.getFacing()) {
					case NORTH:
						otherBlock = block.getRelative(BlockFace.WEST);
						break;
					case EAST:
						otherBlock = block.getRelative(BlockFace.NORTH);
						break;
					case SOUTH:
						otherBlock = block.getRelative(BlockFace.EAST);
						break;
					case WEST:
						otherBlock = block.getRelative(BlockFace.SOUTH);
						break;
					default:
						break;
					}
					
					if (otherBlock != null) {
						BlockData otherBlockData = otherBlock.getBlockData();
						
						if (block.getType().equals(otherBlock.getType()) && otherBlockData instanceof Chest) {
							Chest otherChest = (Chest) otherBlockData;
							
							if (otherChest.getType().equals(Chest.Type.LEFT)) {
								blocks.add(otherBlock);
							}
						}
					}
				}
			}
		}
		
		return blocks;
	}

	public static Objective createBoard(Player player, String name, String title) { // test and change later
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective obj = board.registerNewObjective(name, "dummy", title);
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.setScoreboard(board);
		return obj;
	}

	public static ArrayList<Block> getChunkBlocks(Chunk chunk) {
		ArrayList<Block> blocks = new ArrayList<>();
		World world = chunk.getWorld();
		int minX = chunk.getX() * 16;
		int minZ = chunk.getZ() * 16;
		int maxX = minX + 15;
		int maxY = world.getMaxHeight();
		int maxZ = minZ + 15;

		for (int x = minX; x <= maxX; ++x) {
			for (int y = 0; y <= maxY; ++y) {
				for (int z = minZ; z <= maxZ; ++z) {
					blocks.add(world.getBlockAt(x, y, z));
				}
			}
		}

		return blocks;
	}

	public static boolean deleteWorld(String worldname) {
		if (getAllWorldNames().contains(worldname)) {
			File file = new File(Bukkit.getWorldContainer(), worldname);

			return deleteFile(file);
		} else {
			return false;
		}
	}

	public static void copyWorld(File source, File target) {
		try {
			ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.dat"));
			if (!ignore.contains(source.getName())) {
				if(source.isDirectory()) {
					if (!target.exists()) target.mkdirs();
					String files[] = source.list();
					for (String file : files) {
						File srcFile = new File(source, file);
						File destFile = new File(target, file);
						copyWorld(srcFile, destFile);
					}
				} else {
					InputStream in = new FileInputStream(source);
					OutputStream out = new FileOutputStream(target);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = in.read(buffer)) > 0)
						out.write(buffer, 0, length);
					in.close();
					out.close();
				}
			}
		} catch (IOException e) {

		}
	}

	public static boolean deleteFile(File file) {
		if (file.exists()) {
			File files[] = file.listFiles();
			for (File file2 : files) {
				if (file2.isDirectory()) {
					deleteFile(file2);
				} else {
					file2.delete();
				}
			}
		}
		return (file.delete());
	}

	public static Material getMaterialByName(String name) {
		for (Material mat : Material.values()) {
			if (mat.toString().equalsIgnoreCase(name)) return mat;
		}

		return null;
	}

	public static Enchantment getEnchantmentByName(String name) {
		for (Enchantment ech : Enchantment.values()) {
			if (ech.getKey().toString().equalsIgnoreCase(name)) return ech;
		}

		return null;
	}

	public static Locale getPlayerLocale(Player player) {
		return Locale.forLanguageTag(player.getLocale().replace("_", "-"));
	}

	public static double round(double value, int factor) {
		factor = (int) Math.pow(10, factor);
		return Math.floor(value * factor) / factor;
	}

	public static List<String> getWorldNames() {
		List<String> worlds = new ArrayList<>();
		for (World world : Bukkit.getServer().getWorlds()) {
			worlds.add(world.getName());
		}
		return worlds;
	}

	public static List<String> getAllWorldNames() {
		String[] directories = Bukkit.getWorldContainer().list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory() && new File(current, name + "/level.dat").exists();
			}
		});
		return Arrays.asList(directories);
	}

	public static List<String> getOnlinePlayerNames() {
		List<String> playerNames = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			playerNames.add(player.getName());
		}
		return playerNames;
	}

	public static List<String> getOfflinePlayerNames() {
		List<String> playerNames = new ArrayList<>();
		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			playerNames.add(player.getName());
		}
		return playerNames;
	}

	public static List<String> getUnloadedWorldNames() {
		List<String> worlds = new ArrayList<>();
		List<String> loadedWorlds = getWorldNames();
		for (String world : getAllWorldNames()) {
			if (!loadedWorlds.contains(world)) {
				worlds.add(world);
			}
		}
		return worlds;
	}

	public static int getTotalChunksLoaded() {
		int total = 0;
		for (World world : Bukkit.getWorlds()) {
			total += world.getLoadedChunks().length;
		}
		return total;
	}

	public static int getTotalEntities() {
		int total = 0;
		for (World world : Bukkit.getWorlds()) {
			total += world.getLivingEntities().size();
		}
		return total;
	}

	public static int getItemIndex(Inventory inv, ItemStack item) {
		for (int index = 0; index < inv.getSize(); index++) {
			ItemStack i = inv.getItem(index);
			if (i != null && i.equals(item)) return index;
		}

		return -1;
	}

	public static Block getBlockOnFace(Block block) {
		if (block.getBlockData() instanceof Directional) {
			Directional direction = (Directional) block.getBlockData();

			return block.getRelative(direction.getFacing());
		}

		return null;
	}

	public static int getServerViewDistance() {
		return Bukkit.getViewDistance() * 16 + 16;
	}

	public static void giveItem(Player player, ItemStack itemstack) {
		if (player.getInventory().firstEmpty() != -1) {
			player.getInventory().addItem(itemstack);
		} else {
			Item itemDrop = player.getWorld().dropItem(player.getEyeLocation().subtract(0, 0.25, 0), itemstack); // TODO: make drop from the mouth, like normal gameplay, without doing hardcoded math
			itemDrop.setVelocity(player.getEyeLocation().getDirection().multiply(0.25)); // TODO: make direction more random and natural
		}
	}

	public static String[] splice(String[] list, int start, int deleteCount) { // TODO: check for an error when deleteCount is larger than length, also return anything removed
		String[] newlist = new String[list.length - deleteCount];
		for (int i = 0, j = deleteCount; i < list.length; i++) {
			if (i >= start && j > 0) {
				j--;
			} else {
				if (j == 0) {
					newlist[i - deleteCount] = list[i];
				} else {
					newlist[i] = list[i];
				}
			}
		}
		return newlist;
	}

	/**
	 *   Plugin Assisted Methods
	 */

	private static double tps;

	public static void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			long lasttick;

			@Override
			public void run() {
				long timeSinceLastTickInNanoseconds = System.nanoTime() - lasttick;

				lasttick = System.nanoTime();

				tps = (50_000_000 / (double) timeSinceLastTickInNanoseconds) * 20;
			}
		}, 0, 1);
	}

	public static double getTps() {
		return tps;
	}

	public static void outlineBlock(Block block, Player player) {
		player.spawnParticle(Particle.TOWN_AURA, block.getX() + 0.5, block.getY(), block.getZ(), 1, 0.25, 0, 0);
		player.spawnParticle(Particle.TOWN_AURA, block.getX(), block.getY(), block.getZ() + 0.5, 1, 0, 0, 0.25);
		player.spawnParticle(Particle.TOWN_AURA, block.getX() + 1, block.getY(), block.getZ() + 0.5, 1, 0, 0, 0.25);
		player.spawnParticle(Particle.TOWN_AURA, block.getX() + 0.5, block.getY(), block.getZ() + 1, 1, 0.25, 0, 0);

		player.spawnParticle(Particle.TOWN_AURA, block.getX(), block.getY() + 0.5, block.getZ(), 1, 0, 0.25, 0);
		player.spawnParticle(Particle.TOWN_AURA, block.getX() + 1, block.getY() + 0.5, block.getZ(), 1, 0, 0.25, 0);
		player.spawnParticle(Particle.TOWN_AURA, block.getX(), block.getY() + 0.5, block.getZ() + 1, 1, 0, 0.25, 0);
		player.spawnParticle(Particle.TOWN_AURA, block.getX() + 1, block.getY() + 0.5, block.getZ() + 1, 1, 0, 0.25, 0);

		player.spawnParticle(Particle.TOWN_AURA, block.getX() + 0.5, block.getY() + 1, block.getZ(), 1, 0.25, 0, 0);
		player.spawnParticle(Particle.TOWN_AURA, block.getX(), block.getY() + 1, block.getZ() + 0.5, 1, 0, 0, 0.25);
		player.spawnParticle(Particle.TOWN_AURA, block.getX() + 1, block.getY() + 1, block.getZ() + 0.5, 1, 0, 0, 0.25);
		player.spawnParticle(Particle.TOWN_AURA, block.getX() + 0.5, block.getY() + 1, block.getZ() + 1, 1, 0.25, 0, 0);
	}

	public static Location midpoint(Location loc1, Location loc2) {
		return new Location(loc1.getWorld(), (loc1.getX() + loc2.getX()) / 2, (loc1.getY() + loc2.getY()) / 2, (loc1.getZ() + loc2.getZ()) / 2);
	}

	public static int getPlaytime(Player player) { // Its in ticks
		return player.getStatistic(Statistic.PLAY_ONE_MINUTE);
	}

	public static int getPlaytime(OfflinePlayer player) { // Its still in ticks
		return player.getStatistic(Statistic.PLAY_ONE_MINUTE);
	}

	/**
	 *   Plugin Methods
	 */

	@Deprecated // TODO: remove me, also remove Main.plugin reference
	public static void registerCommand(List<String> aliases) {
		PluginCommand command = getCommand(aliases.get(0), Main.plugin);
		command.setAliases(aliases);
		getCommandMap().register(Main.plugin.getDescription().getName(), command);
	}

	@Deprecated // TODO: remove me, also remove Main.plugin reference
	public static void registerCommand(String name) {
		PluginCommand command = getCommand(name, Main.plugin);
		getCommandMap().register(Main.plugin.getDescription().getName(), command);
	}

	public static PluginCommand getCommand(String name, Plugin plugin) {
		PluginCommand command = null;

		try {
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);

			command = c.newInstance(name, plugin);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException |
				 InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}

		return command;
	}

	public static CommandMap getCommandMap() {
		CommandMap commandMap = null;

		try {
			if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);

				commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return commandMap;
	}

	public static boolean hasPermission(Object target, String string) {
		if (target instanceof Player) {
			Player player = (Player) target;

			return player.hasPermission("cubefruit." + string) || player.hasPermission("cubefruit.*");
		} else if (target instanceof CommandSender) {
			CommandSender sender = (CommandSender) target;

			if (sender instanceof ConsoleCommandSender) {
				return true;
			} else {
				return sender.hasPermission("cubefruit." + string) || sender.hasPermission("cubefruit.*");
			}
		} else {
			return false;
		}
	}
}
