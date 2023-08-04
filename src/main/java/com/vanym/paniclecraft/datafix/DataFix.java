package com.vanym.paniclecraft.datafix;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixType;

public class DataFix implements ITypedFixableData {
    
    protected final int version;
    protected final Function<NBTTagCompound, NBTTagCompound> fix;
    protected final IFixType[] types;
    
    public DataFix(int version, IFixType type, Function<NBTTagCompound, NBTTagCompound> fix) {
        this(version, fix, type);
    }
    
    public DataFix(int version, Function<NBTTagCompound, NBTTagCompound> fix, IFixType... types) {
        this.version = version;
        this.fix = fix;
        this.types = types;
    }
    
    @Override
    public int getFixVersion() {
        return this.version;
    }
    
    @Override
    public List<IFixType> getTypes() {
        return Arrays.asList(this.types);
    }
    
    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
        return this.fix.apply(compound);
    }
    
    public static DataFix create(
            int version,
            IFixType type,
            Function<NBTTagCompound, NBTTagCompound> fix) {
        return new DataFix(version, type, fix);
    }
    
    public static DataFix create(
            int version,
            Function<NBTTagCompound, NBTTagCompound> fix,
            IFixType... types) {
        return new DataFix(version, fix, types);
    }
}
