package me.cylorun.event.callbacks;

import me.cylorun.io.minecraft.WorldFile;

public interface WorldCreationListener {
    void onNewWorld(WorldFile world);
}
