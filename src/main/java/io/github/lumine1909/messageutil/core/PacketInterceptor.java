package io.github.lumine1909.messageutil.core;

import io.github.lumine1909.messageutil.object.PacketContext;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;

public abstract class PacketInterceptor extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Packet<?> packet) {
            MessengerManager.INSTANCE.handleVanilla(context(), packet);
        }
        if (msg instanceof ServerboundCustomPayloadPacket(CustomPacketPayload payload) &&
            payload instanceof DiscardedPayload discardedPayload) {
            MessengerManager.INSTANCE.handlePayload(context(), discardedPayload);
            MessengerManager.INSTANCE.handleBytebuf(context(), discardedPayload);
        }
        super.channelRead(ctx, msg);
    }

    protected abstract PacketContext context();
}
