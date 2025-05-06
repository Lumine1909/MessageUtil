package io.github.lumine1909.messageutil.object;

import io.github.lumine1909.messageutil.api.MessageReceiver;

import java.lang.reflect.Method;

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

    public void invoke(PacketContext context, PacketEvent event, Object object) {
        try {
            invoker.invoke(receiver, context, event, object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
