package dev.debutter.cuberry.paper.utils;

import com.destroystokyo.paper.ParticleBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.Color;

import java.util.Iterator;
import java.util.List;

public class TooManyParticles {

	public static void test() {
		ParticleBuilder effect = new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
				.data(new Particle.DustTransition(Color.fromRGB(0xffff00), Color.fromRGB(0x00ffff), 0));

		for (Player player : Bukkit.getOnlinePlayers()) {
			effect.location(player.getLocation());
			effect.receivers(player);
			effect.spawn();

			line(effect, player.getLocation().clone().add(3, 0, -1), player.getLocation().clone().add(1, 5, 3), 0.2);

			effect.spawn();
		}
	}

	public static void circle(ParticleBuilder effect, Location loc) {
//		particle.spawn(loc);
	}

	public static void line(ParticleBuilder effect, Location loc1, Location loc2, double space) {
		World world = loc1.getWorld();
		assert world != null;

		ParticleBuilder eff = cloneParticleBuilder(effect);
		Iterator<Vector> points = Caboodle.line(loc1.toVector(), loc2.toVector(), space);

		while (points.hasNext()) {
			Vector point = points.next();
			eff.location(world, point.getX(), point.getY(), point.getZ());
			eff.spawn();
		}
	}

	private static ParticleBuilder cloneParticleBuilder(ParticleBuilder effect) {
		ParticleBuilder clone = new ParticleBuilder(effect.particle());

		Location effectLoc = effect.location();
		List<Player> receivers = effect.receivers();

		if (receivers != null) clone.receivers(new ObjectArrayList<>(receivers));
		if (effectLoc != null) clone.location(effectLoc.clone());
		clone.source(effect.source());
		clone.count(effect.count());
		clone.offset(effect.offsetX(), effect.offsetY(), effect.offsetZ());
		clone.extra(effect.extra());
		clone.data(effect.data());
		clone.force(effect.force());

		return clone;
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
