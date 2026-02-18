package dev.debutter.cuberry.paper.chat;

import dev.debutter.cuberry.paper.PaperCuberry;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFilters {

    private static final Map<UUID, Long> lastMessage = new ConcurrentHashMap<>();
    private static final Map<UUID, List<String>> recentMessages = new ConcurrentHashMap<>();

    /**
     * Checks if the chat message triggered any filters
     * @return Whether the message should be deleted
     */
    protected static boolean applyMessageFilters(@NotNull Player player, @NotNull Component message) {
        UUID uuid = player.getUniqueId();

        @SuppressWarnings("all") // Ignore unsubstituted expression
        FileConfiguration config = PaperCuberry.plugin().getConfig();

        if (config.getBoolean("chat.filters.block-phrases.enabled")) {
            boolean blocked = filterPhrases(message);

            if (blocked) {
                player.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("chat.block_message.blacklisted_phrase", player)));
                return true;
            }
        }

        if (config.getBoolean("chat.filters.repeat-messages.enabled")) {
            boolean blocked = antiRepeatMessages(uuid, message);

            if (blocked) {
                player.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("chat.block_message.repeated_messages", player)));
                return true;
            }
        }

        if (config.getBoolean("chat.filters.spam.enabled")) {
            boolean blocked = antiSpam(uuid);

            if (blocked) {
                double maxTime = config.getDouble("chat.filters.spam.max-time");

                player.sendMessage(
                    AwesomeText.beautifyMessage(
                        PaperCuberry.locale().getMessage("chat.block_message.fast_messages", player),
                        Placeholder.unparsed("timeout", AwesomeText.parseTime(maxTime))
                    )
                );
                return true;
            }
        }

        addRecentMessage(uuid, message); // Add message to list of recently sent messages
        lastMessage.put(uuid, System.currentTimeMillis()); // Save last message timestamp

        return false;
    }

    private static boolean filterPhrases(Component message) {
        String rawMessage = AwesomeText.destylize(message);
        FileConfiguration config = PaperCuberry.plugin().getConfig();
        List<String> blockedWords = config.getStringList("chat.filters.block-phrases.blacklist");

        Matcher m = Pattern.compile("(\\w+)").matcher(rawMessage);
        while (m.find())
            if (blockedWords.contains(m.group(0))) return true;

        return false;
    }

    private static boolean antiRepeatMessages(UUID uuid, Component message) {
        String rawMessage = AwesomeText.destylize(message);

        List<String> messages = recentMessages.getOrDefault(uuid, new ArrayList<>());

        return messages.contains(rawMessage);
    }

    private static void addRecentMessage(UUID uuid, Component message) {
        FileConfiguration config = PaperCuberry.plugin().getConfig();
        int maxRecentMessages = config.getInt("chat.filters.repeat-messages.recent-messages");
        String rawMessage = AwesomeText.destylize(message);
        List<String> messages = recentMessages.getOrDefault(uuid, new ArrayList<>());

        messages.add(rawMessage);

        while (messages.size() >= maxRecentMessages)
            messages.removeFirst();

        recentMessages.put(uuid, messages);
    }

    private static boolean antiSpam(UUID uuid) {
        FileConfiguration config = PaperCuberry.plugin().getConfig();
        double maxTime = config.getDouble("chat.filters.spam.max-time");

        if (!lastMessage.containsKey(uuid)) return false;

        long timeSince = System.currentTimeMillis() - lastMessage.get(uuid);

        return timeSince < maxTime * 1000;
    }
}
