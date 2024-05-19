package me.cylorun.map;

import me.cylorun.utils.Vec2i;

import java.util.HashMap;
import java.util.Map;

public class RunCoords {
    private Map<String, Vec2i> structureLocs;

    public RunCoords() {
        this.structureLocs = new HashMap<>();
    }
}
