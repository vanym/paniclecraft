package com.vanym.paniclecraft.client.command.dev;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import com.vanym.paniclecraft.client.command.ClientCommandBase;
import com.vanym.paniclecraft.command.TreeCommandBase;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class ClientCommandBlockDamage extends TreeCommandBase {
    
    protected final DamagePlacer damagePlacer = new DamagePlacer();
    
    public ClientCommandBlockDamage() {
        this.addSubCommand(new CommandAdd());
        this.addSubCommand(new CommandRemove());
        this.addSubCommand(new CommandClear());
    }
    
    @Override
    public String getCommandName() {
        return "blockdamage";
    }
    
    protected class CommandAdd extends ClientCommandBase {
        @Override
        public String getCommandName() {
            return "add";
        }
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 4) {
                throw new WrongUsageException(this.getCommandUsage(sender));
            }
            List<String> posArgs = Arrays.asList(args);
            int damage = 9;
            if (args.length == 1 || args.length == 4) {
                damage = parseIntBounded(sender, args[0], 0, 9);
                posArgs = posArgs.subList(1, posArgs.size());
            }
            ChunkCoordinates pos = this.getBlockTarget(sender, posArgs);
            World world = RenderManager.instance.worldObj;
            boolean added = ClientCommandBlockDamage.this.damagePlacer.add(world, pos, damage);
            ChatComponentTranslation success = new ChatComponentTranslation(
                    this.getTranslationPrefix() + (added ? ".added" : ".updated"),
                    damage,
                    pos.posX,
                    pos.posY,
                    pos.posZ);
            sender.addChatMessage(success);
        }
    }
    
    protected class CommandRemove extends ClientCommandBase {
        @Override
        public String getCommandName() {
            return "remove";
        }
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 3) {
                throw new WrongUsageException(this.getCommandUsage(sender));
            }
            ChunkCoordinates pos = this.getBlockTarget(sender, Arrays.asList(args));
            World world = RenderManager.instance.worldObj;
            boolean removed = ClientCommandBlockDamage.this.damagePlacer.remove(world, pos);
            ChatComponentTranslation success = new ChatComponentTranslation(
                    this.getTranslationPrefix() + (removed ? ".removed" : ".none"),
                    pos.posX,
                    pos.posY,
                    pos.posZ);
            sender.addChatMessage(success);
        }
    }
    
    protected class CommandClear extends ClientCommandBase {
        @Override
        public String getCommandName() {
            return "clear";
        }
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                throw new WrongUsageException(this.getCommandUsage(sender));
            }
            World world = RenderManager.instance.worldObj;
            int cleared = ClientCommandBlockDamage.this.damagePlacer.clear(world);
            ChatComponentTranslation success = new ChatComponentTranslation(
                    this.getTranslationPrefix() + ".cleared",
                    cleared);
            sender.addChatMessage(success);
        }
    }
    
    protected static class DamagePlacer {
        
        protected DamagePlacer() {}
        
        protected final WeakHashMap<World, Map<ChunkCoordinates, Integer>> damages =
                new WeakHashMap<>();
        
        protected long count = 0;
        
        protected boolean add(World world, ChunkCoordinates pos, int damage) {
            pos = new PosWrapper(pos);
            Integer i = this.damages.computeIfAbsent(world, w->new HashMap<>())
                                    .put(pos, damage);
            this.count = 0;
            FMLCommonHandler.instance().bus().register(this);
            return i == null;
        }
        
        protected boolean remove(World world, ChunkCoordinates pos) {
            Map<ChunkCoordinates, Integer> map = this.damages.get(world);
            if (map == null) {
                return false;
            }
            pos = new PosWrapper(pos);
            Integer i = map.remove(pos);
            if (i == null) {
                return false;
            }
            if (world == RenderManager.instance.worldObj) {
                RenderGlobal rg = Minecraft.getMinecraft().renderGlobal;
                rg.destroyBlockPartially(pos.hashCode(), pos.posX, pos.posY, pos.posZ, -1);
            }
            if (!map.isEmpty()) {
                return true;
            }
            this.damages.remove(world);
            if (this.damages.isEmpty()) {
                FMLCommonHandler.instance().bus().unregister(this);
            }
            return true;
        }
        
        protected int clear(World world) {
            int count = 0;
            if (world == RenderManager.instance.worldObj) {
                RenderGlobal rg = Minecraft.getMinecraft().renderGlobal;
                Map<ChunkCoordinates, Integer> map = this.damages.get(world);
                if (map != null) {
                    map.keySet().stream().forEach(p-> {
                        rg.destroyBlockPartially(p.hashCode(), p.posX, p.posY, p.posZ, -1);
                    });
                    count = map.size();
                }
            }
            this.damages.clear();
            FMLCommonHandler.instance().bus().unregister(this);
            return count;
        }
        
        @SubscribeEvent(priority = EventPriority.LOW)
        public void clientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.START) {
                return;
            }
            long i = this.count++;
            if (i % 20 != 0) {
                return;
            }
            if (this.damages.isEmpty()) {
                FMLCommonHandler.instance().bus().unregister(this);
                return;
            }
            Map<ChunkCoordinates, Integer> map = this.damages.get(RenderManager.instance.worldObj);
            if (map == null) {
                return;
            }
            RenderGlobal rg = Minecraft.getMinecraft().renderGlobal;
            map.entrySet()
               .stream()
               .forEach(e-> {
                   ChunkCoordinates pos = e.getKey();
                   int damage = e.getValue();
                   rg.destroyBlockPartially(pos.hashCode(), pos.posX, pos.posY, pos.posZ, damage);
               });
        }
        
        protected static class PosWrapper extends ChunkCoordinates {
            public PosWrapper(ChunkCoordinates pos) {
                super(pos);
            }
            
            @Override
            public int hashCode() {
                return -Math.abs(Objects.hash(this.posX, this.posY, this.posZ));
            }
        }
    }
}
