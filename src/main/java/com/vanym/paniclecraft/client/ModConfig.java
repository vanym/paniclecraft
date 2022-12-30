package com.vanym.paniclecraft.client;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ModConfig extends Configuration {
    
    protected boolean requiresMcRestart = true;
    
    public ModConfig(File file) {
        super(file);
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
    
    public void restartless() {
        this.requiresMcRestart = false;
    }
    
    public void restartlessReset() {
        this.requiresMcRestart = true;
    }
    
    protected void modifyProperty(Property prop) {
        prop.setRequiresMcRestart(this.requiresMcRestart);
    }
}
