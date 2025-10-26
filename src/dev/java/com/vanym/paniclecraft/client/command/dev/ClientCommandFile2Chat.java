package com.vanym.paniclecraft.client.command.dev;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.vanym.paniclecraft.client.command.ClientCommandBase;
import com.vanym.paniclecraft.command.TreeCommandBase;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.client.ClientCommandHandler;

@SideOnly(Side.CLIENT)
public class ClientCommandFile2Chat extends TreeCommandBase {
    
    protected File2ChatReader reader;
    
    public ClientCommandFile2Chat() {
        this.addSubCommand(new CommandOpen());
        this.addSubCommand(new CommandStatus());
        this.addSubCommand(new CommandClose());
    }
    
    @Override
    public String getCommandName() {
        return "file2chat";
    }
    
    protected class CommandOpen extends ClientCommandBase {
        
        @Override
        public String getCommandName() {
            return "open";
        }
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length == 0) {
                throw new WrongUsageException(this.getCommandUsage(sender));
            }
            File file = new File(String.join(" ", args));
            if (!file.exists()) {
                throw new CommandException(
                        this.getTranslationPrefix() + ".notfound",
                        file.getAbsolutePath());
            }
            if (ClientCommandFile2Chat.this.reader != null
                && ClientCommandFile2Chat.this.reader.stop()) {
                sender.addChatMessage(new ChatComponentTranslation(
                        this.getTranslationPrefix() + ".previous_closed",
                        ClientCommandFile2Chat.this.reader.getFile().getAbsolutePath()));
            }
            ClientCommandFile2Chat.this.reader = new File2ChatReader(file).start();
            sender.addChatMessage(new ChatComponentTranslation(
                    this.getTranslationPrefix() + ".opened",
                    file.getAbsolutePath()));
        }
    }
    
    protected class CommandStatus extends ClientCommandBase {
        
        @Override
        public String getCommandName() {
            return "status";
        }
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                throw new WrongUsageException(this.getCommandUsage(sender));
            }
            if (ClientCommandFile2Chat.this.reader == null) {
                ChatComponentTranslation none = new ChatComponentTranslation(
                        this.getTranslationPrefix() + ".none");
                sender.addChatMessage(none);
                return;
            }
            sender.addChatMessage(new ChatComponentTranslation(
                    this.getTranslationPrefix() + ".file",
                    ClientCommandFile2Chat.this.reader.getFile().getAbsoluteFile()));
            sender.addChatMessage(new ChatComponentTranslation(
                    this.getTranslationPrefix() + ".state." +
                        ClientCommandFile2Chat.this.reader.getState()
                                                          .toString()
                                                          .toLowerCase(Locale.ROOT)));
            sender.addChatMessage(new ChatComponentTranslation(
                    this.getTranslationPrefix() + ".count",
                    ClientCommandFile2Chat.this.reader.getCount()));
            if (ClientCommandFile2Chat.this.reader.exception != null) {
                sender.addChatMessage(new ChatComponentTranslation(
                        this.getTranslationPrefix() + ".error",
                        ClientCommandFile2Chat.this.reader.exception.getMessage()));
            }
        }
    }
    
    protected class CommandClose extends ClientCommandBase {
        
        @Override
        public String getCommandName() {
            return "close";
        }
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                throw new WrongUsageException(this.getCommandUsage(sender));
            }
            if (ClientCommandFile2Chat.this.reader == null) {
                ChatComponentTranslation none = new ChatComponentTranslation(
                        this.getTranslationPrefix() + ".none");
                sender.addChatMessage(none);
                return;
            }
            if (ClientCommandFile2Chat.this.reader.stop()) {
                ChatComponentTranslation success = new ChatComponentTranslation(
                        this.getTranslationPrefix() + ".closed",
                        ClientCommandFile2Chat.this.reader.getFile().getAbsolutePath());
                sender.addChatMessage(success);
            } else {
                ChatComponentTranslation already = new ChatComponentTranslation(
                        this.getTranslationPrefix() + ".already_closed",
                        ClientCommandFile2Chat.this.reader.getFile().getAbsolutePath());
                sender.addChatMessage(already);
            }
        }
    }
    
    protected static class File2ChatReader implements Runnable {
        
        protected final Thread thread = new Thread(this, this.getClass().getSimpleName());
        protected final File file;
        
        protected final Queue<String> queue = new ConcurrentLinkedQueue<>();
        
        protected State state = State.INITIALIZED;
        protected Exception exception;
        protected int count = 0;
        
        public File2ChatReader(File file) {
            this.file = Objects.requireNonNull(file);
        }
        
        @SubscribeEvent(priority = EventPriority.LOW)
        public void clientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.START) {
                return;
            }
            while (true) {
                String line = this.queue.poll();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                ++this.count;
                Minecraft mc = Minecraft.getMinecraft();
                if (ClientCommandHandler.instance.executeCommand(mc.thePlayer, line) != 0) {
                    continue;
                }
                mc.thePlayer.sendChatMessage(line);
            }
            if (!this.thread.isAlive()) {
                FMLCommonHandler.instance().bus().unregister(this);
            }
        }
        
        public File getFile() {
            return this.file;
        }
        
        public int getCount() {
            return this.count;
        }
        
        public synchronized State getState() {
            return this.state;
        }
        
        public synchronized Exception getException() {
            return this.exception;
        }
        
        public File2ChatReader start() {
            this.state = State.STARTING;
            this.thread.start();
            FMLCommonHandler.instance().bus().register(this);
            return this;
        }
        
        public boolean stop() {
            if (!this.thread.isAlive()) {
                return false;
            }
            this.thread.interrupt();
            synchronized (this) {
                this.state = State.INTERRUPTED;
            }
            return true;
        }
        
        @Override
        public void run() {
            FileChannel ch;
            synchronized (this) {
                this.state = State.OPENING;
            }
            try {
                ch = FileChannel.open(this.file.toPath());
            } catch (IOException e) {
                synchronized (this) {
                    this.exception = e;
                    this.state = State.ERROR;
                    return;
                }
            }
            if (Thread.interrupted()) {
                synchronized (this) {
                    this.state = State.INTERRUPTED;
                }
                return;
            }
            synchronized (this) {
                this.state = State.READING;
            }
            try {
                ByteBuffer buf = ByteBuffer.allocate(1024);
                ByteArrayOutputStream sb = new ByteArrayOutputStream();
                while (ch.read(buf) != -1) {
                    buf.flip();
                    while (buf.hasRemaining()) {
                        byte c = buf.get();
                        if (c == '\n' || c == '\r') {
                            this.queue.add(sb.toString(StandardCharsets.UTF_8.name()));
                            sb.reset();
                        } else {
                            sb.write(c);
                        }
                    }
                    buf.clear();
                }
            } catch (ClosedByInterruptException e) {
                synchronized (this) {
                    this.state = State.INTERRUPTED;
                }
            } catch (IOException e) {
                synchronized (this) {
                    this.exception = e;
                    this.state = State.ERROR;
                }
            }
            try {
                ch.close();
                synchronized (this) {
                    if (this.state == State.READING) {
                        this.state = State.DONE;
                    }
                }
            } catch (IOException e) {
                synchronized (this) {
                    this.exception = e;
                    this.state = State.ERROR;
                }
            }
        }
        
        public static enum State {
            INITIALIZED,
            STARTING,
            OPENING,
            READING,
            DONE,
            ERROR,
            INTERRUPTED,
        }
    }
}
