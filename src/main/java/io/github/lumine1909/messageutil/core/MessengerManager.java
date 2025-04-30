package io.github.lumine1909.messageutil.core;

import io.github.lumine1909.messageutil.api.Codec;
import io.github.lumine1909.messageutil.api.MessageReceiver;
import io.github.lumine1909.messageutil.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class MessengerManager {

    public static final MessengerManager INSTANCE = new MessengerManager();

    private static final Map<String, Set<Holder<MessageReceiver.Payload>>> key2Payloads = new HashMap<>();
    private static final Map<String, Set<Holder<MessageReceiver.Bytebuf>>> key2Bytebuf = new HashMap<>();
    private static final Map<Class<? extends Packet<?>>, Set<Holder<MessageReceiver.Vanilla>>> key2Vanilla = new HashMap<>();
    private static final Comparator<Holder<?>> DESC_COMPARATOR = (a, b) -> Integer.compare(b.priority(), a.priority());
    private static final Map<MessageReceiver.Payload, StreamCodec<? extends ByteBuf, ?>> payloadCodecs = new HashMap<>();

    public MessengerManager() {

    }

    public void register(MessageReceiver receiver) {
        Class<?> clazz = receiver.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);

            if (method.isAnnotationPresent(MessageReceiver.Payload.class)) {
                MessageReceiver.Payload annotation = method.getAnnotation(MessageReceiver.Payload.class);
                extractCodec(annotation);
                key2Payloads.computeIfAbsent(annotation.key(), k -> new TreeSet<>(DESC_COMPARATOR)).add(Holder.of(annotation, method, receiver));
            }
            if (method.isAnnotationPresent(MessageReceiver.Bytebuf.class)) {
                MessageReceiver.Bytebuf annotation = method.getAnnotation(MessageReceiver.Bytebuf.class);
                key2Bytebuf.computeIfAbsent(annotation.key(), k -> new TreeSet<>(DESC_COMPARATOR)).add(Holder.of(annotation, method, receiver));
            }
            if (method.isAnnotationPresent(MessageReceiver.Vanilla.class)) {
                MessageReceiver.Vanilla annotation = method.getAnnotation(MessageReceiver.Vanilla.class);
                key2Vanilla.computeIfAbsent(annotation.packetType(), k -> new TreeSet<>(DESC_COMPARATOR)).add(Holder.of(annotation, method, receiver));
            }
        }
    }

    public void unregister(MessageReceiver receiver) {
        Class<?> clazz = receiver.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);

            if (method.isAnnotationPresent(MessageReceiver.Payload.class)) {
                MessageReceiver.Payload annotation = method.getAnnotation(MessageReceiver.Payload.class);
                payloadCodecs.remove(annotation);
                key2Payloads.get(annotation.key()).remove(Holder.of(annotation, method, receiver));
            }
            if (method.isAnnotationPresent(MessageReceiver.Bytebuf.class)) {
                MessageReceiver.Bytebuf annotation = method.getAnnotation(MessageReceiver.Bytebuf.class);
                key2Bytebuf.get(annotation.key()).remove(Holder.of(annotation, method, receiver));
            }
            if (method.isAnnotationPresent(MessageReceiver.Vanilla.class)) {
                MessageReceiver.Vanilla annotation = method.getAnnotation(MessageReceiver.Vanilla.class);
                key2Vanilla.get(annotation.packetType()).remove(Holder.of(annotation, method, receiver));
            }
        }
    }

    public void handlePayload(ServerPlayer player, DiscardedPayload payload) {
        String id = payload.id().toString().toLowerCase();
        if (!key2Payloads.containsKey(id)) {
            return;
        }
        for (Holder<MessageReceiver.Payload> holder : key2Payloads.get(id)) {
            holder.invoke(player, transferPayload(payload, payloadCodecs.get(holder.type)));
        }
    }

    public void handleBytebuf(ServerPlayer player, DiscardedPayload payload) {
        String id = payload.id().toString().toLowerCase();
        if (!key2Bytebuf.containsKey(id)) {
            return;
        }
        for (Holder<MessageReceiver.Bytebuf> holder : key2Bytebuf.get(id)) {
            holder.invoke(player, ProtocolUtil.decorate(payload.data()));
        }
    }

    public void handleVanilla(ServerPlayer player, Packet<?> packet) {
        if (!key2Vanilla.containsKey(packet.getClass())) {
            return;
        }
        for (Holder<MessageReceiver.Vanilla> holder : key2Vanilla.get(packet.getClass())) {
            holder.invoke(player, packet);
        }
    }

    @SuppressWarnings("unchecked")
    private static <B, V> V transferPayload(DiscardedPayload payload, StreamCodec<B, V> codec) {
        return codec.decode((B) ProtocolUtil.decorate(payload.data()));
    }

    @SuppressWarnings("unchecked")
    private static void extractCodec(MessageReceiver.Payload annotation) {
        Class<?> payloadClass = annotation.codec();
        for (Field field : payloadClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Codec.class)) {
                try {
                    payloadCodecs.put(annotation, (StreamCodec<? extends ByteBuf, ?>) field.get(null));
                    return;
                } catch (Exception ignored) {
                }
            }
        }
    }


    public record Holder<T>(T type, Method invoker, MessageReceiver receiver) {

        public static <T> Holder<T> of(T type, Method invoker, MessageReceiver receiver) {
            return new Holder<>(type, invoker, receiver);
        }

        public int priority() {
            if (type instanceof MessageReceiver.Payload p) {
                return p.priority();
            }
            if (type instanceof MessageReceiver.Bytebuf b) {
                return b.priority();
            }
            if (type instanceof MessageReceiver.Vanilla v) {
                return v.priority();
            }
            return 0;
        }

        public void invoke(ServerPlayer player, Object... objects) {
            try {
                invoker.invoke(receiver, player, objects);
            } catch (Exception ignored) {
            }
        }
    }
}
