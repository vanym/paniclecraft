package com.vanym.paniclecraft.core;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

public class ModConfig extends Configuration {
    
    protected boolean requiresMcRestart = true;
    
    public ModConfig(File file) {
        super(file);
    }
    
    public void restartless() {
        this.requiresMcRestart = false;
    }
    
    public void restartlessReset() {
        this.requiresMcRestart = true;
    }
    
    protected void modifyProperty(Property prop) {
        prop.setRequiresMcRestart(this.requiresMcRestart);
    }
    
    @Override
    public Property get(
            String category,
            String key,
            String defaultValue,
            String comment,
            Property.Type type) {
        Property prop = super.get(category, key, defaultValue, comment, type);
        this.modifyProperty(prop);
        return prop;
    }
    
    @Override
    public Property get(
            String category,
            String key,
            String[] defaultValues,
            String comment,
            Property.Type type) {
        Property prop = super.get(category, key, defaultValues, comment, type);
        this.modifyProperty(prop);
        return prop;
    }
    
    public double getDouble(
            String name,
            String category,
            double defaultValue,
            double minValue,
            double maxValue,
            String comment) {
        return this.getDouble(name, category, defaultValue, minValue, maxValue, comment, name);
    }
    
    public double getDouble(
            String name,
            String category,
            double defaultValue,
            double minValue,
            double maxValue,
            String comment,
            String langKey) {
        Property prop = this.get(category, name, Double.toString(defaultValue), null, Type.DOUBLE);
        prop.setLanguageKey(langKey);
        prop.setComment(String.format("%s [range: %s ~ %s, default: %s]",
                                      comment, minValue, maxValue, defaultValue));
        prop.setMinValue(minValue);
        prop.setMaxValue(maxValue);
        if (!prop.isDoubleValue()) {
            prop.setValue(defaultValue);
        }
        return Math.min(maxValue, Math.max(minValue, prop.getDouble(defaultValue)));
    }
}
