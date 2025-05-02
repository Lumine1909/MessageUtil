package io.github.lumine1909.messageutil.inject;

import io.github.lumine1909.messageutil.core.PacketInterceptor;
import io.netty.channel.ChannelHandlerContext;
import io.papermc.paper.network.ChannelInitializeListenerHolder;
import net.kyori.adventure.key.Key;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class ChannelInitInjector implements Injector {

    private static final Key LISTENER_KEY = Key.key("messageutil:cii");

    @Override
    public void inject() {
        ChannelInitializeListenerHolder.addListener(LISTENER_KEY, channel -> channel.pipeline().addBefore("packet_handler", "cii_handler", new PacketInterceptor() {
            private String name;

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof ServerboundHelloPacket(String name, UUID uuid)) {
                    this.name = name;
                }
                super.channelRead(ctx, msg);
            }

            @Override
            protected ServerPlayer player() {
                return MinecraftServer.getServer().getPlayerList().getPlayerByName(name);
            }
        }));
    }

    @Override
    public void uninject() {
        ChannelInitializeListenerHolder.removeListener(LISTENER_KEY);
    }
}
