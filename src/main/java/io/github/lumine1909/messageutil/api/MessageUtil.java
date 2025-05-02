package io.github.lumine1909.messageutil.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.lumine1909.messageutil.core.MessengerManager;
import io.github.lumine1909.messageutil.inject.ChannelInitInjector;
import io.github.lumine1909.messageutil.inject.Injector;
import io.github.lumine1909.messageutil.inject.PlayerJoinEventInjector;
import io.github.lumine1909.messageutil.inject.PlayerListInjector;
import org.bukkit.plugin.Plugin;

public class MessageUtil {

    private static final BiMap<MessageUtil, Plugin> CACHE = HashBiMap.create();
    private static Injector injector;

    private final Plugin plugin;

    public MessageUtil(Plugin plugin, InjectorType type) {
        this.plugin = plugin;
        if (injector != null) {
            return;
        }
        switch (type) {
            case PLAYER_LIST -> injector = new PlayerListInjector();
            case PLAYER_JOIN_EVENT -> injector = new PlayerJoinEventInjector();
            case CHANNEL_INIT -> injector = new ChannelInitInjector();
            default -> throw new RuntimeException("Invalid injector type");
        }
    }

    public void enable() {
        if (CACHE.isEmpty()) {
            injector.inject();
        }
        CACHE.put(this, plugin);
    }

    public void disable() {
        CACHE.remove(this);
        if (CACHE.isEmpty()) {
            injector.uninject();
        }
    }

    public MessengerManager getMessengerManager() {
        return MessengerManager.INSTANCE;
    }

    public enum InjectorType {
        CHANNEL_INIT, PLAYER_JOIN_EVENT, PLAYER_LIST
    }
}
