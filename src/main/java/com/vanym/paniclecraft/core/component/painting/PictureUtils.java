package com.vanym.paniclecraft.core.component.painting;

import java.util.Optional;
import java.util.concurrent.Callable;

import com.vanym.paniclecraft.utils.SideUtils;

public class PictureUtils {
    
    public static Object syncObject(Picture picture) {
        return Optional.ofNullable(picture)
                       .map(Picture::getHolder)
                       .filter(IPictureHolder::isProviderSyncRequired)
                       .map(IPictureHolder::getProvider)
                       .orElse(null);
    }
    
    public static void runSync(Picture picture, Runnable run) {
        SideUtils.runSync(syncObject(picture), run);
    }
    
    public static <R> R callSync(Picture picture, Callable<R> call) {
        return SideUtils.callSync(syncObject(picture), call);
    }
}
