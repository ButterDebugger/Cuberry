package com.butterycode.cubefruit.tweaks;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.AwesomeText;
import com.butterycode.cubefruit.utils.Caboodle;
import com.butterycode.cubefruit.utils.DogTags;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.*;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.*;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Snow;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class tweaks implements Listener {

	public static void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin(), new Runnable() {
			@Override
			public void run() {
				FileConfiguration config = Main.plugin().config();

				if (config.getBoolean("tweaks.snowpiles")) { // TODO: make slopey and more performant
					for (World world : Bukkit.getWorlds()) {
						if (!world.getEnvironment().equals(Environment.NORMAL)) continue;
						if (!world.hasStorm()) continue;

						for (Chunk chunk : world.getLoadedChunks()) {
							int minX = chunk.getX() * 16;
							int maxX = minX + 16;
							int minZ = chunk.getZ() * 16;
							int maxZ = minZ + 16;

							for (int x = minX; x < maxX; x++) {
								for (int z = minZ; z < maxZ; z++) {
									Block block = world.getHighestBlockAt(new Location(world, x, 0, z)).getLocation().add(0, 1, 0).getBlock();

									if (block.getType().equals(Material.SNOW) && DogTags.canSnow(block)) {
										if (Caboodle.chance(1) && Caboodle.chance(5)) {
											Snow snow = (Snow) block.getBlockData();

											snow.setLayers(Math.min(snow.getMaximumLayers(), snow.getLayers() + 1));

											block.setBlockData(snow);
										}
									}
								}
							}
						}
					}
				}

				for (Player player : Bukkit.getOnlinePlayers()) {
					if (config.getBoolean("tweaks.atmosphere")) {
						int height = player.getLocation().getBlockY();

						if (height >= player.getWorld().getMaxHeight() + 64) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 50, 0, false, false, false)); // TODO: change to freeze effect in 1.17+
						}
					}

					if (config.getBoolean("tweaks.lowgravity")) { // TODO: Could be better and can relate towards velocity instead of potion effects
						if (player.getWorld().getEnvironment().equals(Environment.THE_END)) { // TODO: Make related to specific worlds
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 50, 0, false, false, false));
							player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 50, 2, false, false, false));
						}
					}
				}
			}
		}, 0, 10);

		registerRecipes();
	}

	private static void registerRecipes() {
		FileConfiguration config = Main.plugin().config();

		if (config.getBoolean("tweaks.recipes.bundle")) {
			ItemStack bundleItem = new ItemStack(Material.BUNDLE);
			ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Main.plugin(), "bundle"), bundleItem);

			recipe.shape(
					"SRS",
					"R R",
					"RRR"
			);
			recipe.setIngredient('S', Material.STRING);
			recipe.setIngredient('R', Material.RABBIT_HIDE);

			try {
				Bukkit.getServer().addRecipe(recipe);
			} catch (IllegalStateException e) {
				Caboodle.log(Main.plugin(), e.getMessage(), Caboodle.LogType.WARN);
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		FileConfiguration config = Main.plugin().config();

		if (entity instanceof Player) {
			Player player = (Player) entity;
			Location location = player.getLocation();

			if (config.getBoolean("tweaks.blood")) {
				player.spawnParticle(Particle.REDSTONE, location.clone().add(new Vector(0, 1, 0)), 20, 0.25, 0.5, 0.25, new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1));
			}

			if (config.getBoolean("tweaks.damage-reveal.enabled")) {
				PotionEffect effect = player.getPotionEffect(PotionEffectType.INVISIBILITY);

				if (effect != null && config.getString("tweaks.damage-reveal.trigger").equalsIgnoreCase("all")) {
					player.removePotionEffect(PotionEffectType.INVISIBILITY);
				}
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		FileConfiguration config = Main.plugin().config();

		if (entity instanceof Player) {
			Player player = (Player) entity;

			if (config.getBoolean("tweaks.damage-reveal.enabled")) {
				PotionEffect effect = player.getPotionEffect(PotionEffectType.INVISIBILITY);

				if (effect != null && config.getString("tweaks.damage-reveal.trigger").equalsIgnoreCase("player")) {
					if (damager instanceof Projectile) {
						Projectile projectile = (Projectile) damager;
						ProjectileSource shooter = projectile.getShooter();

						if (shooter instanceof Player) {
							player.removePotionEffect(PotionEffectType.INVISIBILITY);
						}
					} else if (damager instanceof Player) {
						player.removePotionEffect(PotionEffectType.INVISIBILITY);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		FileConfiguration config = Main.plugin().config();

		if (config.getBoolean("tweaks.forcerespawn")) {
			Caboodle.respawn(player);
		}

		if (config.getBoolean("tweaks.ghost")) {
			player.setGameMode(GameMode.SPECTATOR);
			player.sendMessage(AwesomeText.colorize("&7You are now a ghost."));
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		FileConfiguration config = Main.plugin().config();

		if (config.getBoolean("tweaks.formatsign") && Caboodle.hasPermission(player, "tweaks.formatsigns")) {
			for (int l = 0; l < 4; l++) {
				String line = lines[ l ];
				line = AwesomeText.colorizeHex(line);
				event.setLine(l, line);
			}
		}
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		Block block = event.getBlock();
		Location blockLoc = block.getLocation();
		World world = blockLoc.getWorld();
		FileConfiguration config = Main.plugin().config();

		if (config.getBoolean("tweaks.dynamicportals.enabled")) { // TODO: fix issue with it not lighting all the time
			int maxBlocks = config.getInt("tweaks.dynamicportals.maxblocks");

			if (blockLoc.clone().add(0, -1, 0).getBlock().getType().equals(Material.OBSIDIAN)) {
				// x-axis portal

				ArrayList<Block> portalBlocks = new ArrayList<>();
				long failSafe = System.currentTimeMillis() + 1000;
				int validFlag = 0;

				portalBlocks.add(block);

				while (portalBlocks.size() < maxBlocks && System.currentTimeMillis() < failSafe) {
					ArrayList<Block> newBlocks = new ArrayList<>();

					for (Block pblock : portalBlocks) {
						Block bpx = world.getBlockAt(pblock.getX() + 1, pblock.getY(), pblock.getZ());
						Block bnx = world.getBlockAt(pblock.getX() - 1, pblock.getY(), pblock.getZ());
						Block bpy = world.getBlockAt(pblock.getX(), pblock.getY() + 1, pblock.getZ());
						Block bny = world.getBlockAt(pblock.getX(), pblock.getY() - 1, pblock.getZ());

						if (bpx.isEmpty() && !bny.equals(block) && !portalBlocks.contains(bpx) && !newBlocks.contains(bpx)) {
							newBlocks.add(bpx);
						}
						if (bnx.isEmpty() && !bny.equals(block) && !portalBlocks.contains(bnx) && !newBlocks.contains(bnx)) {
							newBlocks.add(bnx);
						}
						if (bpy.isEmpty() && !bny.equals(block) && !portalBlocks.contains(bpy) && !newBlocks.contains(bpy)) {
							newBlocks.add(bpy);
						}
						if (bny.isEmpty() && !bny.equals(block) && !portalBlocks.contains(bny) && !newBlocks.contains(bny)) {
							newBlocks.add(bny);
						}
					}

					portalBlocks.addAll(newBlocks);
					if (newBlocks.size() == 0) {
						validFlag++;
					}
					if (newBlocks.size() == 0 || portalBlocks.size() >= maxBlocks) {
						break;
					}
				}

				for (Block pblock : portalBlocks) {
					Block bpx = world.getBlockAt(pblock.getX() + 1, pblock.getY(), pblock.getZ());
					Block bnx = world.getBlockAt(pblock.getX() - 1, pblock.getY(), pblock.getZ());
					Block bpy = world.getBlockAt(pblock.getX(), pblock.getY() + 1, pblock.getZ());
					Block bny = world.getBlockAt(pblock.getX(), pblock.getY() - 1, pblock.getZ());

					if (!portalBlocks.contains(bpx) && !bpx.getType().equals(Material.OBSIDIAN)) {
						validFlag++;
					}
					if (!portalBlocks.contains(bnx) && !bnx.getType().equals(Material.OBSIDIAN)) {
						validFlag++;
					}
					if (!portalBlocks.contains(bpy) && !bpy.getType().equals(Material.OBSIDIAN)) {
						validFlag++;
					}
					if (!portalBlocks.contains(bny) && !bny.getType().equals(Material.OBSIDIAN)) {
						validFlag++;
					}
				}

				if (validFlag == 1) {
					event.setCancelled(true);
					ArrayList<Block> newPortalBlocks = portalBlocks;
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin(), () -> {
						for (Block pblock : newPortalBlocks) {
							pblock.setType(Material.NETHER_PORTAL);
						}
					}, 1L);
					return;
				}

				// z-axis portal

				portalBlocks = new ArrayList<>();
				failSafe = System.currentTimeMillis() + 1000;
				validFlag = 0;

				portalBlocks.add(block);

				while (portalBlocks.size() < maxBlocks && System.currentTimeMillis() < failSafe) {
					ArrayList<Block> newBlocks = new ArrayList<>();

					for (Block pblock : portalBlocks) {
						Block bpz = world.getBlockAt(pblock.getX(), pblock.getY(), pblock.getZ() + 1);
						Block bnz = world.getBlockAt(pblock.getX(), pblock.getY(), pblock.getZ() - 1);
						Block bpy = world.getBlockAt(pblock.getX(), pblock.getY() + 1, pblock.getZ());
						Block bny = world.getBlockAt(pblock.getX(), pblock.getY() - 1, pblock.getZ());

						if (bpz.isEmpty() && !bny.equals(block) && !portalBlocks.contains(bpz) && !newBlocks.contains(bpz)) {
							newBlocks.add(bpz);
						}
						if (bnz.isEmpty() && !bny.equals(block) && !portalBlocks.contains(bnz) && !newBlocks.contains(bnz)) {
							newBlocks.add(bnz);
						}
						if (bpy.isEmpty() && !bny.equals(block) && !portalBlocks.contains(bpy) && !newBlocks.contains(bpy)) {
							newBlocks.add(bpy);
						}
						if (bny.isEmpty() && !bny.equals(block) && !portalBlocks.contains(bny) && !newBlocks.contains(bny)) {
							newBlocks.add(bny);
						}
					}

					portalBlocks.addAll(newBlocks);
					if (newBlocks.size() == 0) {
						validFlag++;
					}
					if (newBlocks.size() == 0 || portalBlocks.size() >= maxBlocks) {
						break;
					}
				}

				for (Block pblock : portalBlocks) {
					Block bpz = world.getBlockAt(pblock.getX(), pblock.getY(), pblock.getZ() + 1);
					Block bnz = world.getBlockAt(pblock.getX(), pblock.getY(), pblock.getZ() - 1);
					Block bpy = world.getBlockAt(pblock.getX(), pblock.getY() + 1, pblock.getZ());
					Block bny = world.getBlockAt(pblock.getX(), pblock.getY() - 1, pblock.getZ());

					if (!portalBlocks.contains(bpz) && !bpz.getType().equals(Material.OBSIDIAN)) {
						validFlag++;
					}
					if (!portalBlocks.contains(bnz) && !bnz.getType().equals(Material.OBSIDIAN)) {
						validFlag++;
					}
					if (!portalBlocks.contains(bpy) && !bpy.getType().equals(Material.OBSIDIAN)) {
						validFlag++;
					}
					if (!portalBlocks.contains(bny) && !bny.getType().equals(Material.OBSIDIAN)) {
						validFlag++;
					}
				}

				if (validFlag == 1) {
					event.setCancelled(true);
					ArrayList<Block> newPortalBlocks = portalBlocks;
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin(), () -> {
						for (Block pblock : newPortalBlocks) {
							pblock.setType(Material.NETHER_PORTAL);
							BlockData bd = pblock.getBlockData();
							Orientable orientable = (Orientable) bd;
							orientable.setAxis(Axis.Z);
							pblock.setBlockData(orientable);
						}
					}, 1L);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		FileConfiguration config = Main.plugin().config();

		if (config.getBoolean("tweaks.fix-creeper-block-drops")) {
			for (Block block : event.blockList()) {
				if (block.getType().equals(Material.TNT)) continue;

				block.breakNaturally();
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		FileConfiguration config = Main.plugin().config();

		if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
			Block block = event.getClickedBlock();

			if (config.getBoolean("tweaks.bucket-obsidian")) {
				// FIXME: if obsidian is underwater, bucket will pickup water and delete the obsidian
				if (block.getType().equals(Material.OBSIDIAN) && event.hasItem() && item.getType().equals(Material.BUCKET)) {
					Bukkit.getScheduler().runTaskLater(Main.plugin(), () -> {
						block.setType(Material.AIR);
						item.setType(Material.LAVA_BUCKET);
						player.playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL_LAVA, 0.5f, 1f);
					}, 0);
				}
			}
		}

		if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
			if (config.getBoolean("tweaks.armor-swap.enabled")) {
				if (!config.getBoolean("tweaks.armor-swap.crouch-cancel") || (config.getBoolean("tweaks.armor-swap.crouch-cancel") && !player.isSneaking())) { // TODO: test
					if (event.hasItem() && DogTags.isWearable(item.getType())) { // TODO: test
						EquipmentSlot slot = DogTags.getEquipmentSlot(item.getType());

						if (slot.equals(EquipmentSlot.HEAD)) {
							ItemStack olditem = player.getInventory().getHelmet();

							if (olditem != null) {
								player.getInventory().setHelmet(item);

								int index = Caboodle.getItemIndex(player.getInventory(), item);
								player.getInventory().setItem(index, olditem);

								player.playSound(player, Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.5f, 1f);

								event.setCancelled(true);
							}
						} else if (slot.equals(EquipmentSlot.CHEST)) {
							ItemStack olditem = player.getInventory().getChestplate();

							if (olditem != null) {
								player.getInventory().setChestplate(item);

								int index = Caboodle.getItemIndex(player.getInventory(), item);
								player.getInventory().setItem(index, olditem);

								player.playSound(player, Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.5f, 1f);

								event.setCancelled(true);
							}
						} else if (slot.equals(EquipmentSlot.LEGS)) {
							ItemStack olditem = player.getInventory().getLeggings();

							if (olditem != null) {
								player.getInventory().setLeggings(item);

								int index = Caboodle.getItemIndex(player.getInventory(), item);
								player.getInventory().setItem(index, olditem);

								player.playSound(player, Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.5f, 1f);

								event.setCancelled(true);
							}
						} else if (slot.equals(EquipmentSlot.FEET)) {
							ItemStack olditem = player.getInventory().getBoots();

							if (olditem != null) {
								player.getInventory().setBoots(item);

								int index = Caboodle.getItemIndex(player.getInventory(), item);
								player.getInventory().setItem(index, olditem);

								player.playSound(player, Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.5f, 1f);

								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		FileConfiguration config = Main.plugin().config();

		if (config.getBoolean("tweaks.shear-item-frames")) {
			if (item.getType().equals(Material.SHEARS)) {
				if (entity instanceof ItemFrame) {
					ItemFrame itemframe = (ItemFrame) entity;

					if (!itemframe.getItem().getType().equals(Material.AIR) && itemframe.isVisible()) {
						if (!player.getGameMode().equals(GameMode.CREATIVE)) {
							item.setDurability((short) (item.getDurability() + 1));
							if (item.getDurability() > item.getType().getMaxDurability()) {
								player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
								player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 1f);
							}
						}

						entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_SHEEP_SHEAR, 0.5f, 1f);

						itemframe.setVisible(false);
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		FileConfiguration config = Main.plugin().config();

		if (config.getBoolean("tweaks.quickharvest")) {
			if (DogTags.isHoe(player.getInventory().getItemInMainHand().getType())) {
				BlockData blockdata = block.getBlockData();

				if (Tag.CROPS.getValues().contains(block.getType()) && blockdata instanceof Ageable) {
					Ageable age = (Ageable) blockdata;
					Material oldmat = block.getType();

					if (age.getAge() == age.getMaximumAge()) { // TODO: remove one seed from the drops
						Bukkit.getScheduler().runTaskLater(Main.plugin(), () -> {
							block.setType(oldmat);
						}, 0);
					}
				}
			}
		}
	}

	@EventHandler
	public void onLightningStrike(LightningStrikeEvent event) {
		Location loc = event.getLightning().getLocation();
		FileConfiguration config = Main.plugin().config();

		if (config.getBoolean("tweaks.petrified-lightning.sand") || config.getBoolean("tweaks.petrified-lightning.oakslab")) {
			// TODO: rework this entirely
			Location baseloc = loc.clone();

			if (baseloc.clone().add(0, -1, 0).getBlock().getType().equals(Material.LIGHTNING_ROD)) { // Check if a lighting rod was struck
				baseloc = baseloc.add(0, -1, 0);
			}

			baseloc = baseloc.add(-0.5, 0, -0.5); // Adjust location

			for (Player p : Bukkit.getOnlinePlayers()) {
				Caboodle.dustParticle(p, baseloc, 10, new Vector(0, 0, 0), Color.RED, 1);
			}

			List<Block> blocks = new ArrayList<>();
			blocks.add(baseloc.clone().add(0, -1, 0).getBlock());
			blocks.add(baseloc.clone().add(0, -2, 0).getBlock());
			blocks.add(baseloc.clone().add(1, -1, 0).getBlock());
			blocks.add(baseloc.clone().add(-1, -1, 0).getBlock());
			blocks.add(baseloc.clone().add(0, -1, 1).getBlock());
			blocks.add(baseloc.clone().add(0, -1, -1).getBlock());

			for (Block b : blocks) {
				if (config.getBoolean("tweaks.petrified-lightning.sand")) {
					if (b.getType().equals(Material.SAND) || b.getType().equals(Material.RED_SAND)) {
						if (Caboodle.chance(1 / b.getLocation().distance(baseloc) * 100 - 15)) {
							b.setType(Material.GLASS);
						}
					}
				}

				if (config.getBoolean("tweaks.petrified-lightning.oakslab")) {
					if (b.getType().equals(Material.OAK_SLAB)) {
						Slab oldslab = (Slab) b.getBlockData();
//
						if (Caboodle.chance(1 / b.getLocation().distance(baseloc) * 100 - 15)) {
							b.setType(Material.PETRIFIED_OAK_SLAB);
							Slab slab = (Slab) b.getBlockData();
							slab.setType(oldslab.getType());
							b.setBlockData(slab);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockDispense(BlockDispenseEvent event) {
		// TODO:
		//  fix everything
		//  make sure the right item gets removed from the dispenser
		Block block = event.getBlock();
		ItemStack item = event.getItem();
		FileConfiguration config = Main.plugin().config();

		if (!block.getType().equals(Material.DISPENSER)) return; // Cancel if block isn't a dispenser

		Block frontblock = Caboodle.getBlockOnFace(block);
		Material oldType = frontblock.getType();
		BlockFace face = ((Directional) block.getBlockData()).getFacing();

		if (config.getBoolean("tweaks.dispense-everything.enabled")) {
			boolean customdispense = false;

			if (frontblock.isEmpty() || frontblock.getType().equals(Material.WATER)) {
				if (item.getType().equals(Material.TNT)) {
					return;
				}

				if (item.getType().isBlock() && item.getType().isSolid()) { // Solid blocks
					frontblock.setType(item.getType());

					customdispense = true;

					if (Tag.SIGNS.isTagged(item.getType()) || Tag.PRESSURE_PLATES.isTagged(item.getType()) || Tag.DOORS.isTagged(item.getType()) || Tag.BANNERS.isTagged(item.getType()) || Tag.WOOL_CARPETS.isTagged(item.getType()) || item.getType().equals(Material.BELL)) {
						Block bottomblock = frontblock.getRelative(BlockFace.DOWN);

						if (Tag.DOORS.isTagged(item.getType()) && face.equals(BlockFace.DOWN)) {
							bottomblock = frontblock.getRelative(BlockFace.DOWN, 2);
						}

						if (!(bottomblock.getType().isBlock() && bottomblock.getType().isSolid() && DogTags.isFullBlock(bottomblock))) {
							frontblock.setType(oldType);
							customdispense = false;
							return;
						}
					}

					if (DogTags.isWaterlogged(frontblock.getLocation().getBlock()) || (frontblock.getBlockData() instanceof Container)) {
//						Container container = (Container) newblockdata;

						frontblock.setType(oldType);
						customdispense = false;
						return;
					}

					if (frontblock.getBlockData() instanceof Directional) {
						Directional direction = (Directional) frontblock.getBlockData();

						BlockFace newface = face;
						if (!direction.getFaces().contains(face)) {
							newface = BlockFace.SOUTH;
						}

						direction.setFacing(newface);
						frontblock.setBlockData(direction);

						if (frontblock.getBlockData() instanceof Bed) {
							Block bedhead = frontblock.getRelative(newface);

							if (!(bedhead.isEmpty() || bedhead.getType().equals(Material.WATER))) {
								frontblock.setType(oldType);
								customdispense = false;
								return;
							}

							BlockState headstate = bedhead.getState();
							headstate.setType(item.getType());
							Bed head = (Bed) headstate.getBlockData();
							head.setFacing(newface);
							head.setPart(Part.HEAD);
							headstate.setBlockData(head);
							headstate.update(true);

							Bed foot = (Bed) frontblock.getBlockData();
							foot.setFacing(newface);
							foot.setPart(Part.FOOT);
							frontblock.setBlockData(foot);
						}
					}

					if (frontblock.getBlockData() instanceof Bisected) {
						if (frontblock.getBlockData() instanceof Door) {
							if (face.equals(BlockFace.DOWN)) {
								Block bottomblock = frontblock.getRelative(BlockFace.DOWN);

								if (!(bottomblock.isEmpty() || bottomblock.getType().equals(Material.WATER))) {
									frontblock.setType(oldType);
									customdispense = false;
									return;
								}

								Door top = (Door) frontblock.getBlockData();
								top.setHalf(Half.TOP);

								BlockFace newface = face;
								if (!top.getFaces().contains(face)) {
									newface = BlockFace.SOUTH;
								}

								top.setFacing(newface);
								frontblock.setBlockData(top);

								bottomblock.setType(item.getType());
								Door bottom = (Door) bottomblock.getBlockData();
								bottom.setHalf(Half.BOTTOM);
								bottom.setFacing(newface);
								bottomblock.setBlockData(bottom);
							} else {
								Block topblock = frontblock.getRelative(BlockFace.UP);

								if (!(topblock.isEmpty() || topblock.getType().equals(Material.WATER))) {
									frontblock.setType(oldType);
									customdispense = false;
									return;
								}

								Door top = (Door) frontblock.getBlockData();
								top.setHalf(Half.TOP);

								BlockFace newface = face;
								if (!top.getFaces().contains(face)) {
									newface = BlockFace.SOUTH;
								}

								top.setFacing(newface);
								topblock.setBlockData(top);
							}
						}
					}
				}
			}

			if (customdispense) {
				event.setCancelled(true);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin(), new Runnable() {
					@Override
					public void run() {
						Dispenser dispenser = (Dispenser) block.getState();
						if (dispenser.getInventory().containsAtLeast(item, 1)) {
							dispenser.getInventory().removeItem(item);
						}
					}
				}, 0);

				block.getWorld().playSound(block.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5f, 1f);
			}
		}
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		Entity entity = event.getEntity();
		EntityType type = event.getEntityType();
		FileConfiguration config = Main.plugin().config();

		if (type.equals(EntityType.POLAR_BEAR)) { // FIXME: prevent baby polar bears
			if (config.getBoolean("tweaks.polarbearjokey.enabled")) {
				if (Caboodle.chance(config.getDouble("tweaks.polarbearjokey.chance"))) {
					Snowman snowgolem = (Snowman) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.SNOWMAN);

					entity.addPassenger(snowgolem);
				}
			}
		}

		if (type.equals(EntityType.SLIME)) {
			Slime slime = (Slime) entity;

			if (config.getBoolean("tweaks.slimestack.enabled")) { // TODO: make max size work
				if (Caboodle.chance(config.getDouble("tweaks.slimestack.chance"))) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin(), new Runnable() {
						@Override
						public void run() {
							if (!entity.isDead()) {
								Slime stackslime = (Slime) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.SLIME);

								int size = Math.max(1, Caboodle.randomInteger(slime.getSize() - 1, slime.getSize()));
								stackslime.setSize(size);

								entity.addPassenger(stackslime);
								entity.getWorld().playSound(stackslime.getLocation(), Sound.ENTITY_SLIME_SQUISH, 0.5f, 1f);
								entity.getWorld().spawnParticle(Particle.SLIME, stackslime.getLocation(), 10, size / 2, size / 2, size / 2);
							}
						}
					}, 10);
				}
			}
		}
	}
}
