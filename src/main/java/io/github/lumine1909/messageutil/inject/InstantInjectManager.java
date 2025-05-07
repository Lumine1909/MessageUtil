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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.lumine1909.messageutil.object.PacketContext.CONTEXT_CACHE;

public class InstantInjectManager {

    public static final Set<Player> INJECTED_PLAYERS = ConcurrentHashMap.newKeySet();

    public static void injectPlayer(Player player) {
        if (INJECTED_PLAYERS.contains(player)) {
            return;
        }
        ServerPlayer sp = ((CraftPlayer) player).getHandle();
        String name = "instant_handler" + UUID.randomUUID();
        PacketContext context = new PacketContext(sp, name);
        sp.connection.connection.channel.pipeline().addBefore("packet_handler", name, new PacketInterceptor() {
            @Override
            protected PacketContext context() {
                return context;
            }
        });
    }

    public static void uninjectPlayer(Player player) {
        ServerPlayer sp = ((CraftPlayer) player).getHandle();
        Channel channel = sp.connection.connection.channel;
        PacketContext context;
        if ((context = CONTEXT_CACHE.getIfPresent(player.getName())) == null) {
            return;
        }
        try {
            channel.pipeline().remove(context.interceptor());
        } catch (Exception ignored) {
        }
        INJECTED_PLAYERS.remove(player);
    }

    public static class AutoUninjectListener implements Listener {

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            uninjectPlayer(event.getPlayer());
            CONTEXT_CACHE.invalidate(event.getPlayer().getName());
            INJECTED_PLAYERS.remove(event.getPlayer());
        }
    }
}
