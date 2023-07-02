package com.vanym.paniclecraft.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CommandBase implements ICommand {
    
    protected String[] path;
    
    protected String getPath() {
        if (this.path != null) {
            return "/" + String.join(" ", this.path);
        } else {
            return this.getName();
        }
    }
    
    protected void setParentPath(String[] path) {
        if (path != null) {
            List<String> list = new ArrayList<>(Arrays.asList(path));
            list.add(this.getName());
            this.path = list.toArray(new String[list.size()]);
        }
    }
    
    protected String getTranslationPrefix() {
        List<String> path = new ArrayList<>();
        path.add("commands");
        if (this.path != null) {
            path.addAll(Arrays.asList(this.path));
        } else {
            path.add(this.getName());
        }
        return String.join(".", path);
    }
}
