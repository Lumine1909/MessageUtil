package io.github.lumine1909.messageutil.object;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PacketContext {

    private final Channel channel;
    private ServerPlayer player;
    private String name;

    public PacketContext(ServerPlayer player) {
        this.name = player.getGameProfile().getName();
        this.player = player;
        this.channel = player.connection.connection.channel;
    }

    public PacketContext(Channel channel, String name) {
        this.name = name;
        this.channel = channel;
    }

    public PacketContext(Channel channel) {
        this.channel = channel;
    }

    private static @Nullable ServerPlayer byName(String name) {
        return MinecraftServer.getServer().getPlayerList().getPlayerByName(name);
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
        this.name = player.getGameProfile().getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<ServerPlayer> player() {
        if (player == null) {
            player = byName(name);
        }
        return Optional.ofNullable(player);
    }

    public Optional<String> name() {
        return Optional.ofNullable(name);
    }

    public void send(Packet<?> packet) {
        channel.writeAndFlush(packet);
    }

    public void send(String id, ByteBuf data) {
        channel.writeAndFlush(new ClientboundCustomPayloadPacket(new DiscardedPayload(ResourceLocation.parse(id), data.array())));
    }

    public void send(CustomPacketPayload payload) {
        channel.writeAndFlush(new ClientboundCustomPayloadPacket(payload));
    }
}