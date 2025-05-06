package io.github.lumine1909.messageutil.inject;

import io.github.lumine1909.messageutil.core.PacketInterceptor;
import io.github.lumine1909.messageutil.object.PacketContext;
import io.github.lumine1909.messageutil.util.InternalPlugin;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventInjector implements Listener, Injector {

    private boolean injected = false;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ServerPlayer sp = ((CraftPlayer) player).getHandle();
        PacketContext context = new PacketContext(sp);
        sp.connection.connection.channel.pipeline().addBefore("packet_handler", "pjei_handler", new PacketInterceptor() {
            @Override
            protected PacketContext context() {
                return context;
            }
        });
    }

    @Override
    public void inject() {
        Bukkit.getPluginManager().registerEvents(this, InternalPlugin.INSTANCE);
        injected = true;
    }

    @Override
    public void uninject() {
        PlayerJoinEvent.getHandlerList().unregister(InternalPlugin.INSTANCE);
        injected = false;
    }

    @Override
    public boolean isInjected() {
        return injected;
    }
}
