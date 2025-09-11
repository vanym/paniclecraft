package com.vanym.paniclecraft.utils;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class DistUtils {
    
    public static Side get() {
        return FMLCommonHandler.instance().getSide();
    }
    
    public static void run(Side on, Supplier<Runnable> toRun) {
        JUtils.runIf(get() == on, ()->toRun.get().run());
    }
    
    public static void crun(Supplier<Runnable> toRun) {
        run(Side.CLIENT, toRun);
    }
    
    public static void srun(Supplier<Runnable> toRun) {
        run(Side.SERVER, toRun);
    }
    
    public static <T> T call(Side on, Supplier<Callable<T>> toCall) {
        return JUtils.callIf(get() == on, ()->toCall.get().call());
    }
    
    public static <T> T ccall(Supplier<Callable<T>> toCall) {
        return call(Side.CLIENT, toCall);
    }
    
    public static <T> T scall(Supplier<Callable<T>> toCall) {
        return call(Side.SERVER, toCall);
    }
}
