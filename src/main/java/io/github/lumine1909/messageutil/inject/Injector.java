package io.github.lumine1909.messageutil.inject;

public interface Injector {

    void inject();

    void uninject();

    boolean isInjected();
}
