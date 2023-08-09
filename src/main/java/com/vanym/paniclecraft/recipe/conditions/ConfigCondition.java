package com.vanym.paniclecraft.recipe.conditions;

import java.util.Arrays;
import java.util.EnumMap;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.gson.JsonObject;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.utils.JUtils;

import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

public class ConfigCondition implements ICondition {
    
    protected static final ResourceLocation NAME = new ResourceLocation(DEF.MOD_ID, "config");
    
    protected final String namespace;
    protected final ModConfig.Type type;
    protected final String path;
    
    public ConfigCondition(String location) {
        String path = location;
        int colon = path.indexOf(":");
        if (colon == -1) {
            this.namespace = "minecraft";
        } else {
            this.namespace = path.substring(0, colon);
            path = path.substring(colon + 1);
        }
        int slash = path.indexOf('/');
        if (slash == -1) {
            this.type = Type.SERVER;
        } else {
            String str = path.substring(0, slash);
            this.type = Arrays.stream(ModConfig.Type.values())
                              .filter(t->str.equalsIgnoreCase(t.name()))
                              .findAny()
                              .orElseThrow(()->new IllegalArgumentException("Unknown config type"));
            path = path.substring(slash + 1);
        }
        this.path = path;
    }
    
    @Override
    public ResourceLocation getID() {
        return NAME;
    }
    
    @Override
    public boolean test() {
        return ModList.get()
                      .getModContainerById(this.namespace)
                      .map(mc->JUtils.trap(()->ObfuscationReflectionHelper.<
                          EnumMap<ModConfig.Type, ModConfig>,
                          ModContainer>getPrivateValue(ModContainer.class, mc, "configs")))
                      .map(configs->configs.get(this.type))
                      .map(config-> {
                          ForgeConfigSpec spec = config.getSpec();
                          CommentedConfig data = config.getConfigData();
                          return data.getOrElse(this.path, ()->spec.get(this.path));
                      })
                      .map(String::valueOf)
                      .map(Boolean::valueOf)
                      .orElse(false);
    }
    
    public String getLocation() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.namespace);
        sb.append(':');
        sb.append(this.type.name().toLowerCase());
        sb.append('/');
        sb.append(this.path);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("config(\"%s\")", this.getLocation());
    }
    
    public static class Serializer implements IConditionSerializer<ConfigCondition> {
        public static final Serializer INSTANCE = new Serializer();
        
        @Override
        public void write(JsonObject json, ConfigCondition value) {
            json.addProperty("location", value.getLocation());
        }
        
        @Override
        public ConfigCondition read(JsonObject json) {
            return new ConfigCondition(JSONUtils.getString(json, "location"));
        }
        
        @Override
        public ResourceLocation getID() {
            return ConfigCondition.NAME;
        }
    }
}
