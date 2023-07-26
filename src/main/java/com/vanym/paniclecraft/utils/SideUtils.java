package com.vanym.paniclecraft.utils;

import java.util.concurrent.Callable;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class SideUtils {
    
    public static void runSync(LogicalSide syncOn, Object sync, Runnable run) {
        runSync(EffectiveSide.get() == syncOn, sync, run);
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
        return callSync(EffectiveSide.get() == syncOn, sync, call);
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
