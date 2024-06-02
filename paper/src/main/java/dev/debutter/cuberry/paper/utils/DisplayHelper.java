package dev.debutter.cuberry.paper.utils;

import dev.debutter.cuberry.paper.Paper;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class DisplayHelper {

    private static Map<String, GhostBlock> GhostBlocks = new HashMap<>();

    public static class GhostBlock {
        private Player player;
        private Location location;
        private BlockData blockdata;
        private BukkitTask task;
        private String key;
        private long duration = -1;
        private long recursion = -1;
        private long radius = -1; // TODO: default set to server render distance
        private boolean removed = false;
        private boolean created = false;

        public GhostBlock(Player p, Location loc, BlockData bd) {
            player = p;
            location = loc;
            key = Caboodle.stringifyLocation(loc) + "&" + p.getUniqueId();
            blockdata = bd;
        }

        public Player getPlayer() {
            return player;
        }

        public Location getLocation() {
            return location;
        }

        public BlockData getBlockdata() {
            return blockdata;
        }

        public String getKey() {
            return key;
        }

        public boolean isCreated() {
            return created;
        }

        public boolean isRemoved() {
            return removed;
        }

        public void setDuration(long time) {
            duration = Math.max(-1, time);
        }

        public long getDuration() {
            return duration;
        }

        public void setRecursion(long time) {
            recursion = Math.max(-1, time);
        }

        public long getRecursion() {
            return recursion;
        }

        public void setRadius(long distance) {
            duration = Math.max(-1, distance);
        }

        public long getRadius() {
            return radius;
        }

        public void create() {
            if (created) return; // Prevent double creation
            created = true;

            if (GhostBlocks.get(key) != null) { // Check if ghost block already exists
                GhostBlock other = GhostBlocks.get(key);

                if (blockdata.equals(other.getBlockdata())) { // Ghost blocks are identical
                    other.setDuration(duration);
                    other.setRecursion(recursion);
                    return;
                }
            }

            GhostBlocks.put(key, this);
            blink();

            task = new BukkitRunnable() {
                long recursionPeriod = recursion;

                @Override
                public void run() {
                    if (duration != -1) {
                        if (duration > 0) {
                            duration--;
                        } else {
                            player.sendBlockChange(location, location.getBlock().getBlockData());
                            remove();
                            return;
                        }
                    }

                    if (recursion != -1) {
                        if (recursionPeriod > 0) {
                            recursionPeriod--;
                        } else {
                            blink();
                            recursionPeriod = recursion;
                        }
                    }
                }
            }.runTaskTimer(Paper.plugin(), 0L, 1L);
        }

        public void remove() {
            task.cancel();
            GhostBlocks.remove(key);
            created = true; // Prevent later creation
            removed = true;
        }

        public void blink() {
            if (removed || !created) return;

            if (radius == -1) {
                player.sendBlockChange(location, blockdata);
            } else {
                if (location.distance(player.getLocation()) <= radius) {
                    player.sendBlockChange(location, blockdata);
                }
            }
        }
    }

}
