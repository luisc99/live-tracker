package me.cylorun;

import kaptainwutax.mcutils.util.math.Vec3i;
import me.cylorun.io.WorldCreationEventHandler;
import me.cylorun.io.callbacks.WorldCreationListener;
import me.cylorun.minecraft.WorldFile;

public class Main {
    public static void main(String[] args) {
        WorldCreationEventHandler worldHandler = new WorldCreationEventHandler();
        worldHandler.addListener(world -> System.out.println(world.path));
    }
}