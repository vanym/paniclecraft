package com.vanym.paniclecraft.client.renderer;

import java.util.HashMap;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class RenderKVCache<K, V> {
    
    private long entryTimeout = 60 * 60;
    private long count = 0;
    
    private HashMap<K, V> actual = new HashMap<>();
    private HashMap<K, V> stale = new HashMap<>();
    
    public final void setTimeout(long framesAmount) {
        this.entryTimeout = framesAmount;
    }
    
    public final V obtain(K key) {
        V value;
        value = this.actual.get(key);
        if (value != null) {
            return value;
        }
        value = this.stale.get(key);
        if (value == null) {
            return null;
        }
        this.stale.remove(key, value);
        this.actual.put(key, value);
        return value;
    }
    
    public final void put(K key, V value) {
        this.actual.put(key, value);
    }
    
    @SubscribeEvent
    public void renderWorldLast(RenderWorldLastEvent event) {
        if (++this.count % this.entryTimeout != 0) {
            return;
        }
        HashMap<K, V> oldStale = this.stale;
        this.stale = this.actual;
        this.actual = new HashMap<>();
        oldStale.values().forEach(this::clear);
    }
    
    protected abstract void clear(V value);
}
