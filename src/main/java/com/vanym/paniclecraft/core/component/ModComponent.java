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

import net.minecraft.item.Item;

public abstract class ModComponent implements IModComponent {
    
    @Override
    public List<Item> getItems() {
        return this.getObjects(Item.class);
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
