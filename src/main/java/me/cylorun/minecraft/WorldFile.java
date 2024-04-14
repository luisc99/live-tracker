package me.cylorun.minecraft;

import java.io.File;
import java.nio.file.Path;

public class WorldFile extends File {
    public final String path;
    public WorldFile(String path){
        super(path);
        this.path = path;
    }
}
