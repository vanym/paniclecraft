package com.vanym.paniclecraft.datafix;

import java.util.List;

import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.IFixableData;

public interface ITypedFixableData extends IFixableData {
    
    public List<IFixType> getTypes();
}
