package com.vanym.paniclecraft.client.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatScreenUtils {
    
    protected static final String SCREEN_CLASSNAME = Screen.class.getCanonicalName();
    protected static final String SENDMESSAGE_BOOL_METHODNAME = "sendMessage";
    protected static final String SENDMESSAGE_METHODNAME = "sendMessage";
    
    public static boolean needToLogMessage() {
        Iterator<StackTraceElement> it =
                Arrays.stream(Thread.currentThread().getStackTrace()).limit(16).iterator();
        while (it.hasNext()) {
            StackTraceElement st1 = it.next();
            if (st1 == null || !Objects.equals(st1.getClassName(), SCREEN_CLASSNAME)) {
                continue;
            }
            if (!it.hasNext()) {
                return false;
            }
            StackTraceElement st2 = it.next();
            return st2 != null
                && SENDMESSAGE_BOOL_METHODNAME.equals(st1.getMethodName())
                && SENDMESSAGE_METHODNAME.equals(st2.getMethodName());
        }
        return false;
    }
}
