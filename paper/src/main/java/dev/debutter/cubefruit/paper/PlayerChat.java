package dev.debutter.cubefruit.paper;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.debutter.cubefruit.paper.utils.AwesomeText;
import dev.debutter.cubefruit.paper.utils.Caboodle;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerChat implements Listener, PluginMessageListener {

	private static HashMap<UUID, Long> lastMessage = new HashMap<>();
	private static HashMap<UUID, Long> spamCooldown = new HashMap<>();
	private static HashMap<UUID, List<String>> recentMessages = new HashMap<>();

	public PlayerChat() {
		Paper.plugin().getServer().getMessenger().registerOutgoingPluginChannel(Paper.plugin(), "cubefruit:globalchat");
		Paper.plugin().getServer().getMessenger().registerIncomingPluginChannel(Paper.plugin(), "cubefruit:globalchat", this);
	}

	@EventHandler
	public void onPlayerChat(AsyncChatEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Component message = event.message();
		FileConfiguration config = Paper.plugin().getConfig();

		if (config.getBoolean("chat.message-filter.enabled")) {
			if (config.getBoolean("chat.message-filter.censer-words") && config.getBoolean("chat.format.enabled")) {
				message = censoredFilter(message);
			} else {
				boolean blocked = filter(message);

				if (blocked) {
					player.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("chat.block_message.filtered_word", player)));
					event.setCancelled(true);
					return;
				}
			}
		}

		if (config.getBoolean("chat.anti-repeat-messages.enabled")) {
			boolean blocked = antiRepeatMessages(uuid, message);

			if (blocked) {
				player.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("chat.block_message.repeated_messages", player)));
				event.setCancelled(true);
				return;
			}
		}

		if (config.getBoolean("chat.anti-spam.enabled")) {
			boolean blocked = antiSpam(uuid);

			if (blocked) {
				double maxTime = config.getDouble("chat.anti-spam.max-time");

				player.sendMessage(
						AwesomeText.beautifyMessage(
								Paper.locale().getMessage("chat.block_message.fast_messages", player),
								Placeholder.unparsed("timeout", AwesomeText.parseTime(maxTime))
						)
				);
				event.setCancelled(true);
				return;
			}
		}

		addRecentMessage(uuid, message); // Add message to list of recently sent messages
		lastMessage.put(uuid, System.currentTimeMillis()); // Save last message timestamp

		if (config.getBoolean("chat.format.enabled")) {
			if (config.getBoolean("chat.format.colorize")) {
				TagResolver.Builder tags = TagResolver.builder();

				if (Caboodle.hasPermission(player, "chat.colorize.color")) {
					tags.resolvers(StandardTags.color(), StandardTags.reset());
				}
				if (Caboodle.hasPermission(player, "chat.colorize.decoration")) {
					tags.resolver(StandardTags.decorations());
				}
				if (Caboodle.hasPermission(player, "chat.colorize.gradient")) {
					tags.resolvers(StandardTags.gradient(), StandardTags.rainbow(), StandardTags.transition());
				}
				if (Caboodle.hasPermission(player, "chat.colorize.dynamic")) {
					tags.resolvers(StandardTags.font(), StandardTags.keybind(), StandardTags.translatable());
				}
				if (Caboodle.hasPermission(player, "chat.colorize.interactive")) {
					tags.resolvers(StandardTags.clickEvent(), StandardTags.hoverEvent(), StandardTags.insertion());
				}

				MiniMessage serializer = MiniMessage.builder()
						.tags(tags.build())
						.build();

				message = serializer.deserialize(PlainTextComponentSerializer.plainText().serialize(message));
			}

			String format = config.getString("chat.format.format");
			Bukkit.broadcast(AwesomeText.beautifyMessage(format, player, Placeholder.component("message", message)));

			event.setCancelled(true);
		}
	}

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] bytes) {
		if (!channel.equals("cubefruit:globalchat")) return;

		ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
//		String command = in.readUTF();
	}

	private Component censoredFilter(Component message) {
		String rawMessage = AwesomeText.destylize(message);
		FileConfiguration config = Paper.plugin().getConfig();
		List<String> blockedWords = config.getStringList("chat.message-filter.blocked-words");

		Matcher m = Pattern.compile("(\\w+)").matcher(rawMessage);
		while (m.find()) {
			if (!blockedWords.contains(m.group(0))) continue;

			String start = rawMessage.substring(0, m.start());
			String end = rawMessage.substring(m.start() + m.group(0).length());
			rawMessage = start + "*".repeat(m.group(0).length()) + end;
		}

		return AwesomeText.beautifyMessage(rawMessage);
	}
	private boolean filter(Component message) {
		String rawMessage = AwesomeText.destylize(message);
		FileConfiguration config = Paper.plugin().getConfig();
		List<String> blockedWords = config.getStringList("chat.message-filter.blocked-words");

		Matcher m = Pattern.compile("(\\w+)").matcher(rawMessage);
		while (m.find()) {
			if (blockedWords.contains(m.group(0))) return true;
		}

		return false;
	}
	private boolean antiRepeatMessages(UUID uuid, Component message) {
		String rawMessage = AwesomeText.destylize(message);

		List<String> messages = recentMessages.getOrDefault(uuid, new ArrayList<>());

        return messages.contains(rawMessage);
    }
	private void addRecentMessage(UUID uuid, Component message) {
		FileConfiguration config = Paper.plugin().getConfig();
		int maxRecentMessages = config.getInt("chat.anti-repeat-messages.recent-messages");
		String rawMessage = AwesomeText.destylize(message);
		List<String> messages = recentMessages.getOrDefault(uuid, new ArrayList<>());

		messages.add(rawMessage);

		if (messages.size() >= maxRecentMessages) {
			messages.remove(0);
		}

		recentMessages.put(uuid, messages);
	}
	private boolean antiSpam(UUID uuid) {
		FileConfiguration config = Paper.plugin().getConfig();
		double maxTime = config.getDouble("chat.anti-spam.max-time");

		if (!lastMessage.containsKey(uuid)) return false;

		long timeSince = System.currentTimeMillis() - lastMessage.get(uuid);

        return timeSince < maxTime * 1000;
    }
}
