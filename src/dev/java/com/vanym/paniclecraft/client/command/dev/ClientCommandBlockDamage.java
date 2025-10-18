package com.vanym.paniclecraft.client.command.dev;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import com.vanym.paniclecraft.client.command.ClientCommandBase;
import com.vanym.paniclecraft.command.TreeCommandBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientCommandBlockDamage extends TreeCommandBase {
    
    protected final DamagePlacer damagePlacer = new DamagePlacer();
    
    public ClientCommandBlockDamage() {
        this.addSubCommand(new CommandAdd());
        this.addSubCommand(new CommandRemove());
        this.addSubCommand(new CommandClear());
    }
    
    @Override
    public String getName() {
        return "blockdamage";
    }
    
    protected class CommandAdd extends ClientCommandBase {
        @Override
        public String getName() {
            return "add";
        }
        
        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args)
                throws CommandException {
            if (args.length > 4) {
                throw new WrongUsageException(this.getUsage(sender));
            }
            List<String> posArgs = Arrays.asList(args);
            int damage = 9;
            if (args.length == 1 || args.length == 4) {
                damage = parseInt(args[0], 0, 9);
                posArgs = posArgs.subList(1, posArgs.size());
            }
            BlockPos pos = this.getBlockTarget(sender, posArgs);
            World world = Minecraft.getMinecraft().getRenderManager().world;
            boolean added = ClientCommandBlockDamage.this.damagePlacer.add(world, pos, damage);
            TextComponentTranslation success = new TextComponentTranslation(
                    this.getTranslationPrefix() + (added ? ".added" : ".updated"),
                    damage,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ());
            sender.sendMessage(success);
        }
    }
    
    protected class CommandRemove extends ClientCommandBase {
        @Override
        public String getName() {
            return "remove";
        }
        
        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args)
                throws CommandException {
            if (args.length > 3) {
                throw new WrongUsageException(this.getUsage(sender));
            }
            BlockPos pos = this.getBlockTarget(sender, Arrays.asList(args));
            World world = Minecraft.getMinecraft().getRenderManager().world;
            boolean removed = ClientCommandBlockDamage.this.damagePlacer.remove(world, pos);
            TextComponentTranslation success = new TextComponentTranslation(
                    this.getTranslationPrefix() + (removed ? ".removed" : ".none"),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ());
            sender.sendMessage(success);
        }
    }
    
    protected class CommandClear extends ClientCommandBase {
        @Override
        public String getName() {
            return "clear";
        }
        
        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args)
                throws CommandException {
            if (args.length > 0) {
                throw new WrongUsageException(this.getUsage(sender));
            }
            World world = Minecraft.getMinecraft().getRenderManager().world;
            int cleared = ClientCommandBlockDamage.this.damagePlacer.clear(world);
            TextComponentTranslation success = new TextComponentTranslation(
                    this.getTranslationPrefix() + ".cleared",
                    cleared);
            sender.sendMessage(success);
        }
    }
    
    protected static class DamagePlacer {
        
        protected DamagePlacer() {}
        
        protected final WeakHashMap<World, Map<BlockPos, Integer>> damages =
                new WeakHashMap<>();
        
        protected long count = 0;
        
        protected boolean add(World world, BlockPos pos, int damage) {
            pos = new PosWrapper(pos);
            Integer i = this.damages.computeIfAbsent(world, w->new HashMap<>())
                                    .put(pos, damage);
            this.count = 0;
            MinecraftForge.EVENT_BUS.register(this);
            return i == null;
        }
        
        protected boolean remove(World world, BlockPos pos) {
            Map<BlockPos, Integer> map = this.damages.get(world);
            if (map == null) {
                return false;
            }
            pos = new PosWrapper(pos);
            Integer i = map.remove(pos);
            if (i == null) {
                return false;
            }
            if (world == Minecraft.getMinecraft().getRenderManager().world) {
                RenderGlobal rg = Minecraft.getMinecraft().renderGlobal;
                rg.sendBlockBreakProgress(pos.hashCode(), pos, -1);
            }
            if (!map.isEmpty()) {
                return true;
            }
            this.damages.remove(world);
            if (this.damages.isEmpty()) {
                MinecraftForge.EVENT_BUS.unregister(this);
            }
            return true;
        }
        
        protected int clear(World world) {
            int count = 0;
            if (world == Minecraft.getMinecraft().getRenderManager().world) {
                RenderGlobal rg = Minecraft.getMinecraft().renderGlobal;
                Map<BlockPos, Integer> map = this.damages.get(world);
                if (map != null) {
                    map.keySet().stream().forEach(p-> {
                        rg.sendBlockBreakProgress(p.hashCode(), p, -1);
                    });
                    count = map.size();
                }
            }
            this.damages.clear();
            MinecraftForge.EVENT_BUS.unregister(this);
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
                MinecraftForge.EVENT_BUS.unregister(this);
                return;
            }
            Map<BlockPos, Integer> map =
                    this.damages.get(Minecraft.getMinecraft().getRenderManager().world);
            if (map == null) {
                return;
            }
            RenderGlobal rg = Minecraft.getMinecraft().renderGlobal;
            map.entrySet()
               .stream()
               .forEach(e-> {
                   BlockPos pos = e.getKey();
                   int damage = e.getValue();
                   rg.sendBlockBreakProgress(pos.hashCode(), pos, damage);
               });
        }
        
        protected static class PosWrapper extends BlockPos {
            public PosWrapper(BlockPos pos) {
                super(pos);
            }
            
            @Override
            public int hashCode() {
                return -Math.abs(Objects.hash(this.getX(), this.getY(), this.getZ()));
            }
        }
    }
}
