package com.butterycode.cubefruit.tweaks;

import com.butterycode.cubefruit.Main;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Listener;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class NetherFlambe implements Listener {

	private static HashMap<UUID, Integer> negativeAge = new HashMap<UUID, Integer>();
	
	public static void start() {
		negativeAge.clear();
		
		for (World world : Bukkit.getWorlds()) {
			if (!world.getEnvironment().equals(Environment.NETHER)) continue;
			
			for (Entity entity : world.getEntities()) {
				if (!(entity instanceof Item)) continue;

				negativeAge.put(entity.getUniqueId(), getItemAge(entity));
			}
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin(), new Runnable() {
			@Override
			public void run() {
				FileConfiguration config = Main.plugin().config();

				for (World world : Bukkit.getWorlds()) {
					if (!world.getEnvironment().equals(Environment.NETHER)) continue;
					
					for (Entity entity : world.getEntities()) {
						if (entity instanceof Item) {
							Item item = (Item) entity;
							ItemStack itemStack = item.getItemStack();
							Material itemType = itemStack.getType();
							ItemMeta itemMeta = itemStack.getItemMeta();
							Location itemLoc = entity.getLocation();
							
							if (!itemMeta.equals(new ItemStack(itemType).getItemMeta())) {
								continue;
							}
							
							int cookTime = getBetterItemAge(entity);
							int timeNeeded = (int) (config.getDouble("tweaks.nether-flambe.time") * 20) * itemStack.getAmount();
							
							if (cookTime >= timeNeeded) {
								Iterator<Recipe> iter = Bukkit.recipeIterator();
								while (iter.hasNext()) {
									Recipe recipe = iter.next();
									
									if (recipe instanceof FurnaceRecipe) {
										FurnaceRecipe furnace = (FurnaceRecipe) recipe;
										ItemStack result = furnace.getResult();
										
										if (!furnace.getInput().getType().equals(itemType)) {
											continue;
										}
										
										for (int i = 0; i < itemStack.getAmount(); i++) {
											world.dropItem(itemLoc, result);
										}
										
										item.remove();
										world.playSound(itemLoc, Sound.ITEM_FIRECHARGE_USE, 0.25f, 1f);
									}
								}
							}
						}
					}
				}
			}
		}, 0, 10);
	}

	private static int getItemAge(Entity entity) {
		return entity.getTicksLived();
	}

	private static int getBetterItemAge(Entity entity) {
		int neg = 0;
		UUID id = entity.getUniqueId();
		
		if (negativeAge.containsKey(id)) {
			neg = negativeAge.get(id);
		}
		
		return getItemAge(entity) - neg;
	}
}
