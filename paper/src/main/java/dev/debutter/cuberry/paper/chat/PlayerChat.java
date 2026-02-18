package dev.debutter.cuberry.paper.chat;

import dev.debutter.cuberry.paper.PaperCuberry;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.PluginSupport;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.permission.PermissionChecker;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.regex.Pattern;

public class PlayerChat implements Listener {

	public PlayerChat() {
		if (PaperCuberry.plugin().getConfig().getBoolean("chat.global-chat.enabled")) {
			if (PluginSupport.hasProtoWeaver()) {
				RelayHandler.register();
			} else {
				PaperCuberry.plugin().getLogger().warning("Could not find the required plugin \"ProtoWeaver\" to enable global chat");
			}
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncChatEvent event) {
		@SuppressWarnings("all") // Ignore unsubstituted expression
		FileConfiguration config = PaperCuberry.plugin().getConfig();

		{
			Player player = event.getPlayer();
			Component message = event.message();

			// Delete the message if it triggered any of the chat filters
			if (ChatFilters.applyMessageFilters(player, message)) {
				event.setCancelled(true);
				return;
			}

			// Relay the message for the global chat
			if (PluginSupport.hasProtoWeaver()) {
				RelayHandler.sendMessage(player.getUniqueId(), message);
			}
		}

		// If format is enabled, tell the event to use the renderer defined in this file
		if (config.getBoolean("chat.modification.enabled")) {
            event.renderer((@NonNull Player source, @NonNull Component sourceDisplayName, @NonNull Component message, @NonNull Audience viewer) -> {
				// Format the original chat message
				Component formattedMessage = message;

				// Colorize the chat message
				if (config.getBoolean("chat.modification.colorize")) {
					TagResolver.Builder tags = TagResolver.builder();

					if (Caboodle.hasPermission(source, "chat.colorize.color"))
						tags.resolvers(StandardTags.color(), StandardTags.reset());
					if (Caboodle.hasPermission(source, "chat.colorize.decoration"))
						tags.resolver(StandardTags.decorations());
					if (Caboodle.hasPermission(source, "chat.colorize.gradient"))
						tags.resolvers(StandardTags.gradient(), StandardTags.rainbow(), StandardTags.transition());
					if (Caboodle.hasPermission(source, "chat.colorize.dynamic"))
						tags.resolvers(StandardTags.font(), StandardTags.keybind(), StandardTags.translatable());
					if (Caboodle.hasPermission(source, "chat.colorize.interactive"))
						tags.resolvers(StandardTags.clickEvent(), StandardTags.hoverEvent(), StandardTags.insertion());

					MiniMessage serializer = MiniMessage.builder()
						.tags(tags.build())
						.build();

					formattedMessage = serializer.deserialize(PlainTextComponentSerializer.plainText().serialize(formattedMessage));
				}

				// Handle viewer mentions
				if (config.getBoolean("chat.modification.mentions.enabled")) {
					Optional<String> viewerName = viewer.get(Identity.NAME);

					if (viewerName.isPresent()) {
						Component replacedNameComponent = formattedMessage.replaceText(
							TextReplacementConfig.builder()
								.match(Pattern.compile("(?<!<|</)\\b\\Q" + viewerName.get() + "\\E\\b(?!>)"))
								.replacement(AwesomeText.beautifyMessage("<u><viewer_name></u>",
									Placeholder.unparsed("viewer_name", viewerName.get())
								))
								.build()
						);

						// If the viewer was mentioned and mention sounds are enabled, play a sound for the viewer
						if (!replacedNameComponent.equals(formattedMessage) && config.getBoolean("chat.modification.mentions.sound.enabled")) {
							@SuppressWarnings("all") // Ignore unsubstituted expression
							@Nullable String soundName = config.getString("chat.modification.mentions.sound.name");
							assert soundName != null;

							@SuppressWarnings("all") // Ignore unsubstituted expression
							Sound sound = Sound.sound()
								.type(Key.key(soundName))
								.volume((float) config.getDouble("chat.modification.mentions.sound.volume"))
								.pitch((float) config.getDouble("chat.modification.mentions.sound.pitch"))
								.build();

							viewer.playSound(sound);
						}

						// Update formatted message
						formattedMessage = replacedNameComponent;
					}
				}

				// See if the viewer should be able to delete the message
				Optional<PermissionChecker> optionalViewerPermissionChecker = viewer.get(PermissionChecker.POINTER);
				boolean yourMessage = source.equals(viewer);
				boolean canDeleteMessages = (
					config.getBoolean(yourMessage
						? "chat.modification.delete-own-messages"
						: "chat.modification.delete-others-messages"
					) &&
					optionalViewerPermissionChecker.isPresent() &&
					optionalViewerPermissionChecker.get().test(yourMessage
						? "cuberry.chat.delete-own-messages"
						: "cuberry.chat.delete-others-messages"
					)
				);

				// Add the delete message action if the viewer is able to delete the message
				if (canDeleteMessages)
					formattedMessage = formattedMessage
						.hoverEvent(PaperCuberry.locale().getBeautifiedMessage(yourMessage
							? "chat.click_action.delete_own_message"
							: "chat.click_action.delete_others_message",
							Placeholder.unparsed("others_name", source.getName())
						))
						.clickEvent(ClickEvent.callback(audience -> {
							// Cancel if the user should no longer be able to delete messages
							Optional<PermissionChecker> optionalAudiencePermissionChecker = audience.get(PermissionChecker.POINTER);

							if (!(
								config.getBoolean(yourMessage
									? "chat.modification.delete-own-messages"
									: "chat.modification.delete-others-messages"
								) &&
								optionalAudiencePermissionChecker.isPresent() &&
								optionalAudiencePermissionChecker.get().test(yourMessage
									? "cuberry.chat.delete-own-messages"
									: "cuberry.chat.delete-others-messages"
								)
							)) return;

							// Delete the message
							Bukkit.getServer().deleteMessage(event.signedMessage());
						}));

				// Format source player name
				Component sourceName = Component.text(source.getName());

				if (config.getBoolean("chat.modification.fancy-player-names")) {
					boolean messageCommandEnabled = config.getBoolean("commands.message.enabled");

					sourceName = Component.text(source.getName())
						.hoverEvent(HoverEvent.showText(AwesomeText.beautifyMessage(
							"<player_head> <b><username></b>" +
								// Add click to message player prompt if the message command is enabled
								(messageCommandEnabled ? "<newline><click_to_message_player>" : ""),
							Placeholder.component("player_head", Component.object(ObjectContents.playerHead(source.getUniqueId()))),
							Placeholder.unparsed("username", source.getName()),
							Placeholder.component("click_to_message_player", PaperCuberry.locale().getBeautifiedMessage("chat.click_action.message_player"))
						)));

					// Add click event if the message command is enabled
					if (messageCommandEnabled)
						sourceName = sourceName.clickEvent(ClickEvent.suggestCommand("/message " + source.getName() + " "));
				}

				// Return the formatted chat message
				String format = config.getString("chat.modification.format");

				return AwesomeText.beautifyMessage(
					format, source,
					Placeholder.component("player_nameplate", sourceName),
					Placeholder.component("message", formattedMessage)
				);
			});
		}
	}

}
