package io.github.lumine1909.messageutil.inject;

import io.github.lumine1909.messageutil.core.PacketInterceptor;
import io.github.lumine1909.messageutil.object.PacketContext;
import io.netty.channel.ChannelHandlerContext;
import io.papermc.paper.network.ChannelInitializeListenerHolder;
import net.kyori.adventure.key.Key;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;

import java.util.UUID;

public class ChannelInitInjector implements Injector {

    private static final Key LISTENER_KEY = Key.key("messageutil:cii");

    @Override
    public void inject() {
        ChannelInitializeListenerHolder.addListener(LISTENER_KEY, channel -> channel.pipeline().addBefore("packet_handler", "cii_handler", new PacketInterceptor() {
            private final PacketContext context = new PacketContext(channel);

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof ServerboundHelloPacket(String name, UUID uuid)) {
                    this.context.setName(name);
                }
                super.channelRead(ctx, msg);
            }

            @Override
            protected PacketContext context() {
                return context;
            }
        }));
    }

    @Override
    public void uninject() {
        ChannelInitializeListenerHolder.removeListener(LISTENER_KEY);
    }
}
