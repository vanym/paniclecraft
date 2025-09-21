package com.vanym.paniclecraft.core;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.WeakHashMap;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.vanym.paniclecraft.utils.JUtils;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class EarlyConfigLoader {
    
    public static final FolderName SERVERCONFIG =
            JUtils.trap(()->ObfuscationReflectionHelper.getPrivateValue(ServerLifecycleHooks.class,
                                                                        null, "SERVERCONFIG"),
                        ()->new FolderName("serverconfig"));
    
    protected final WeakHashMap<ModConfig, CommentedConfig> cache = new WeakHashMap<>();
    
    public Optional<CommentedConfig> load(ModConfig config) {
        // Checking for CommentedFileConfig to avoid using config left by server
        if (config.getConfigData() instanceof CommentedFileConfig
            || config.getType() != ModConfig.Type.SERVER) {
            return Optional.ofNullable(config.getConfigData());
        }
        return Optional.ofNullable(SideUtils.callSync(this.cache, ()-> {
            return this.cache.computeIfAbsent(config, EarlyConfigLoader::loadRaw);
        }))
                       .map(JUtils.peek(data-> {
                           ModList.get()
                                  .getModContainerById(config.getModId())
                                  .filter(FMLModContainer.class::isInstance)
                                  .map(FMLModContainer.class::cast)
                                  .map(FMLModContainer::getEventBus)
                                  .ifPresent(bus->bus.register(this));
                       }));
    }
    
    @SubscribeEvent
    protected void modConfigLoad(ModConfig.ModConfigEvent event) {
        synchronized (this.cache) {
            this.cache.remove(event.getConfig());
        }
    }
    
    protected static CommentedFileConfig loadRaw(ModConfig config) {
        return getServerConfigPath().map(path->JUtils.trap(()-> {
            Path file = path.resolve(config.getFileName());
            CommentedFileConfig configData = CommentedFileConfig.builder(file).build();
            configData.load();
            configData.close();
            return configData;
        })).orElse(null);
    }
    
    public static Optional<Path> getServerConfigPath() {
        return getWorldPath().map(p->p.resolve(SERVERCONFIG.getId()));
    }
    
    public static Optional<Path> getWorldPath() {
        return JUtils.takeFirstOptional(EarlyConfigLoader::getWorldPathFromServer,
                                        EarlyConfigLoader::getWorldPathFromResourcePackList);
    }
    
    protected static Optional<Path> getWorldPathFromServer() {
        return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer())
                       .map(s->s.getWorldPath(FolderName.ROOT));
    }
    
    protected static Optional<Path> getWorldPathFromResourcePackList() {
        return Optional.<ResourcePackList>ofNullable(JUtils.trap(()-> {
            return ObfuscationReflectionHelper.getPrivateValue(ResourcePackLoader.class, null,
                                                               "resourcePackList");
        }))
                       .map(list->list.sources)
                       .flatMap(s->s.stream()
                                    .filter(FolderPackFinder.class::isInstance)
                                    .map(FolderPackFinder.class::cast)
                                    .map(finder->finder.folder)
                                    .map(File::toPath)
                                    .map(Path::getParent)
                                    .findFirst());
    }
}
