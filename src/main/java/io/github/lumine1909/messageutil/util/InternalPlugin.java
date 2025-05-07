package io.github.lumine1909.messageutil.util;

import io.github.lumine1909.messageutil.inject.InstantInjectManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.List;

@SuppressWarnings({"removal", "UnstableApiUsage"})
public class InternalPlugin extends PluginBase {

    public static final InternalPlugin INSTANCE = new InternalPlugin();
    private final PluginDescriptionFile pdf;
    private boolean enabled = true;

    public InternalPlugin() {
        String pluginName = "MessageUtilInternal";
        pdf = new PluginDescriptionFile(pluginName, "1.0", "nms");
        Bukkit.getPluginManager().registerEvents(new InstantInjectManager.AutoUninjectListener(), this);
    }

    @Override
    public @NotNull File getDataFolder() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public @NotNull PluginDescriptionFile getDescription() {
        return pdf;
    }

    @Override
    public io.papermc.paper.plugin.configuration.@NotNull PluginMeta getPluginMeta() {
        return pdf;
    }

    @Override
    public @NotNull FileConfiguration getConfig() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public InputStream getResource(@NotNull String filename) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void saveConfig() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void saveDefaultConfig() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void saveResource(@NotNull String resourcePath, boolean replace) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void reloadConfig() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public @NotNull PluginLogger getLogger() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public @NotNull org.bukkit.plugin.PluginLoader getPluginLoader() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public @NotNull Server getServer() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void onDisable() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void onLoad() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void onEnable() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean isNaggable() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setNaggable(boolean canNag) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull String worldName, @Nullable String id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public @NotNull io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager<org.bukkit.plugin.@NotNull Plugin> getLifecycleManager() {
        throw new UnsupportedOperationException("Not supported.");
    }
}
