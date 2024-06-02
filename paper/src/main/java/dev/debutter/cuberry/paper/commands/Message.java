package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.DogTags;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Message extends CommandWrapper {

	private static HashMap<UUID, UUID> reply = new HashMap<>();

	// TODO: make it so not just players can message you

	public Message() {
		CommandRegistry messageCmd = new CommandRegistry(this, "message");
		messageCmd.addAliases("msg", "w", "tell");
		messageCmd.setDescription("Send a private message to another player");

		CommandRegistry replyCmd = new CommandRegistry(this, "reply");
		replyCmd.addAliases("r");
		replyCmd.setDescription("Reply to the last player who messaged you");

		addRegistries(messageCmd, replyCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = Paper.plugin().getConfig();

		if (label.equalsIgnoreCase("message") || label.equalsIgnoreCase("msg") || label.equalsIgnoreCase("w") || label.equalsIgnoreCase("tell")) {
			if (!(sender instanceof Player player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

            if (!Caboodle.hasPermission(sender, "message")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/message <player> <message>"));
				return true;
			}

			if (!DogTags.isOnline(args[0])) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_found", sender)));
				return true;
			}

			Player receiver = Bukkit.getPlayer(args[0]);
			assert receiver != null;

			String str = String.join(" ", Caboodle.splice(args, 0, 1));

			if (str.isEmpty()) {
				sender.sendMessage(AwesomeText.beautifyMessage("<red>Error: <gray>You must provide a message to send."));
				return true;
			}

			String senderFormat = Paper.locale().getMessage("commands.message.send", sender);
			String receiveFormat = Paper.locale().getMessage("commands.message.receive", receiver);

			sender.sendMessage(AwesomeText.beautifyMessage(senderFormat, player,
					Placeholder.component("sender", AwesomeText.createPlayerHoverComponent(player)),
					Placeholder.component("receiver", AwesomeText.createPlayerHoverComponent(receiver)),
					Placeholder.unparsed("message", str)
			));
			receiver.sendMessage(AwesomeText.beautifyMessage(receiveFormat, receiver,
					Placeholder.component("sender", AwesomeText.createPlayerHoverComponent(player)),
					Placeholder.component("receiver", AwesomeText.createPlayerHoverComponent(receiver)),
					Placeholder.unparsed("message", str)
			));

			if (config.getBoolean("commands.message.sound.enabled")) {
				Sound sound = Sound.valueOf(config.getString("commands.message.sound.name"));
				float volume = (float) config.getDouble("commands.message.sound.volume");
				float pitch = (float) config.getDouble("commands.message.sound.pitch");
				receiver.playSound(receiver.getLocation(), sound, volume, pitch);
			}

			reply.put(receiver.getUniqueId(), player.getUniqueId());
			return true;
		}
		if (label.equalsIgnoreCase("reply") || label.equalsIgnoreCase("r")) {
			if (!(sender instanceof Player player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

            if (!Caboodle.hasPermission(sender, "reply")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/reply <message>"));
				return true;
			}

			if (!reply.containsKey(player.getUniqueId())) {
				sender.sendMessage(AwesomeText.beautifyMessage("<red>Error: <gray>There's no one for you to reply to."));
				return true;
			}

			if (!DogTags.isOnline(reply.get(player.getUniqueId()))) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_found", sender)));
				return true;
			}

			Player receiver = Bukkit.getPlayer(reply.get(player.getUniqueId()));
			assert receiver != null;

			String str = String.join(" ", args);

			if (str.isEmpty()) {
				sender.sendMessage(AwesomeText.beautifyMessage("<red>Error: <gray>You must provide a message to send."));
				return true;
			}

			String senderFormat = Paper.locale().getMessage("commands.message.send", sender);
			String receiveFormat = Paper.locale().getMessage("commands.message.receive", receiver);

			sender.sendMessage(AwesomeText.beautifyMessage(senderFormat, player,
					Placeholder.component("sender", AwesomeText.createPlayerHoverComponent(player)),
					Placeholder.component("receiver", AwesomeText.createPlayerHoverComponent(receiver)),
					Placeholder.unparsed("message", str)
			));
			receiver.sendMessage(AwesomeText.beautifyMessage(receiveFormat, receiver,
					Placeholder.component("sender", AwesomeText.createPlayerHoverComponent(player)),
					Placeholder.component("receiver", AwesomeText.createPlayerHoverComponent(receiver)),
					Placeholder.unparsed("message", str)
			));

			if (config.getBoolean("commands.message.sound.enabled")) {
				Sound sound = Sound.valueOf(config.getString("commands.message.sound.name"));
				float volume = (float) config.getDouble("commands.message.sound.volume");
				float pitch = (float) config.getDouble("commands.message.sound.pitch");
				receiver.playSound(receiver.getLocation(), sound, volume, pitch);
			}

			reply.put(receiver.getUniqueId(), player.getUniqueId());
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null;
	}
}
