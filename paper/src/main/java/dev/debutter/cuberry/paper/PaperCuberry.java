package dev.debutter.cuberry.paper;

import dev.debutter.cuberry.paper.chat.PlayerChat;
import dev.debutter.cuberry.paper.commands.Action;
import dev.debutter.cuberry.paper.commands.Back;
import dev.debutter.cuberry.paper.commands.Cuberry;
import dev.debutter.cuberry.paper.commands.EnderChest;
import dev.debutter.cuberry.paper.commands.Fly;
import dev.debutter.cuberry.paper.commands.Gamemode;
import dev.debutter.cuberry.paper.commands.Heal;
import dev.debutter.cuberry.paper.commands.Home;
import dev.debutter.cuberry.paper.commands.InvSee;
import dev.debutter.cuberry.paper.commands.Item;
import dev.debutter.cuberry.paper.commands.Launch;
import dev.debutter.cuberry.paper.commands.Message;
import dev.debutter.cuberry.paper.commands.Mutechat;
import dev.debutter.cuberry.paper.commands.Report;
import dev.debutter.cuberry.paper.commands.Respawn;
import dev.debutter.cuberry.paper.commands.Rules;
import dev.debutter.cuberry.paper.commands.Skipday;
import dev.debutter.cuberry.paper.commands.Spawn;
import dev.debutter.cuberry.paper.commands.Speed;
import dev.debutter.cuberry.paper.commands.Sudo;
import dev.debutter.cuberry.paper.commands.Teleport;
import dev.debutter.cuberry.paper.commands.Top;
import dev.debutter.cuberry.paper.commands.Tpa;
import dev.debutter.cuberry.paper.commands.Warp;
import dev.debutter.cuberry.paper.commands.Whois;
import dev.debutter.cuberry.paper.commands.WorldWhitelist;
import dev.debutter.cuberry.paper.communication.JoinAndLeave;
import dev.debutter.cuberry.paper.idle.IdlePlayers;
import dev.debutter.cuberry.paper.proxy.OnlyProxy;
import dev.debutter.cuberry.paper.resourcepack.ResourcePacks;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.LocaleManager;
import dev.debutter.cuberry.paper.utils.PluginSupport;
import dev.debutter.cuberry.paper.utils.storage.DataManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;

public final class PaperCuberry extends JavaPlugin implements Listener {

    private static PaperCuberry plugin;
    private static LocaleManager localeInstance;
    private static DataManager dataInstance;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        plugin = this;

        // Create a locale instance
        localeInstance = new LocaleManager(plugin, StandardCharsets.ISO_8859_1, "en_us");
        localeInstance.setDefaultLocale(getConfig().getString("plugin.language"));

        // Create a locale instance
        dataInstance = new DataManager(plugin);

        // Setup Plugin
        Bukkit.getServer().getPluginManager().registerEvents(new IdlePlayers(), plugin);
        IdlePlayers.hook();
        Bukkit.getServer().getPluginManager().registerEvents(new Caboodle(), plugin);
        Caboodle.start();
        PluginSupport.setup();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        Bukkit.getServer().getPluginManager().registerEvents(new InfoSaver(), plugin);
        InfoSaver.start();

        Bukkit.getServer().getPluginManager().registerEvents(new ResourcePacks(), plugin);
        ResourcePacks.hook();

        // Enable Basic Features
        enableFeatures();

        // Register Commands
        registerCommands();

        getLogger().info("Successfully loaded.");
    }

    private void registerCommands() { // TODO: finish adding descriptions
        Cuberry.start();

        new Cuberry().registerCommands(plugin);

        if (getConfig().getBoolean("commands.message.enabled")) {
            new Message().registerCommands(plugin);
        }

        if (getConfig().getBoolean("commands.skipday.enabled")) {
            new Skipday().registerCommands(plugin);
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

        enablePaperCommands();
    }

    private void enablePaperCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            if (getConfig().getBoolean("commands.itemstack.enabled")) {
                commands.registrar().register(Item.command);
            }
        });
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
    }

    @Override
    public void onDisable() {
        // Unload classes
        Cuberry.end();

        InfoSaver.end();

        // Save data
        dataInstance.saveAll();

        getLogger().info("Successfully unloaded.");
    }

    public static PaperCuberry plugin() {
        return plugin;
    }
    public static LocaleManager locale() {
        return localeInstance;
    }
    public static DataManager data() {
        return dataInstance;
    }

    public void reload() {
        reloadConfig();
        dataInstance.saveAll();

        getLogger().info("Reload complete.");
    }
}
