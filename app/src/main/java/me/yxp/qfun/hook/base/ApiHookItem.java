package me.yxp.qfun.hook.base;

import java.util.HashSet;
import java.util.Set;

public abstract class ApiHookItem {

    public Set<Listener> mListenerSet = new HashSet<>();

    public abstract void loadHook() throws Throwable;

    public final void addListener(Listener listener) {
        mListenerSet.add(listener);
    }

    public final void removeListener(Listener listener) {
        mListenerSet.remove(listener);
    }

    public interface Listener {
    }

}

