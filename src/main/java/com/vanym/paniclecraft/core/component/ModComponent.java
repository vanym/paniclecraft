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

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
    public void registerContainers(RegistryEvent.Register<ContainerType<?>> e) {
        this.getContainers().forEach(e.getRegistry()::register);
    }
    
    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> e) {
        this.getTileEntities().forEach(e.getRegistry()::register);
    }
    
    @Override
    public List<Item> getItems() {
        return this.getObjects(Item.class);
    }
    
    public List<Block> getBlocks() {
        return this.getObjects(Block.class);
    }
    
    @SuppressWarnings("rawtypes")
    public List<ContainerType> getContainers() {
        return this.getObjects(ContainerType.class);
    }
    
    @SuppressWarnings("rawtypes")
    public List<TileEntityType> getTileEntities() {
        return this.getObjects(TileEntityType.class);
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
