package io.github.lumine1909.messageutil.api;

import io.github.lumine1909.messageutil.core.MessengerManager;
import io.github.lumine1909.messageutil.inject.ChannelInitInjector;
import io.github.lumine1909.messageutil.inject.Injector;
import io.github.lumine1909.messageutil.inject.PlayerJoinEventInjector;
import io.github.lumine1909.messageutil.inject.PlayerListInjector;
import io.github.lumine1909.messageutil.util.InternalPlugin;
import org.bukkit.plugin.Plugin;

public class MessageUtil {

    public static Plugin plugin = InternalPlugin.INSTANCE;

    private final Injector injector;

    public MessageUtil(InjectorType type) {
        switch (type) {
            case PLAYER_LIST -> injector = new PlayerListInjector();
            case PLAYER_JOIN_EVENT -> injector = new PlayerJoinEventInjector();
            case CHANNEL_INIT -> injector = new ChannelInitInjector();
            default -> throw new RuntimeException("Invalid injector type");
        }
    }

    public void inject() {
        injector.inject();
    }

    public void uninject() {
        injector.uninject();
    }

    public MessengerManager getMessengerManager() {
        return MessengerManager.INSTANCE;
    }

    public enum InjectorType {
        CHANNEL_INIT, PLAYER_JOIN_EVENT, PLAYER_LIST
    }
}
