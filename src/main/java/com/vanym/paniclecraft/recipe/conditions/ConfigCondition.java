package com.vanym.paniclecraft.recipe.conditions;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Optional;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.gson.JsonObject;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.recipe.RecipeUtils;
import com.vanym.paniclecraft.utils.FileUtils;
import com.vanym.paniclecraft.utils.JUtils;

import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.FalseCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigCondition implements ICondition {
    
    public static final ModConfig.Type DEFAULT_TYPE = ModConfig.Type.SERVER;
    
    protected static final ResourceLocation NAME = new ResourceLocation(DEF.MOD_ID, "config");
    
    protected final String namespace;
    protected final ModConfig.Type type;
    protected final String path;
    
    protected final ICondition defaultValue;
    
    public ConfigCondition(String location, ICondition defaultValue) {
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
            this.type = DEFAULT_TYPE;
        } else {
            String str = path.substring(0, slash);
            this.type = Arrays.stream(ModConfig.Type.values())
                              .filter(t->str.equalsIgnoreCase(t.name()))
                              .findAny()
                              .orElseThrow(()->new IllegalArgumentException("Unknown config type"));
            path = path.substring(slash + 1);
        }
        this.path = path;
        this.defaultValue = defaultValue;
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
                      .map(this::getConfigValue)
                      .orElseGet(this.defaultValue::test);
    }
    
    protected Boolean getConfigValue(ModConfig config) {
        Optional<ForgeConfigSpec> spec = Optional.ofNullable(config.getSpec());
        Optional<CommentedConfig> data = FileUtils.earlyConfigLoad(config);
        return data.map(d->d.get(this.path))
                   .map(String::valueOf)
                   .map(Boolean::valueOf)
                   .orElseGet(()->spec.map(s->s.<ValueSpec>get(this.path))
                                      .map(ValueSpec::getDefault)
                                      .map(String::valueOf)
                                      .map(Boolean::valueOf)
                                      .orElse(null));
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
            json.add("default", CraftingHelper.serialize(value.defaultValue));
        }
        
        @Override
        public ConfigCondition read(JsonObject json) {
            return new ConfigCondition(
                    JSONUtils.getAsString(json, "location"),
                    Optional.ofNullable(RecipeUtils.getCondition(json.get("default")))
                            .orElse(FalseCondition.INSTANCE));
        }
        
        @Override
        public ResourceLocation getID() {
            return ConfigCondition.NAME;
        }
    }
}
