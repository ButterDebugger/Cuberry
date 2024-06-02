package dev.debutter.cuberry.paper.commands.builder;

import dev.debutter.cuberry.paper.utils.Caboodle;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRegistry {
    private CommandWrapper wrapper;
    private String name;
    private List<String> aliases = new ArrayList<>();
    private String description = null;

    public CommandRegistry(CommandWrapper wrapper, String name) {
        this.wrapper = wrapper;
        this.name = name;
    }

    public void register(JavaPlugin plugin) { // TODO: retest
        PluginCommand command = Caboodle.getCommand(name, plugin);

        if (aliases != null) command.setAliases(aliases);
        if (description != null) command.setDescription(description);
//            command.setLabel();
//            command.setPermission();
//            command.setUsage();

        command.setExecutor(wrapper);
        command.setTabCompleter(wrapper);

        Caboodle.getCommandMap().register(plugin.getDescription().getName(), command);
    }

    /**
     *  Private setter methods
     */

    public void addAliases(String ...alias) {
        this.aliases.addAll(Arrays.asList(alias));
    }
    public void removeAliases(String ...alias) {
        this.aliases.removeAll(Arrays.asList(alias));
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void removeDescription() {
        this.description = null;
    }

    /**
     *  Getters
     */

    public CommandWrapper getWrapper() {
        return wrapper;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }
}
