package dev.debutter.cubefruit.paper.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Iterator;

public class TooManyParticles {

	/*class ParticleConfig {
		private Particle type;
		private int count = 1;
		private Vector offset = new Vector(0, 0, 0);
		private DustOptions dustOptions;
		private DustTransition dustTransition;

		ParticleConfig(Particle type) {
			this.setType(type);
		}
		ParticleConfig(Particle type, int count) {
			this.setType(type);
			this.setCount(count);
		}
		ParticleConfig(Particle type, int count, Vector offset) {
			this.setType(type);
			this.setCount(count);
			this.setOffset(offset);
		}

		public Particle getType() {
			return type;
		}
		public void setType(Particle type) {
			this.type = type;
		}

		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}

		public Vector getOffset() {
			return offset;
		}
		public void setOffset(Vector offset) {
			this.offset = offset;
		}

		public DustOptions getDustOptions() {
			return dustOptions;
		}
		public void setDustOptions(Color color, float size) {
			dustOptions = new Particle.DustOptions(color, size);
		}

		public DustTransition getDustTransition() {
			return dustTransition;
		}
		public void setDustTransition(Color fromColor, Color toColor, float size) {
			dustTransition = new Particle.DustTransition(fromColor, toColor, size);
		}

		void spawn(Location loc) {
//			player.spawnParticle(Particle.REDSTONE, location, amount, offset.getX(), offset.getY(), offset.getZ(), new Particle.DustOptions(color, size));
//			player.spawnParticle(Particle.TOWN_AURA, block.getX() + 0.5, block.getY(), block.getZ(), 1, 0.25, 0, 0);
		}
	}*/
	
	public static class ParticleConfig {
		private Particle type;
		private int count = 1;
		private Vector offset = new Vector(0, 0, 0);
		private double speed = 0;
		
		ParticleConfig(Particle type) {
			setType(type);
		}

		public Particle getType() {
			return type;
		}
		public void setType(Particle type) {
			this.type = type;
		}
		
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}

		public Vector getOffset() {
			return offset;
		}
		public void setOffset(Vector offset) {
			this.offset = offset;
		}

		public double getSpeed() {
			return speed;
		}
		public void setSpeed(double speed) {
			this.speed = speed;
		}

		void spawn(Location loc) {
			World world = loc.getWorld();
			
			world.spawnParticle(type, loc, count, offset.getX(), offset.getY(), offset.getZ(), speed);

//			world.spawnParticle(Particle.REDSTONE, location, amount, offset.getX(), offset.getY(), offset.getZ(), new Particle.DustOptions(color, size));
//			world.spawnParticle(Particle.TOWN_AURA, block.getX() + 0.5, block.getY(), block.getZ(), 1, 0.25, 0, 0);
		}
		void spawn(Player player, Location loc) {
			player.spawnParticle(type, loc, count, offset.getX(), offset.getY(), offset.getZ(), speed);

//			player.spawnParticle(Particle.REDSTONE, location, amount, offset.getX(), offset.getY(), offset.getZ(), new Particle.DustOptions(color, size));
//			player.spawnParticle(Particle.TOWN_AURA, block.getX() + 0.5, block.getY(), block.getZ(), 1, 0.25, 0, 0);
		}
	}

	public static void test() {
		ParticleConfig particle = new ParticleConfig(Particle.CLOUD);
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			particle.spawn(player, player.getLocation());
			
			line(player.getLocation(), player.getLocation().clone().add(1, 5, 3), 0.2);
		}
	}

	/*public static void circle(ParticleConfig particle, Location loc) {
		particle.spawn(loc);
	}*/

	public static void line(Location loc1, Location loc2, double space) {
		World world = loc1.getWorld();
		assert world != null;

		Iterator<Vector> points = Caboodle.line(loc1.toVector(), loc2.toVector(), space);

		while (points.hasNext()) {
			Vector point = points.next();
			world.spawnParticle(Particle.END_ROD, point.getX(), point.getY(), point.getZ(), 0);
		}
	}











	// I have no idea what any of this does but it *should* create a fake lightning bolt

	/*public static void lightning(Player p, Location l) {
		Class<?> light = getNMSClass("EntityLightning");
		try {
			Constructor<?> constu = light .getConstructor(getNMSClass("World"), double.class, double.class, double.class, boolean.class, boolean.class);
			Object wh = p.getWorld().getClass().getMethod("getHandle").invoke(p.getWorld());
			Object lighobj = constu.newInstance(wh, l.getX(), l.getY(), l.getZ(), false, false);

			Object obj = getNMSClass("PacketPlayOutSpawnEntityWeather").getConstructor(getNMSClass("Entity")).newInstance(lighobj);

			sendPacket(p, obj);
			p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 100, 1);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public static Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	 public static void sendPacket(Player player, Object packet) {
		 try {
			 Object handle = player.getClass().getMethod("getHandle").invoke(player);
			 Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			 playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"))
			 .invoke(playerConnection, packet);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }*/
}
