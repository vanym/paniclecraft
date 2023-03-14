package com.vanym.paniclecraft.plugins.computercraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import dan200.computercraft.api.peripheral.IComputerAccess;

public class ComputerEventListener implements BiConsumer<String, Object[]> {
    
    protected final IComputerAccess computer;
    
    public ComputerEventListener(IComputerAccess computer) {
        this.computer = computer;
    }
    
    @Override
    public void accept(String name, Object[] args) {
        List<Object> list = new ArrayList<>();
        list.add(this.computer.getAttachmentName());
        list.addAll(Arrays.asList(args));
        this.computer.queueEvent(name, list.toArray());
    }
    
    @Override
    public int hashCode() {
        return this.computer.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComputerEventListener) {
            return this.computer.equals(((ComputerEventListener)obj).computer);
        } else {
            return false;
        }
    }
}
