package com.vanym.paniclecraft.network;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraftforge.fml.network.NetworkEvent;

public class NetworkUtils {
    
    public static <MSG> BiConsumer<MSG, Supplier<NetworkEvent.Context>> handle(
            BiConsumer<MSG, NetworkEvent.Context> handler) {
        return (msg, ctx)->handler.accept(msg, ctx.get());
    }
    
    public static <MSG> BiConsumer<MSG, Supplier<NetworkEvent.Context>> handleInWorld(
            BiConsumer<MSG, NetworkEvent.Context> handler) {
        return handle((msg, ctx)->ctx.enqueueWork(()->handler.accept(msg, ctx)));
    }
}
