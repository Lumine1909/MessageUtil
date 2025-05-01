package io.github.lumine1909.messageutil.inject;

import io.github.lumine1909.messageutil.core.PacketInterceptor;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static io.github.lumine1909.messageutil.api.MessageUtil.plugin;

public class PlayerJoinEventInjector implements Listener, Injector {


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ServerPlayer sp = ((CraftPlayer) player).getHandle();
        sp.connection.connection.channel.pipeline().addBefore("packet_handler", "pjei_handler", new PacketInterceptor() {

            @Override
            protected ServerPlayer player() {
                return sp;
            }
        });
    }

    @Override
    public void inject() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void uninject() {
        PlayerJoinEvent.getHandlerList().unregister(plugin);
    }
}
