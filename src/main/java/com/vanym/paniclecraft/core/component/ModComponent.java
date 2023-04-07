package com.vanym.paniclecraft.core.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.IWithCustomStateMapper;
import com.vanym.paniclecraft.item.IWithSubtypes;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ModComponent implements IModComponent {
    
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> e) {
        this.getItems().forEach(e.getRegistry()::register);
    }
    
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> e) {
        this.getBlocks().forEach(e.getRegistry()::register);
    }
    
    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> e) {
        this.getRecipes().forEach(e.getRegistry()::register);
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        this.getBlocks().forEach(this::registerModel);
        this.getItems().forEach(this::registerModel);
    }
    
    @SideOnly(Side.CLIENT)
    protected void registerModel(Block block) {
        if (block instanceof IWithCustomStateMapper) {
            IWithCustomStateMapper mapper = (IWithCustomStateMapper)block;
            ModelLoader.setCustomStateMapper(block, mapper.getStateMapper());
        }
    }
    
    @SideOnly(Side.CLIENT)
    protected void registerModel(Item item) {
        Stream<ImmutablePair<Integer, ResourceLocation>> st;
        if (item instanceof IWithSubtypes) {
            IWithSubtypes ty = (IWithSubtypes)item;
            st = ty.getSubtypes()
                   .entrySet()
                   .stream()
                   .map(e->ImmutablePair.of(e.getKey(), e.getValue()))
                   .map(e->ImmutablePair.of(e.left, new ResourceLocation(DEF.MOD_ID, e.right)));
        } else {
            st = Stream.of(ImmutablePair.of(0, item.getRegistryName()));
        }
        st.map(e->ImmutablePair.of(e.left, new ModelResourceLocation(e.right, "inventory")))
          .forEach(e->ModelLoader.setCustomModelResourceLocation(item, e.left, e.right));
    }
    
    @Override
    public List<Item> getItems() {
        return this.getObjects(Item.class);
    }
    
    public List<Block> getBlocks() {
        return this.getObjects(Block.class);
    }
    
    public List<IRecipe> getRecipes() {
        return this.getObjects(IRecipe.class);
    }
    
    protected final <T> List<T> getObjects(Class<T> clazz) {
        Builder<Class<?>> builder = Stream.builder();
        for (Class<?> current = this.getClass();
             current != ModComponent.class;
             current = current.getSuperclass()) {
            builder.add(current);
        }
        Comparator<Field> comparatorOrder =
                Comparator.comparing(f->f.getAnnotation(ModComponentObject.class).value());
        return builder.build()
                      .flatMap(c->Arrays.stream(c.getDeclaredFields()))
                      .filter(f->f.isAnnotationPresent(ModComponentObject.class))
                      .filter(f->clazz.isAssignableFrom(f.getType()))
                      .sorted(comparatorOrder)
                      .map(f-> {
                          try {
                              return f.get(this);
                          } catch (IllegalArgumentException | IllegalAccessException e) {
                              return null;
                          }
                      })
                      .filter(Objects::nonNull)
                      .map(clazz::cast)
                      .collect(Collectors.toList());
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    protected static @interface ModComponentObject {
        public int value() default 0;
    }
}
