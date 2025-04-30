package io.github.lumine1909.messageutil.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;

public class ProtocolUtil {

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
