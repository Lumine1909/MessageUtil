package io.github.lumine1909.messageutil.core;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PacketInterceptor extends ChannelDuplexHandler {

    private String playerName;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Packet<?> packet) {
            MessengerManager.INSTANCE.handleVanilla(getPlayer(), packet);
        }
        if (msg instanceof ServerboundCustomPayloadPacket(CustomPacketPayload payload) &&
            payload instanceof DiscardedPayload discardedPayload) {
            MessengerManager.INSTANCE.handlePayload(getPlayer(), discardedPayload);
            MessengerManager.INSTANCE.handleBytebuf(getPlayer(), discardedPayload);
        }
    }

    private ServerPlayer getPlayer() {
        return MinecraftServer.getServer().getPlayerList().getPlayerByName(playerName);
    }
}
