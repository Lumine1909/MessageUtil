package io.github.lumine1909.messageutil.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.lumine1909.messageutil.core.MessengerManager;
import io.github.lumine1909.messageutil.inject.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MessageUtil {

    private static final BiMap<MessageUtil, Plugin> CACHE = HashBiMap.create();
    private static Injector injector;
    private static InjectorType injectorType;

    private final Plugin plugin;

    public MessageUtil(Plugin plugin) {
        this(plugin, InjectorType.CHANNEL_INIT);
    }

    public MessageUtil(Plugin plugin, InjectorType type) {
        this(plugin, type, null);
    }

    public MessageUtil(Plugin plugin, InjectorType type, Injector customInjector) {
        this.plugin = plugin;
        if (injectorType != null && type.ordinal() <= injectorType.ordinal()) {
            return;
        }
        refreshInjector(type, customInjector);
    }

    public void injectDirect(Player player) {
        InstantInjectManager.injectPlayer(player);
    }

    public void uninjectDirect(Player player) {
        InstantInjectManager.uninjectPlayer(player);
    }

    private void refreshInjector(InjectorType type, Injector customInjector) {
        boolean wasInjected = injector != null && injector.isInjected();
        if (wasInjected) {
            injector.uninject();
        }
        injector = switch (type) {
            case PLAYER_JOIN_EVENT -> new PlayerJoinEventInjector();
            case PLAYER_LIST -> new PlayerListInjector();
            case CHANNEL_INIT -> new ChannelInitInjector();
            case CUSTOM -> {
                if (customInjector == null) {
                    throw new IllegalArgumentException("Custom injector cannot be null");
                }
                yield customInjector;
            }
        };
        injectorType = type;
        if (wasInjected) {
            injector.inject();
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
        PLAYER_JOIN_EVENT,
        PLAYER_LIST,
        CHANNEL_INIT,
        CUSTOM
    }
}
