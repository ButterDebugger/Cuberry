package dev.debutter.cubefruit.paper.commands.builder;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
}
