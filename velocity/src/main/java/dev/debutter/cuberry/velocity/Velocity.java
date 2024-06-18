package dev.debutter.cuberry.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.debutter.cuberry.common.Constants;
import dev.debutter.cuberry.velocity.chat.BroadcastHandler;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

@Plugin(
    id = Constants.PROJECT_ID,
    name = Constants.PROJECT_NAME,
    authors = "ButterDebugger",
    version = Constants.VERSION,
    dependencies = {
        @Dependency(id = "protoweaver", optional = true)
    }
)
public class Velocity {

    private static Velocity plugin;
    private ProxyServer proxy;
    private Logger logger;
    private YamlDocument config;
    private Path dataDirectory;

    @Inject
    public Velocity(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        plugin = this;

        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        // Setup plugin configuration
        try {
            config = YamlDocument.create(new File(dataDirectory.toFile(), "config.yml"),
                    Objects.requireNonNull(getClass().getResourceAsStream("/velocity-config.yml")),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version"))
                            .setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build()
            );

            config.update();
            config.save();
        } catch (IOException e) {
            logger.error("Could not create/load config.");
            Optional<PluginContainer> container = proxy.getPluginManager().getPlugin("cuberry");
            container.ifPresent(pluginContainer -> pluginContainer.getExecutorService().shutdown());
        }

        logger.info("Successfully initialized.");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        if (config.getBoolean("global-chat.enabled")) {
            if (getProxy().getPluginManager().isLoaded("protoweaver")) {
                BroadcastHandler.register();
            } else {
                logger.warn("Could not find the required plugin \"ProtoWeaver\" to enable global chat");
            }
        }
    }

    public static Velocity getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public YamlDocument getConfig() {
        return config;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }
}
