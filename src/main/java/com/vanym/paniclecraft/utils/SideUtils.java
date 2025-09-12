package com.vanym.paniclecraft.utils;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class SideUtils {
    
    public static LogicalSide get() {
        return EffectiveSide.get();
    }
    
    public static void run(LogicalSide on, Supplier<Runnable> toRun) {
        JUtils.runIf(get() == on, ()->toRun.get().run());
    }
    
    public static void crun(Supplier<Runnable> toRun) {
        run(LogicalSide.CLIENT, toRun);
    }
    
    public static void srun(Supplier<Runnable> toRun) {
        run(LogicalSide.SERVER, toRun);
    }
    
    public static <T> T call(LogicalSide on, Supplier<Callable<T>> toCall) {
        return JUtils.callIf(get() == on, ()->toCall.get().call());
    }
    
    public static <T> T ccall(Supplier<Callable<T>> toCall) {
        return call(LogicalSide.CLIENT, toCall);
    }
    
    public static <T> T scall(Supplier<Callable<T>> toCall) {
        return call(LogicalSide.SERVER, toCall);
    }
    
    public static void runSync(LogicalSide syncOn, Object sync, Runnable run) {
        runSync(get() == syncOn, sync, run);
    }
    
    public static void runSync(Object sync, Runnable run) {
        runSync(sync != null, sync, run);
    }
    
    public static void runSync(boolean doSync, Object sync, Runnable run) {
        if (doSync) {
            synchronized (sync) {
                run.run();
            }
        } else {
            run.run();
        }
    }
    
    public static <R> R callSync(LogicalSide syncOn, Object sync, Callable<R> call) {
        return callSync(get() == syncOn, sync, call);
    }
    
    public static <R> R callSync(Object sync, Callable<R> call) {
        return callSync(sync != null, sync, call);
    }
    
    public static <R> R callSync(boolean doSync, Object sync, Callable<R> call) {
        try {
            if (doSync) {
                synchronized (sync) {
                    return call.call();
                }
            }
            return call.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
