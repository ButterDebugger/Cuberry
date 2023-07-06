package com.butterycode.cubefruit;

import com.butterycode.cubefruit.utils.AwesomeText;
import com.butterycode.cubefruit.utils.Caboodle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerChatPreviewEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class PlayerChat implements Listener {

	private static HashMap<UUID, Long> lastMessage = new HashMap<>();
	private static HashMap<UUID, Long> spamCooldown = new HashMap<>();
	private static HashMap<UUID, List<String>> recentMessages = new HashMap<>();

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		ChatMessage chat = new ChatMessage(event.getPlayer(), event.getMessage());

		if (chat.isBlocked()) {
			String blockMessage = chat.getBlockMessage();
			event.setCancelled(true);

			if (blockMessage != null && !blockMessage.equals("")) {
				player.sendMessage(AwesomeText.prettifyMessage(blockMessage, player));
			}
		} else {
			event.setMessage(chat.getMessage());
			event.setFormat(chat.getFormat());
		}
	}

	@EventHandler
	public void onPlayerPreview(AsyncPlayerChatPreviewEvent event) {
		ChatMessage chat = new ChatMessage(event.getPlayer(), event.getMessage());

		if (chat.isBlocked()) {
			event.setFormat(AwesomeText.prettifyMessage("&c* Message Blocked *"));
		} else {
			event.setMessage(chat.getMessage());
			event.setFormat(chat.getFormat());
		}
	}

	private class ChatMessage {
		private Player player;
		private String message;
		private String unmodifiedMessage;
		private String format = "<%1$s> %2$s"; // Default format
		private boolean isBlocked = false;
		private String blockMessage = null;

		ChatMessage(Player p, String msg) {
			player = p;
			message = msg;
			unmodifiedMessage = msg;

			// Initialize chat message
			FileConfiguration config = Main.plugin().config();

			if (config.getBoolean("chat.anti-spam.enabled")) {
				antispam();
			}
			if (config.getBoolean("chat.message-filter.enabled")) {
				filter();
			}
			if (config.getBoolean("chat.format.enabled")) {
				formatMsg();
				formatFull();
			}
		}

		// Getters
		public Player getPlayer() {
			return player;
		}
		public String getMessage() {
			return message;
		}
		public String getUnmodifiedMessage() {
			return unmodifiedMessage;
		}
		public String getFormat() {
			return format;
		}
		public boolean isBlocked() {
			return isBlocked;
		}
		public String getBlockMessage() {
			return blockMessage;
		}

		// Initializers
		private void antispam() {
			UUID uuid = player.getUniqueId();
			FileConfiguration config = Main.plugin().config();

			// Repeated messages
			if (config.getInt("chat.anti-spam.repeated-messages.recent-messages") != -1) {
				if (!recentMessages.containsKey(uuid)) {
					recentMessages.put(uuid, new ArrayList<String>());
				}

				List<String> messages = recentMessages.get(uuid);

				if (messages.contains(unmodifiedMessage)) {
					blockMessage = config.getString("chat.anti-spam.repeated-messages.block-message");
					blockMessage = AwesomeText.prettifyMessage(blockMessage, player);

					isBlocked = true;
				} else {
					messages.add(unmodifiedMessage);

					if (messages.size() >= config.getInt("chat.anti-spam.repeated-messages.recent-messages")) {
						messages.remove(0);
					}
				}

				recentMessages.put(uuid, messages);
			}

			// Fast messages
			double maxTime = config.getDouble("chat.anti-spam.fast-messages.max-time");

			if (maxTime != -1) {
				if (!lastMessage.containsKey(uuid)) {
					lastMessage.put(uuid, System.currentTimeMillis());
				} else {
					Long timeSince = System.currentTimeMillis() - lastMessage.get(uuid);

					if (timeSince < maxTime * 1000) {
						blockMessage = config.getString("chat.anti-spam.fast-messages.block-message");
						blockMessage = AwesomeText.replacePlaceholder(blockMessage, "timeout", AwesomeText.parseTime(maxTime));
						blockMessage = AwesomeText.prettifyMessage(blockMessage, player);

						isBlocked = true;
					} else {
						lastMessage.put(uuid, System.currentTimeMillis());
					}
				}
			}
		}
		private void formatMsg() {
			List<String> rebuiltMessage = new ArrayList<>();
			FileConfiguration config = Main.plugin().config();

			for (String msg : message.split(" ")) {
				if (msg.equalsIgnoreCase("gg") && config.getBoolean("chat.format.golden-gg") && Caboodle.hasPermission(player, "chat.golden-gg")) {
					msg = AwesomeText.colorizeHex("&6" + msg + "&r");
				}

				if (config.getBoolean("chat.format.colorful-emotes") && Caboodle.hasPermission(player, "chat.colorful-emotes")) {
					if (msg.equalsIgnoreCase("<3") || msg.equalsIgnoreCase("\u2764")) {
						msg = AwesomeText.colorizeHex("&c\u2764&r");
					} else if (msg.equalsIgnoreCase("</3") || msg.equalsIgnoreCase("<\\3")) {
						msg = AwesomeText.colorizeHex("&#393939\u2764&r");
					} else if (msg.equalsIgnoreCase("rip")) {
						msg = AwesomeText.colorizeHex("&c" + msg + "&r");
					} else if (msg.equals("L")) {
						msg = AwesomeText.colorizeHex("&c" + msg + "&r");
					} else if (msg.equalsIgnoreCase("bozo")) {
						msg = AwesomeText.colorizeHex("&c" + msg + "&r");
					} else if (msg.equalsIgnoreCase("uwu") || msg.equalsIgnoreCase("owo") || msg.equalsIgnoreCase("owu") || msg.equalsIgnoreCase("uwo")) {
						msg = AwesomeText.colorizeHex("&d" + msg + "&r");
					} else if (
							msg.equalsIgnoreCase("o7") ||
							msg.equalsIgnoreCase("o/") ||
							msg.equalsIgnoreCase("\\o") ||
							msg.equalsIgnoreCase(":)") ||
							msg.equalsIgnoreCase(":o") ||
							msg.equalsIgnoreCase(">:O") ||
							msg.equalsIgnoreCase(":D") ||
							msg.equalsIgnoreCase("D:") ||
							msg.equalsIgnoreCase(":P") ||
							msg.equalsIgnoreCase("c:") ||
							msg.equalsIgnoreCase(":/") ||
							msg.equalsIgnoreCase(":\\") ||
							msg.equalsIgnoreCase(">:\\") ||
							msg.equalsIgnoreCase(">:/") ||
							msg.equalsIgnoreCase(":L") ||
							msg.equalsIgnoreCase(":|")
					) {
						msg = AwesomeText.colorizeHex("&e" + msg + "&r");
					} else if (
							msg.equalsIgnoreCase("¯\\_(\u30C4)_/¯") ||
							msg.equalsIgnoreCase("*shrug*") ||
							msg.equalsIgnoreCase("*shrugs*")
					) {
						msg = AwesomeText.colorizeHex("&e¯\\_(\u30C4)_/¯&r");
					} else if (
							msg.equalsIgnoreCase(":3") ||
							msg.equalsIgnoreCase(">:3")
					) {
						msg = AwesomeText.colorizeHex("&#FF8604" + msg + "&r");
					} else if (msg.equalsIgnoreCase(":c")) {
						msg = AwesomeText.colorizeHex("&#99C6F7" + msg + "&r");
					} else if (msg.equalsIgnoreCase("xD")) {
						msg = AwesomeText.colorizeHex("&#E1E114" + msg + "&r");
					} else if (msg.equalsIgnoreCase("lol") || msg.equalsIgnoreCase("lmao") || msg.equalsIgnoreCase("lmfao")) {
						msg = AwesomeText.colorizeHex("&#05FF86" + msg + "&r");
					} else if (msg.equalsIgnoreCase("piss")) {
						msg = AwesomeText.colorizeHex("&#FA1464" + msg + "&r");
					}
				}

				rebuiltMessage.add(msg);
			}
			message = String.join(" ", rebuiltMessage);

			if (config.getBoolean("chat.format.colorize") && Caboodle.hasPermission(player, "chat.colorize")) {
				message = AwesomeText.colorizeHex(message);
			}
		}
		private void formatFull() {
			FileConfiguration config = Main.plugin().config();
			String newFormat = AwesomeText.prettifyMessage(config.getString("chat.format.format"), player);

			newFormat = AwesomeText.replacePlaceholder(newFormat, "player", "%1$s");
			newFormat = AwesomeText.replacePlaceholder(newFormat, "message", "%2$s");
			newFormat = AwesomeText.importPlaceholders(newFormat, player);

			format = newFormat;
		}
		private void filter() {
			String[] splitMessage = Pattern.compile("[^\\w\\s]").matcher(message).replaceAll("").split(" ");
			String[] newMessage = message.split(" ");
			FileConfiguration config = Main.plugin().config();
			List<String> blockedWords = config.getStringList("chat.message-filter.blocked-words");
			int dropThreshold = config.getInt("chat.message-filter.drop-threshold");
			int wordsCaught = 0;

			for (int i = 0; i < splitMessage.length; i++) {
				String word = splitMessage[i].toLowerCase();

				if (blockedWords.contains(word)) {
					wordsCaught += 1;

					newMessage[i] = "*".repeat(word.length());
				}
			}

			if (dropThreshold != -1 && wordsCaught >= dropThreshold) {
				blockMessage = config.getString("chat.message-filter.block-message");
				blockMessage = AwesomeText.prettifyMessage(blockMessage, player);

				isBlocked = true;
			}

			message = String.join(" ", newMessage);
		}
	}
}
