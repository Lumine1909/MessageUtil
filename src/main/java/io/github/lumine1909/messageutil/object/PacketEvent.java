package io.github.lumine1909.messageutil.object;

public class PacketEvent {

    private boolean isCancelled;

    public PacketEvent(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public PacketEvent() {
        this(false);
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
