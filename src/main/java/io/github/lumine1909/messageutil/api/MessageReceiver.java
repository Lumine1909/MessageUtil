package io.github.lumine1909.messageutil.api;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class MessageReceiver {

    public abstract boolean isActive();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Payload {
        String key();
        Class<?> codec();
        int priority() default 0;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Bytebuf {
        String key();
        int priority() default 0;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Vanilla {
        Class<? extends Packet<?>> packetType();
        int priority() default 0;
    }

}
