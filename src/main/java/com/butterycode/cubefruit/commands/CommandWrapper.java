package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.utils.Caboodle;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public abstract class CommandWrapper implements CommandExecutor, TabCompleter {

    private List<CommandRegistry> registries = new ArrayList<CommandRegistry>();

    public void registerCommands(JavaPlugin plugin) {
        for (CommandRegistry command : registries) {
            command.register(plugin);
        }
    }

    /**
     *  Registry methods
     */

    protected void addRegistries(CommandRegistry ...command) {
        registries.addAll(Arrays.asList(command));
    }
    protected List<CommandRegistry> getRegistries() {
        return registries;
    }
    protected CommandRegistry getRegistryByName(String name) {
        return registries.stream().filter(cmd -> cmd.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     *  Command registry constructor
     */

    protected class CommandRegistry {
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
}
