package com.vanym.paniclecraft.utils;

import java.util.concurrent.Callable;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class SideUtils {
    
    public static void runSync(Side syncOn, Object sync, Runnable run) {
        runSync(FMLCommonHandler.instance().getEffectiveSide() == syncOn, sync, run);
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
    
    public static <R> R callSync(Side syncOn, Object sync, Callable<R> call) {
        return callSync(FMLCommonHandler.instance().getEffectiveSide() == syncOn, sync, call);
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
