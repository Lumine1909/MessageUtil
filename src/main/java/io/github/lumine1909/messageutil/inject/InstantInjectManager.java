package io.github.lumine1909.messageutil.inject;

import io.github.lumine1909.messageutil.core.PacketInterceptor;
import io.github.lumine1909.messageutil.object.PacketContext;
import io.netty.channel.Channel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InstantInjectManager {

    public static final Set<Player> INJECTED_PLAYERS = ConcurrentHashMap.newKeySet();

    public static void injectPlayer(Player player) {
        if (INJECTED_PLAYERS.contains(player)) {
            return;
        }
        ServerPlayer sp = ((CraftPlayer) player).getHandle();
        PacketContext context = new PacketContext(sp);
        sp.connection.connection.channel.pipeline().addBefore("packet_handler", "instant_handler", new PacketInterceptor() {
            @Override
            protected PacketContext context() {
                return context;
            }
        });
    }

    public static void uninjectPlayer(Player player) {
        ServerPlayer sp = ((CraftPlayer) player).getHandle();
        Channel channel = sp.connection.connection.channel;
        removeHandler(channel, "instant_handler");
        removeHandler(channel, "pjei_handler");
        removeHandler(channel, "pli_handler");
        removeHandler(channel, "cii_handler");
        INJECTED_PLAYERS.remove(player);
    }

    private static void removeHandler(Channel channel, String handlerName) {
        try {
            channel.pipeline().remove(handlerName);
        } catch (Exception ignored) {
        }
    }

    public static class AutoUninjectListener implements Listener {

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            uninjectPlayer(event.getPlayer());
            PacketContext.CONTEXT_CACHE.invalidate(event.getPlayer().getName());
        }
    }
}
