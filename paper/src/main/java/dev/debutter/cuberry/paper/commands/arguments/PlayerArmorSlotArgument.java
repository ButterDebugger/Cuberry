package dev.debutter.cuberry.paper.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class PlayerArmorSlotArgument implements CustomArgumentType.Converted<PlayerArmorSlot, String> {

    private static final DynamicCommandExceptionType ERROR_INVALID_ARMOR_SLOT = new DynamicCommandExceptionType(slot ->
        MessageComponentSerializer.message().serialize(Component.translatable("slot.unknown", Component.text(String.valueOf(slot))))
    );

    @Override
    public PlayerArmorSlot convert(String nativeType) throws CommandSyntaxException {
        try {
            return PlayerArmorSlot.valueOf(nativeType.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            throw ERROR_INVALID_ARMOR_SLOT.create(nativeType);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (PlayerArmorSlot armorSlot : PlayerArmorSlot.values()) {
            String name = armorSlot.toString();

            // Only suggest if the armor slot name matches the user input
            if (name.startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(armorSlot.toString());
            }
        }

        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
