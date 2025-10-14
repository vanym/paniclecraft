package com.vanym.paniclecraft.utils;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class DistUtils {
    
    public static Dist get() {
        return FMLEnvironment.dist;
    }
    
    public static void run(Dist on, Supplier<Runnable> toRun) {
        JUtils.runIf(get() == on, ()->toRun.get().run());
    }
    
    public static void crun(Supplier<Runnable> toRun) {
        run(Dist.CLIENT, toRun);
    }
    
    public static void srun(Supplier<Runnable> toRun) {
        run(Dist.DEDICATED_SERVER, toRun);
    }
    
    public static <T> T call(Dist on, Supplier<Callable<T>> toCall) {
        return JUtils.callIf(get() == on, ()->toCall.get().call());
    }
    
    public static <T> T ccall(Supplier<Callable<T>> toCall) {
        return call(Dist.CLIENT, toCall);
    }
    
    public static <T> T scall(Supplier<Callable<T>> toCall) {
        return call(Dist.DEDICATED_SERVER, toCall);
    }
    
    public static <T> T call(Supplier<Callable<T>> clientCall, Supplier<Callable<T>> serverCall) {
        if (get() == Dist.CLIENT) {
            return JUtils.call(()->clientCall.get().call());
        } else {
            return JUtils.call(()->serverCall.get().call());
        }
    }
}
