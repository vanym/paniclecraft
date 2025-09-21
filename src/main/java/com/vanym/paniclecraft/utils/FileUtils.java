package com.vanym.paniclecraft.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Optional;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class FileUtils {
    
    public static final FolderName SERVERCONFIG =
            JUtils.trap(()->ObfuscationReflectionHelper.getPrivateValue(ServerLifecycleHooks.class,
                                                                        null, "SERVERCONFIG"),
                        ()->new FolderName("serverconfig"));
    
    public static Optional<CommentedConfig> earlyConfigLoad(ModConfig config) {
        // Checking for CommentedFileConfig to avoid using config left by server
        if (config.getConfigData() instanceof CommentedFileConfig
            || config.getType() != ModConfig.Type.SERVER) {
            return Optional.ofNullable(config.getConfigData());
        }
        return getServerConfigPath().map(path->JUtils.trap(()-> {
            Path file = path.resolve(config.getFileName());
            CommentedFileConfig configData = CommentedFileConfig.builder(file).build();
            configData.load();
            configData.close();
            JUtils.trap(()-> {
                Method method =
                        ObfuscationReflectionHelper.findMethod(ModConfig.class, "setConfigData",
                                                               CommentedConfig.class);
                method.invoke(config, configData);
            });
            return configData;
        }));
    }
    
    public static Optional<Path> getServerConfigPath() {
        return getWorldPath().map(p->p.resolve(SERVERCONFIG.getId()));
    }
    
    public static Optional<Path> getWorldPath() {
        return JUtils.takeFirstOptional(FileUtils::getWorldPathFromServer,
                                        FileUtils::getWorldPathFromResourcePackList);
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
