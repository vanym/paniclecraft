package com.vanym.paniclecraft.core;

import java.util.HashMap;
import java.util.Map;

public class Shooter {
    
    protected final Map<String, Long> counters = new HashMap<>();
    
    public void once(Runnable action) {
        this.once(callerKey(), action);
    }
    
    public void once(String key, Runnable action) {
        this.shot(key, Long.MAX_VALUE, action);
    }
    
    public void shot(long timeout, Runnable action) {
        this.shot(callerKey(), timeout, action);
    }
    
    public void shot(String key, long timeout, Runnable action) {
        if (key != null) {
            synchronized (this) {
                Long last = this.counters.get(key);
                long now = System.nanoTime();
                if (last != null && (now - last) / 1000000 < timeout) {
                    return;
                }
                this.counters.put(key, now);
            }
        }
        action.run();
    }
    
    protected static String callerKey() {
        try {
            return Thread.currentThread().getStackTrace()[3].toString();
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
