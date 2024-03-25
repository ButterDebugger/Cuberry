package dev.debutter.cubefruit.paper;

import dev.debutter.cubefruit.paper.commands.*;
import dev.debutter.cubefruit.paper.utils.*;
import dev.debutter.cubefruit.paper.utils.storage.DataManager;
import dev.debutter.cubefruit.paper.utils.storage.DataStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Paper extends JavaPlugin implements Listener {

    private static Paper plugin;
    private HashMap<String, DataStorage> dataFiles = new HashMap<>();
    private static LocaleManager localeInstance;
    private static DataManager dataInstance;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        plugin = this;

        // Create a locale instance
        localeInstance = new LocaleManager(plugin, "en_us");
        localeInstance.setDefaultLocale(getConfig().getString("plugin.language"));

        // Create a locale instance
        dataInstance = new DataManager(plugin);

        // Setup Plugin
        Bukkit.getServer().getPluginManager().registerEvents(new IdlePlayers(), plugin);
        IdlePlayers.start();
        Bukkit.getServer().getPluginManager().registerEvents(new Caboodle(), plugin);
        Caboodle.start();
        PluginSupport.setup();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        Bukkit.getServer().getPluginManager().registerEvents(new InfoSaver(), plugin);
        InfoSaver.start();

        // Enable Basic Features
        enableFeatures();

        // Register Commands
        registerCommands();

        getLogger().info("Successfully loaded.");
    }

    private void registerCommands() { // TODO: finish adding descriptions
        CubeFruit.start();

        PluginCommand command = Caboodle.getCommand("cubefruit", plugin);
        command.setExecutor(new CubeFruit());
        command.setTabCompleter(new CubeFruit());
        Caboodle.getCommandMap().register(plugin.getDescription().getName(), command);

        if (getConfig().getBoolean("commands.message.enabled")) {
            new Message().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.skipday.enabled")) {
            new Skipday().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.itemstack.enabled")) {
            new Item().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.home.enabled")) {
            Home home = new Home();

            Bukkit.getServer().getPluginManager().registerEvents(home, plugin);
            home.registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.spawn.enabled")) {
            Spawn spawn = new Spawn();

            Bukkit.getServer().getPluginManager().registerEvents(spawn, plugin);
            spawn.registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.tpa.enabled")) {
            new Tpa().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.respawn.enabled")) {
            new Respawn().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.worldwhitelist.enabled")) {
            WorldWhitelist worldWhitelist = new WorldWhitelist();

            Bukkit.getServer().getPluginManager().registerEvents(worldWhitelist, plugin);
            worldWhitelist.registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.gamemode.enabled")) {
            new Gamemode().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.fly.enabled")) {
            new Fly().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.heal.enabled")) {
            new Heal().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.mutechat.enabled")) {
            Mutechat mutechat = new Mutechat();

            Bukkit.getServer().getPluginManager().registerEvents(mutechat, plugin);
            mutechat.registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.speed.enabled")) {
            new Speed().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.report.enabled")) {
            new Report().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.whois.enabled")) {
            new Whois().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.rules.enabled")) {
            new Rules().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.launch.enabled")) {
            new Launch().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.warp.enabled")) {
            new Warp().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.teleport.enabled")) {
            new Teleport().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.sudo.enabled")) {
            new Sudo().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.back.enabled")) {
            Back back = new Back();

            Bukkit.getServer().getPluginManager().registerEvents(back, plugin);
            back.registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.enderchest.enabled")) {
            new EnderChest().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.top.enabled")) {
            new Top().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.invsee.enabled")) {
            new InvSee().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.action.enabled")) {
            new Action().registerCommands(plugin);
        }
    }

    private void enableFeatures() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerChat(), plugin);

        Bukkit.getServer().getPluginManager().registerEvents(new JoinAndLeave(), plugin);

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerName(), plugin);
        PlayerName.start();

        Bukkit.getServer().getPluginManager().registerEvents(new FormattableText(), plugin);

        if (getConfig().getBoolean("only-proxy.enabled")) { // TODO: test
            Bukkit.getServer().getPluginManager().registerEvents(new OnlyProxy(), plugin);
        }

        if (getConfig().getBoolean("resource-packs.enabled")) {
            Bukkit.getServer().getPluginManager().registerEvents(new ResourcePacks(), plugin);
        }
    }

    @Override
    public void onDisable() {
        // Unload classes
        CubeFruit.end();

        InfoSaver.end();

        // Save data
        saveDataFiles();

        getLogger().info("Successfully unloaded.");
    }

    public static Paper plugin() {
        return plugin;
    }

    public static LocaleManager locale() {
        return localeInstance;
    }

    public void reload() {
        reloadConfig();
        saveDataFiles();

        getLogger().info("Reload complete.");
    }

    public void saveDataFiles() {
        for (String key : dataFiles.keySet()) {
            dataFiles.get(key).save();
        }
    }

    public DataStorage getData(String filepath) {
        if (!dataFiles.containsKey(filepath)) {
            dataFiles.put(filepath, new DataStorage(plugin, filepath));
        }
        return dataFiles.get(filepath);
    }
}
