package dev.debutter.cuberry.paper;

import dev.debutter.cuberry.paper.utils.Caboodle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;

public class FormattableText implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = Paper.plugin().getConfig();

        if (!config.getBoolean("formattable-text.signs")) return;

        MiniMessage serializer = getSerializer(player, "formattable-text.signs");

        for (int l = 0; l < 4; l++) {
            Component line = event.line(l);
            if (line == null) continue;
            line = serializer.deserialize(PlainTextComponentSerializer.plainText().serialize(line));
            event.line(l, line);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBookSign(PlayerEditBookEvent event) {
        Player player = event.getPlayer();
        BookMeta bookMeta = event.getNewBookMeta();
        FileConfiguration config = Paper.plugin().getConfig();

        if (!event.isSigning()) return;
        if (!config.getBoolean("formattable-text.books")) return;

        MiniMessage serializer = getSerializer(player, "formattable-text.books");

        for (int p = 1; p <= bookMeta.getPageCount(); p++) {
            Component page = bookMeta.page(p);
            page = serializer.deserialize(PlainTextComponentSerializer.plainText().serialize(page));
            bookMeta.page(p, page);
        }

        event.setNewBookMeta(bookMeta);
    }

    private MiniMessage getSerializer(Player player, String permissionHead) {
        TagResolver.Builder tags = TagResolver.builder();

        if (Caboodle.hasPermission(player, permissionHead + ".color")) {
            tags.resolvers(StandardTags.color(), StandardTags.reset());
        }
        if (Caboodle.hasPermission(player, permissionHead + ".decoration")) {
            tags.resolver(StandardTags.decorations());
        }
        if (Caboodle.hasPermission(player, permissionHead + ".gradient")) {
            tags.resolvers(StandardTags.gradient(), StandardTags.rainbow(), StandardTags.transition());
        }
        if (Caboodle.hasPermission(player, permissionHead + ".dynamic")) {
            tags.resolvers(StandardTags.font(), StandardTags.keybind(), StandardTags.translatable());
        }
        if (Caboodle.hasPermission(player, permissionHead + ".interactive")) {
            tags.resolvers(StandardTags.clickEvent(), StandardTags.hoverEvent(), StandardTags.insertion());
        }

        return MiniMessage.builder()
                .tags(tags.build())
                .build();
    }

}
