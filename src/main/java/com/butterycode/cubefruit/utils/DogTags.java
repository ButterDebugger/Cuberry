package com.butterycode.cubefruit.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Hangable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.*;
import org.bukkit.block.data.type.Bell.Attachment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DogTags {

	/*
	 *   Material Tags
	 */
	
	public static boolean isWearable(Material material) {
		EquipmentSlot slot = getEquipmentSlot(material);

		return !(slot.equals(EquipmentSlot.HAND) || slot.equals(EquipmentSlot.OFF_HAND));
	}

	public static EquipmentSlot getEquipmentSlot(Material material) {
		List<Material> headList = Arrays.asList(new Material[] {
			Material.CARVED_PUMPKIN,
			Material.DRAGON_HEAD,
			Material.SKELETON_SKULL,
			Material.WITHER_SKELETON_SKULL,
			Material.CREEPER_HEAD,
			Material.PLAYER_HEAD,
			Material.ZOMBIE_HEAD
		});

		List<Material> chestList = Arrays.asList(new Material[] {
			Material.ELYTRA
		});

		List<Material> legsList = Arrays.asList(new Material[] {});

		List<Material> feetList = Arrays.asList(new Material[] {});

		List<Material> offhandList = Arrays.asList(new Material[] {
			Material.SHIELD,
			Material.TOTEM_OF_UNDYING
		});

		if (isHelmet(material) || headList.contains(material)) {
			return EquipmentSlot.HEAD;
		} else if (isChestplate(material) || chestList.contains(material)) {
			return EquipmentSlot.CHEST;
		} else if (isLeggings(material) || legsList.contains(material)) {
			return EquipmentSlot.LEGS;
		} else if (isBoots(material) || feetList.contains(material)) {
			return EquipmentSlot.FEET;
		} else if (offhandList.contains(material)) {
			return EquipmentSlot.OFF_HAND;
		} else {
			return EquipmentSlot.HAND;
		}
	}

	public static boolean isArmor(Material material) {
//		return CraftItemStack.asNMSCopy(item).getItem() instanceof ItemArmor;
		return isHelmet(material) || isChestplate(material) || isLeggings(material) || isBoots(material);
	}

	public static boolean isHelmet(Material material) {
		List<Material> armorList = Arrays.asList(new Material[] {
			Material.LEATHER_HELMET,
			Material.CHAINMAIL_HELMET,
			Material.IRON_HELMET,
			Material.GOLDEN_HELMET,
			Material.DIAMOND_HELMET,
			Material.NETHERITE_HELMET,
			Material.TURTLE_HELMET
		});

		return armorList.contains(material);
	}

	public static boolean isChestplate(Material material) {
		List<Material> armorList = Arrays.asList(new Material[] {
			Material.LEATHER_CHESTPLATE,
			Material.CHAINMAIL_CHESTPLATE,
			Material.IRON_CHESTPLATE,
			Material.GOLDEN_CHESTPLATE,
			Material.DIAMOND_CHESTPLATE,
			Material.NETHERITE_CHESTPLATE
		});

		return armorList.contains(material);
	}

	public static boolean isLeggings(Material material) {
		List<Material> armorList = Arrays.asList(new Material[] {
			Material.LEATHER_LEGGINGS,
			Material.CHAINMAIL_LEGGINGS,
			Material.IRON_LEGGINGS,
			Material.GOLDEN_LEGGINGS,
			Material.DIAMOND_LEGGINGS,
			Material.NETHERITE_LEGGINGS
		});

		return armorList.contains(material);
	}

	public static boolean isBoots(Material material) {
		List<Material> armorList = Arrays.asList(new Material[] {
			Material.LEATHER_BOOTS,
			Material.CHAINMAIL_BOOTS,
			Material.IRON_BOOTS,
			Material.GOLDEN_BOOTS,
			Material.DIAMOND_BOOTS,
			Material.NETHERITE_BOOTS
		});

		return armorList.contains(material);
	}

	public static boolean isHoe(Material material) {
		List<Material> hoeList = Arrays.asList(new Material[] {
			Material.WOODEN_HOE,
			Material.STONE_HOE,
			Material.IRON_HOE,
			Material.GOLDEN_HOE,
			Material.DIAMOND_HOE,
			Material.NETHERITE_HOE
		});

		return hoeList.contains(material);
	}

	public static boolean isFlowerPlaceable(Material material) {
		List<Material> placeableList = Arrays.asList(new Material[] {
			Material.GRASS_BLOCK,
			Material.DIRT,
			Material.COARSE_DIRT,
			Material.PODZOL,
			Material.ROOTED_DIRT,
			Material.MOSS_BLOCK,
			Material.MYCELIUM,
			Material.FARMLAND
		});

		return placeableList.contains(material);
	}

	public static boolean isTwoBlocksTall(Material material) {
		List<Material> blockList = Arrays.asList(new Material[] {
			Material.SMALL_DRIPLEAF,
			Material.TALL_GRASS,
			Material.TALL_SEAGRASS
		});

		if (Tag.DOORS.isTagged(material)) return true;
		if (Tag.TALL_FLOWERS.isTagged(material)) return true;

		return blockList.contains(material);
	}

	public static boolean isGravityBlock(Material material) {
		List<Material> blockList = Arrays.asList(new Material[] {
			Material.SAND,
			Material.RED_SAND,
			Material.GRAVEL,
			Material.DRAGON_EGG
		});

		if (Tag.ANVIL.isTagged(material)) return true;
		if (isConcretePowder(material)) return true;

		return blockList.contains(material);
	}

	public static boolean isConcretePowder(Material material) {
		List<Material> concreteList = Arrays.asList(new Material[] {
			Material.BLACK_CONCRETE_POWDER,
			Material.BLUE_CONCRETE_POWDER,
			Material.BROWN_CONCRETE_POWDER,
			Material.CYAN_CONCRETE_POWDER,
			Material.GRAY_CONCRETE_POWDER,
			Material.GREEN_CONCRETE_POWDER,
			Material.LIGHT_BLUE_CONCRETE_POWDER,
			Material.LIGHT_GRAY_CONCRETE_POWDER,
			Material.LIME_CONCRETE_POWDER,
			Material.MAGENTA_CONCRETE_POWDER,
			Material.ORANGE_CONCRETE_POWDER,
			Material.PINK_CONCRETE_POWDER,
			Material.PURPLE_CONCRETE_POWDER,
			Material.RED_CONCRETE_POWDER,
			Material.WHITE_CONCRETE_POWDER,
			Material.YELLOW_CONCRETE_POWDER
		});

		return concreteList.contains(material);
	}

	public static boolean isStandingBanner(Material material) {
		List<Material> concreteList = Arrays.asList(new Material[] {
			Material.BLACK_BANNER,
			Material.BLUE_BANNER,
			Material.BROWN_BANNER,
			Material.CYAN_BANNER,
			Material.GRAY_BANNER,
			Material.GREEN_BANNER,
			Material.LIGHT_BLUE_BANNER,
			Material.LIGHT_GRAY_BANNER,
			Material.LIME_BANNER,
			Material.MAGENTA_BANNER,
			Material.ORANGE_BANNER,
			Material.PINK_BANNER,
			Material.PURPLE_BANNER,
			Material.RED_BANNER,
			Material.WHITE_BANNER,
			Material.YELLOW_BANNER
		});

		return concreteList.contains(material);
	}

	public static boolean isWallBanner(Material material) {
		List<Material> concreteList = Arrays.asList(new Material[] {
			Material.BLACK_WALL_BANNER,
			Material.BLUE_WALL_BANNER,
			Material.BROWN_WALL_BANNER,
			Material.CYAN_WALL_BANNER,
			Material.GRAY_WALL_BANNER,
			Material.GREEN_WALL_BANNER,
			Material.LIGHT_BLUE_WALL_BANNER,
			Material.LIGHT_GRAY_WALL_BANNER,
			Material.LIME_WALL_BANNER,
			Material.MAGENTA_WALL_BANNER,
			Material.ORANGE_WALL_BANNER,
			Material.PINK_WALL_BANNER,
			Material.PURPLE_WALL_BANNER,
			Material.RED_WALL_BANNER,
			Material.WHITE_WALL_BANNER,
			Material.YELLOW_WALL_BANNER
		});

		return concreteList.contains(material);
	}

	public static boolean isCoral(Material material) {
		List<Material> coralList = Arrays.asList(new Material[] {
			Material.BRAIN_CORAL,
			Material.BUBBLE_CORAL,
			Material.FIRE_CORAL,
			Material.HORN_CORAL,
			Material.TUBE_CORAL
		});

		return coralList.contains(material);
	}

	public static boolean isCoralFan(Material material) {
		List<Material> coralList = Arrays.asList(new Material[] {
			Material.BRAIN_CORAL_FAN,
			Material.BUBBLE_CORAL_FAN,
			Material.FIRE_CORAL_FAN,
			Material.HORN_CORAL_FAN,
			Material.TUBE_CORAL_FAN
		});

		return coralList.contains(material);
	}

	public static boolean isCoralWallFan(Material material) {
		List<Material> coralList = Arrays.asList(new Material[] {
			Material.BRAIN_CORAL_WALL_FAN,
			Material.BUBBLE_CORAL_WALL_FAN,
			Material.FIRE_CORAL_WALL_FAN,
			Material.HORN_CORAL_WALL_FAN,
			Material.TUBE_CORAL_WALL_FAN
		});

		return coralList.contains(material);
	}

	public static boolean isCoralBlock(Material material) {
		List<Material> coralList = Arrays.asList(new Material[] {
			Material.BRAIN_CORAL_BLOCK,
			Material.BUBBLE_CORAL_BLOCK,
			Material.FIRE_CORAL_BLOCK,
			Material.HORN_CORAL_BLOCK,
			Material.TUBE_CORAL_BLOCK
		});

		return coralList.contains(material);
	}

	public static boolean isDeadCoral(Material material) {
		List<Material> coralList = Arrays.asList(new Material[] {
			Material.DEAD_BRAIN_CORAL,
			Material.DEAD_BUBBLE_CORAL,
			Material.DEAD_FIRE_CORAL,
			Material.DEAD_HORN_CORAL,
			Material.DEAD_TUBE_CORAL
		});

		return coralList.contains(material);
	}

	public static boolean isDeadCoralFan(Material material) {
		List<Material> coralList = Arrays.asList(new Material[] {
			Material.DEAD_BRAIN_CORAL_FAN,
			Material.DEAD_BUBBLE_CORAL_FAN,
			Material.DEAD_FIRE_CORAL_FAN,
			Material.DEAD_HORN_CORAL_FAN,
			Material.DEAD_TUBE_CORAL_FAN
		});

		return coralList.contains(material);
	}

	public static boolean isDeadCoralWallFan(Material material) {
		List<Material> coralList = Arrays.asList(new Material[] {
			Material.DEAD_BRAIN_CORAL_WALL_FAN,
			Material.DEAD_BUBBLE_CORAL_WALL_FAN,
			Material.DEAD_FIRE_CORAL_WALL_FAN,
			Material.DEAD_HORN_CORAL_WALL_FAN,
			Material.DEAD_TUBE_CORAL_WALL_FAN
		});

		return coralList.contains(material);
	}

	public static boolean isDeadCoralBlock(Material material) {
		List<Material> coralList = Arrays.asList(new Material[] {
			Material.DEAD_BRAIN_CORAL_BLOCK,
			Material.DEAD_BUBBLE_CORAL_BLOCK,
			Material.DEAD_FIRE_CORAL_BLOCK,
			Material.DEAD_HORN_CORAL_BLOCK,
			Material.DEAD_TUBE_CORAL_BLOCK
		});

		return coralList.contains(material);
	}

	public static boolean isAmethystBud(Material material) {
		List<Material> amethystList = Arrays.asList(new Material[] {
			Material.SMALL_AMETHYST_BUD,
			Material.MEDIUM_AMETHYST_BUD,
			Material.LARGE_AMETHYST_BUD,
			Material.AMETHYST_CLUSTER
		});

		return amethystList.contains(material);
	}
	
	/*
	 *   Player Tags
	 */

	public static boolean isOnline(String username) {
		return Bukkit.getPlayer(username) != null;
	}

	public static boolean isOnline(UUID uuid) {
		return Bukkit.getPlayer(uuid) != null;
	}

	public static boolean isGrounded(Player player) { // TODO: retest accuracy
		if (player.isFlying() || player.isSwimming()) {
			return false;
		}

		Location locUnder = player.getLocation().clone().add(new Vector(0, -0.00001, 0));

		return !locUnder.getBlock().isPassable() || !(player.getFallDistance() > 0);
	}

	public static boolean hasEmptyHands(Player player) {
		return isEmptyHanded(player) && isEmptyOffHanded(player);
	}

	public static boolean isEmptyHanded(Player player) {
		return player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType().equals(Material.AIR);
	}

	public static boolean isEmptyOffHanded(Player player) {
		return player.getInventory().getItemInOffHand() == null || player.getInventory().getItemInOffHand().getType().equals(Material.AIR);
	}
	
	/*
	 *   Entity Tags
	 */

	public static boolean isHostile(Entity entity) {
		return Monster.class.isAssignableFrom(entity.getType().getEntityClass());
	}
	
	/*
	 *   Block Tags
	 */

	public static boolean isFullBlock(Block block) {
		BoundingBox boundingbox = block.getBoundingBox();

		return boundingbox.getWidthX() == 1 && boundingbox.getHeight() == 1 && boundingbox.getWidthZ() == 1;
	}

	public static boolean isWaterlogged(Block block) {
		if (block.getBlockData() instanceof Waterlogged) {
			 Waterlogged waterlog = (Waterlogged) block.getBlockData();
			 
			 return waterlog.isWaterlogged();
		}

		return false;
	}

	public static boolean hasWater(Block block) {
		List<Material> wetList = Arrays.asList(new Material[] {
			Material.WATER,
			Material.SEAGRASS,
			Material.TALL_SEAGRASS,
			Material.KELP
		});

		return wetList.contains(block.getType()) || isWaterlogged(block);
	}

	public static boolean canRain(Block block) {
		double temp = block.getTemperature();
		return temp > 0.15 && temp <= 0.95;
	}

	public static boolean canSnow(Block block) {
		return block.getTemperature() <= 0.15;
	}

	public static boolean isSupportedFromBelow(Block block) {
		Material material = block.getType();
		BlockData blockData = block.getBlockData();
		
		List<Material> blockList = Arrays.asList(new Material[] {
			Material.SMALL_DRIPLEAF,
			Material.TALL_GRASS,
			Material.TALL_SEAGRASS,
			Material.SNOW,
			Material.TORCH,
			Material.REDSTONE_TORCH,
			Material.SOUL_TORCH,
			Material.BIG_DRIPLEAF,
			Material.BIG_DRIPLEAF_STEM,
			Material.SWEET_BERRY_BUSH,
			Material.TWISTING_VINES_PLANT,
			Material.TWISTING_VINES,
			Material.MOSS_CARPET,
			Material.GRASS,
			Material.FERN,
			Material.LARGE_FERN,
			Material.BROWN_MUSHROOM,
			Material.RED_MUSHROOM,
			Material.SEA_PICKLE,
			Material.AZALEA,
			Material.FLOWERING_AZALEA,
			Material.NETHER_SPROUTS,
			Material.WARPED_FUNGUS,
			Material.WARPED_ROOTS,
			Material.CRIMSON_ROOTS,
			Material.CRIMSON_FUNGUS,
			Material.DEAD_BUSH,
			Material.BAMBOO,
			Material.BAMBOO_SAPLING,
			Material.SUGAR_CANE,
			Material.SEAGRASS,
			Material.KELP,
			Material.KELP_PLANT,
			Material.REDSTONE_WIRE,
			Material.COMPARATOR,
			Material.REPEATER,
			Material.CHORUS_FLOWER,
			Material.CAKE
		});

		if (Tag.DOORS.isTagged(material)) return true;
		if (Tag.TALL_FLOWERS.isTagged(material)) return true;
		if (Tag.SMALL_FLOWERS.isTagged(material)) return true;
		if (isGravityBlock(material)) return true;
		if (Tag.PRESSURE_PLATES.isTagged(material)) return true;
		if (Tag.WOOL_CARPETS.isTagged(material)) return true;
		if (Tag.STANDING_SIGNS.isTagged(material)) return true;
		if (Tag.CROPS.isTagged(material)) return true;
		if (Tag.RAILS.isTagged(material)) return true;
		if (Tag.CORAL_PLANTS.isTagged(material)) return true;
		if (isStandingBanner(material)) return true;
		
		if (blockData instanceof Hangable) {
			Hangable hangable = (Hangable) blockData;
			
			if (!hangable.isHanging()) return true;
		}
		
		if (blockData instanceof PointedDripstone) {
			PointedDripstone dripstone = (PointedDripstone) blockData;
			
			if (dripstone.getVerticalDirection().equals(BlockFace.UP)) return true;
		}
		
		if (blockData instanceof SculkVein) {
			SculkVein sculk = (SculkVein) blockData;
			
			if (sculk.hasFace(BlockFace.DOWN)) return true;
		}
		
		if (blockData instanceof Bell) {
			Bell bell = (Bell) blockData;
			
			if (bell.getAttachment().equals(Attachment.FLOOR)) return true;
		}
		
		if (blockData instanceof GlowLichen) {
			GlowLichen lichen = (GlowLichen) blockData;
			
			if (lichen.hasFace(BlockFace.DOWN)) return true;
		}
		
		if (blockData instanceof Scaffolding) {
			Scaffolding scaffolding = (Scaffolding) blockData;
			
			if (scaffolding.isBottom() == false) return true;
		}
		
		if (blockData instanceof Fire) {
			Fire fire = (Fire) blockData;
			
			if (!(
				fire.hasFace(BlockFace.UP) &&
				fire.hasFace(BlockFace.NORTH) &&
				fire.hasFace(BlockFace.EAST) &&
				fire.hasFace(BlockFace.SOUTH) &&
				fire.hasFace(BlockFace.WEST)
			)) return true;
		}
		
		if (
			Tag.BUTTONS.isTagged(material) ||
			material.equals(Material.LEVER) ||
			material.equals(Material.CHORUS_PLANT) ||
			isDeadCoral(material) ||
			isDeadCoralFan(material) ||
			isCoral(material) ||
			isCoralFan(material) ||
			isAmethystBud(material) ||
			Tag.SAPLINGS.isTagged(material)
		) {
			if (getAttachedFaces(block).get(BlockFace.DOWN)) return true;
		}

		return blockList.contains(material);
	}
	
	// TODO: make "is supported" for every direction, then make isSupportedByBlock which checks if one block is supported from another

	public static HashMap<BlockFace, Boolean> getAttachedFaces(Block block) {
		BoundingBox box = block.getBoundingBox();
		HashMap<BlockFace, Boolean> faces = new HashMap<BlockFace, Boolean>();

		faces.put(BlockFace.NORTH, box.getMinZ() % 1 == 0);
		faces.put(BlockFace.EAST, box.getMaxX() % 1 == 0);
		faces.put(BlockFace.SOUTH, box.getMaxZ() % 1 == 0);
		faces.put(BlockFace.WEST, box.getMinX() % 1 == 0);
		faces.put(BlockFace.UP, box.getMaxY() % 1 == 0);
		faces.put(BlockFace.DOWN, box.getMinY() % 1 == 0);
		
		return faces;
	}

	public static boolean isContainer(Block block) { // TODO: retest
		return block.getState() instanceof InventoryHolder;
	}
	
	/*
	 *   World Tags
	 */

	public static boolean isDay(World world) {
		long time = world.getTime();

		return time > 0 && time < 12300;
	}

	/*
	 *   UUID Tags
	 */

	public static boolean isUUID(String string) {
		try {
			UUID.fromString(string);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

	/*
	 *   String Tags
	 */

	public static boolean isLowerCase(String str) {
		return str.toLowerCase().equals(str);
	}

	public static boolean isUpperCase(String str) {
		return str.toUpperCase().equals(str);
	}
	
	public static boolean isNumeric(String string) {
		if (string == null) return false;

		try {
			Double.parseDouble(string);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}
}
