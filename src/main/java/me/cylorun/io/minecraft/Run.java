package me.cylorun.io.minecraft;

import me.cylorun.io.minecraft.world.WorldFile;

import java.util.ArrayList;

public class Run extends ArrayList<String> {

    private final WorldFile worldFile;
    private final RecordFile recordFile;
    public Run(WorldFile worldFile, RecordFile recordFile) {
        this.worldFile = worldFile;
        this.recordFile = recordFile;
    }

    private void gatherAll() {

    }
}
