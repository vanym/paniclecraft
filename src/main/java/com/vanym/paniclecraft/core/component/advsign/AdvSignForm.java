package com.vanym.paniclecraft.core.component.advsign;

import java.util.Arrays;
import java.util.Locale;

import net.minecraft.util.IStringSerializable;

public enum AdvSignForm implements IStringSerializable {
    WALL, STICK_DOWN;
    
    @Override
    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
    
    public int getIndex() {
        return this.ordinal();
    }
    
    public static AdvSignForm byName(String name) {
        return Arrays.stream(values())
                     .filter(e->e.name().equalsIgnoreCase(name))
                     .findAny()
                     .orElse(null);
    }
    
    public static AdvSignForm byIndex(int index) {
        return values()[Math.abs(index) % values().length];
    }
}
