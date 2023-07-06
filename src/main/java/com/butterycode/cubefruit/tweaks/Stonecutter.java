package com.butterycode.cubefruit.tweaks;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.Caboodle;
import com.butterycode.cubefruit.utils.ResourceStorage;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stonecutter implements Listener {

	public static void start() {
		HashMap<ItemStack, List<ItemStack>> recipes = readStonecutterData();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin(), new Runnable() {
			@Override
			public void run() {
				FileConfiguration config = Main.plugin().config();

				for (World world : Bukkit.getWorlds()) {
					for (Entity entity : world.getEntities()) {
						if (entity instanceof LivingEntity) {
							LivingEntity livingEntity = (LivingEntity) entity;
							Location livingEntityLoc = livingEntity.getLocation();

							if (livingEntityLoc.getBlock().getType().equals(Material.STONECUTTER)) {
								livingEntity.damage(config.getInt("tweaks.stonecutterhurt.damage"));
							}
						} else if (entity instanceof Item) {
							Item item = (Item) entity;
							ItemStack itemStack = item.getItemStack();
							Material itemType = itemStack.getType();
							ItemMeta itemMeta = itemStack.getItemMeta();
							Location itemLoc = entity.getLocation();

							if (itemLoc.getBlock().getType().equals(Material.STONECUTTER)) {
								String option = config.getString("tweaks.stonecutterhurt.items");

								if (option.equals("delete")) {
									item.remove();
								} else if (option.equals("cut")) {
									if (itemMeta.equals(new ItemStack(itemType).getItemMeta())) {
										for (ItemStack input : recipes.keySet()) {
											if (!input.getType().equals(itemType)) continue;
											
											int amount = input.getAmount();
											int extra = (int) Math.floor(itemStack.getAmount() / amount);
											int remainder = itemStack.getAmount() % amount;
											
											List<ItemStack> output = recipes.get(input);

											if (extra > 0) {
												for (ItemStack ingredient : output) {
													for (int i = 0; i < extra; i++) {
														if (ingredient != null) {
															world.dropItem(itemLoc, ingredient);
														}
													}
												}

												world.playSound(itemLoc, Sound.UI_STONECUTTER_TAKE_RESULT, 0.25f, 1);
												
												if (remainder == 0) {
													item.remove();
												} else {
													itemStack.setAmount(remainder);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}, 0, 10);
	}

	private static HashMap<ItemStack, List<ItemStack>> readStonecutterData() {
		ResourceStorage resource = new ResourceStorage(Main.plugin(), "stonecutter.txt", false);
		String content = resource.getContent();
		
		HashMap<ItemStack, List<ItemStack>> stonecutterMap = new HashMap<ItemStack, List<ItemStack>>();
		
		for (String data : content.split("\n")) {
			try {
				if (data.startsWith("!")) continue;
				
				Pattern matRegex = Pattern.compile("([a-zA-Z0-9_]+)\\*(\\d+)");
				
				String[] lineArgs = data.split(";");
				if (lineArgs.length != 2) {
					continue;
				}
				
				// Get input item
				Matcher inputArgs = matRegex.matcher(lineArgs[0]);
				if (!inputArgs.find()) {
					continue;
				}

				Material inmat = Caboodle.getMaterialByName(inputArgs.group(1));
				ItemStack input = new ItemStack(inmat, Integer.parseInt(inputArgs.group(2)));
				
				// Get output items
				List<ItemStack> output = new ArrayList<ItemStack>();
				
				String[] outputArgs = lineArgs[1].split(",");
				for (String out : outputArgs) {
					Matcher outArgs = matRegex.matcher(out);
					if (!outArgs.find()) {
						continue;
					}
					
					Material outmat = Caboodle.getMaterialByName(outArgs.group(1));
					output.add(new ItemStack(outmat, Integer.parseInt(outArgs.group(2))));
				}
				
				if (output.size() != outputArgs.length) {
					continue;
				}
				
				// Add recipe
				stonecutterMap.put(input, output);
			} catch (IllegalArgumentException e) {
				continue;
			}
		}
		
		return stonecutterMap;
	}
	
}
