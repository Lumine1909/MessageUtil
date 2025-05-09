package io.github.lumine1909.messageutil.object;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PacketContext {

    public static Cache<String, PacketContext> CONTEXT_CACHE = CacheBuilder.newBuilder().build();

    private final Channel channel;
    private ServerPlayer player;
    private String name;
    private final String interceptor;

    public PacketContext(ServerPlayer player, String interceptor) {
        this.name = player.getGameProfile().getName();
        this.player = player;
        this.channel = player.connection.connection.channel;
        this.interceptor = interceptor;
        CONTEXT_CACHE.put(name, this);
    }

    public PacketContext(ServerPlayer notPreparedPlayer, Connection connection, String interceptor) {
        this.player = notPreparedPlayer;
        this.channel = connection.channel;
        this.name = notPreparedPlayer.getGameProfile().getName();
        this.interceptor = interceptor;
        CONTEXT_CACHE.put(name, this);
    }

    public PacketContext(Channel channel, String name, String interceptor) {
        this.name = name;
        this.channel = channel;
        this.interceptor = interceptor;
        CONTEXT_CACHE.put(name, this);
    }

    public PacketContext(Channel channel, String interceptor) {
        this.channel = channel;
        this.interceptor = interceptor;
    }

    private static @Nullable ServerPlayer byName(String name) {
        return MinecraftServer.getServer().getPlayerList().getPlayerByName(name);
    }

    public String interceptor() {
        return interceptor;
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
        this.name = player.getGameProfile().getName();
    }

    public void setName(String name) {
        this.name = name;
        CONTEXT_CACHE.put(name, this);
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

    public void send(String id, byte[] data) {
        channel.writeAndFlush(new ClientboundCustomPayloadPacket(new DiscardedPayload(ResourceLocation.parse(id), data)));
    }

    public void send(String id, ByteBuf data) {
        channel.writeAndFlush(new ClientboundCustomPayloadPacket(new DiscardedPayload(ResourceLocation.parse(id), data.array())));
    }

    public void send(CustomPacketPayload payload) {
        channel.writeAndFlush(new ClientboundCustomPayloadPacket(payload));
    }
}