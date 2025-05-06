package io.github.lumine1909.messageutil.core;

import io.github.lumine1909.messageutil.api.Codec;
import io.github.lumine1909.messageutil.api.MessageReceiver;
import io.github.lumine1909.messageutil.object.Holder;
import io.github.lumine1909.messageutil.object.PacketContext;
import io.github.lumine1909.messageutil.object.PacketEvent;
import io.github.lumine1909.messageutil.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class MessengerManager {

    public static final MessengerManager INSTANCE = new MessengerManager();

    private static final Map<String, Set<Holder<MessageReceiver.Payload>>> key2Payloads = new HashMap<>();
    private static final Map<String, Set<Holder<MessageReceiver.Bytebuf>>> key2Bytebuf = new HashMap<>();
    private static final Map<Class<? extends Packet<?>>, Set<Holder<MessageReceiver.Vanilla>>> key2Vanilla = new HashMap<>();
    private static final Comparator<Holder<?>> DESC_COMPARATOR = Comparator.comparing((Holder<?> h) -> h.priority()).reversed().thenComparing(Record::toString, Comparator.reverseOrder());

    private static final Map<MessageReceiver.Payload, StreamCodec<? extends ByteBuf, ?>> payloadCodecs = new HashMap<>();

    public MessengerManager() {

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

    public boolean handlePayload(PacketContext context, DiscardedPayload payload) {
        String id = payload.id().toString().toLowerCase();
        if (!key2Payloads.containsKey(id)) {
            return false;
        }
        PacketEvent event = new PacketEvent();
        for (Holder<MessageReceiver.Payload> holder : key2Payloads.get(id)) {
            if (holder.receiver().isActive() && holder.type().ignoreCancelled() || !event.isCancelled()) {
                holder.invoke(context, event, transferPayload(payload, payloadCodecs.get(holder.type())));
            }
        }
        return event.isCancelled();
    }

    public boolean handleBytebuf(PacketContext context, DiscardedPayload payload) {
        String id = payload.id().toString().toLowerCase();
        if (!key2Bytebuf.containsKey(id)) {
            return false;
        }
        PacketEvent event = new PacketEvent();
        for (Holder<MessageReceiver.Bytebuf> holder : key2Bytebuf.get(id)) {
            if (holder.receiver().isActive() && holder.type().ignoreCancelled() || !event.isCancelled()) {
                holder.invoke(context, event, ProtocolUtil.decorate(payload.data()));
            }
        }
        return event.isCancelled();
    }

    public boolean handleVanilla(PacketContext context, Packet<?> packet) {
        if (!key2Vanilla.containsKey(packet.getClass())) {
            return false;
        }
        PacketEvent event = new PacketEvent();
        for (Holder<MessageReceiver.Vanilla> holder : key2Vanilla.get(packet.getClass())) {
            if (holder.receiver().isActive() && holder.type().ignoreCancelled() || !event.isCancelled()) {
                holder.invoke(context, event, packet);
            }
        }
        return event.isCancelled();
    }
}
