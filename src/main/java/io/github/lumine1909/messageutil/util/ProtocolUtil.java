package io.github.lumine1909.messageutil.util;

import io.github.lumine1909.messageutil.object.PacketContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;

public class ProtocolUtil {

    public static void send(String name, Packet<?> packet) {
        PacketContext context;
        if ((context = PacketContext.CONTEXT_CACHE.getIfPresent(name)) != null) {
            context.send(packet);
        }
    }

    public static void send(String name, String id, byte[] data) {
        PacketContext context;
        if ((context = PacketContext.CONTEXT_CACHE.getIfPresent(name)) != null) {
            context.send(id, data);
        }
    }

    public static void send(String name, String id, ByteBuf data) {
        PacketContext context;
        if ((context = PacketContext.CONTEXT_CACHE.getIfPresent(name)) != null) {
            context.send(id, data);
        }
    }

    public static void send(String name, CustomPacketPayload payload) {
        PacketContext context;
        if ((context = PacketContext.CONTEXT_CACHE.getIfPresent(name)) != null) {
            context.send(payload);
        }
    }

    public static RegistryFriendlyByteBuf decorate(byte[] data) {
        return registry(Unpooled.wrappedBuffer(data));
    }

    public static RegistryFriendlyByteBuf registry(ByteBuf buf) {
        if (buf instanceof RegistryFriendlyByteBuf) {
            return (RegistryFriendlyByteBuf) buf;
        }
        return new RegistryFriendlyByteBuf(buf, MinecraftServer.getServer().registryAccess());
    }

    public static FriendlyByteBuf friendly(ByteBuf buf) {
        if (buf instanceof FriendlyByteBuf) {
            return (FriendlyByteBuf) buf;
        }
        return new FriendlyByteBuf(buf);
    }
}
