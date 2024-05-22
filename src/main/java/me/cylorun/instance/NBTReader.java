package me.cylorun.instance;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kaptainwutax.mcutils.state.Dimension;
import me.cylorun.Tracker;
import me.cylorun.instance.world.WorldFile;
import me.cylorun.utils.Assert;
import me.cylorun.utils.ExceptionUtil;
import me.cylorun.utils.Vec2i;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class NBTReader {
    private Path path;
    public static String[] SEED_PATH = {"value", "Data", "value", "WorldGenSettings", "value", "seed", "value"};
    public static String[] PLAYER_PATH = {"value", "Data", "value", "Player", "value"};
    public static String[] INVENTORY_PATH = {"value", "Data", "value", "Player", "value", "Inventory", "value", "list"};
    public static String[] PLAYER_DIMENSION = {"value", "Data", "value", "Player", "value", "Dimension", "value"};
    public static String[] PLAYER_POS = {"value", "Data", "value", "Player", "value", "Pos", "value", "list"};

    private NBTReader(Path path) {
        this.path = path;
    }

    public NBTReader setPath(Path path) {
        this.path = path;
        return this;
    }

    public static NBTReader from(WorldFile file) {
        Assert.isNotNull(file,"Worldfile object null");
        return new NBTReader(file.getLevelDatPath());
    }

    public static NBTReader from(Path path) {
        Assert.isNotNull(path,"Worldfile object null");
        return new NBTReader(path);
    }

    public JsonObject read() {
        NamedTag tag;
        try {
            tag = NBTUtil.read(this.path.toFile());
        } catch (IOException e) {
            Tracker.log(Level.ERROR,"Failed to read a nbt file");
            return null;
        }
        return JsonParser.parseString(tag.getTag().toString()).getAsJsonObject();
    }

    public Vec2i getPlayerLocation() {
        String stringData = this.get(NBTReader.PLAYER_POS);
        if (stringData == null) {
            return null;
        }

        String[] s = stringData.replaceAll("[\\[\\]]", "").split(",");
        Integer[] loc = Arrays.stream(s)
                .map((l) -> (int) Double.parseDouble(l))
                .toArray(Integer[]::new);
        return new Vec2i(loc[0], loc[2]);
    }

    public Dimension getPlayerDimension() {
        String data = this.get(NBTReader.PLAYER_DIMENSION).replace("\"","");
        String[] split = data.split(":");
        Assert.isTrue(split.length == 2);

        return Dimension.fromString(split[1]);
    }


    public String get(String[] tags) {
        return this.getNestedValue(tags, this.read());
    }

    public String get(String[] tags, JsonObject start) {
        return this.getNestedValue(tags, start);
    }

    public JsonObject getAsJson(String[] tags, JsonObject start) {
        return JsonParser.parseString(this.getNestedValue(tags, start)).getAsJsonObject();
    }

    public JsonObject getAsJson(String[] tags) {
        return JsonParser.parseString(this.getNestedValue(tags, this.read())).getAsJsonObject();
    }

    private String getNestedValue(String[] tags, JsonObject curr) {
        if (curr == null) {
            return null;
        }

        for (int i = 0; i < tags.length; i++) {
            String tag = tags[i];
            if (i == tags.length - 1) {
                return curr.get(tag).toString();
            }
            curr = curr.get(tag).getAsJsonObject();

        }
        return curr.toString();
    }

}
