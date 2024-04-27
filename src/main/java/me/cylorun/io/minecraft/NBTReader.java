package me.cylorun.io.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.utils.ExceptionUtil;
import me.cylorun.utils.Vec2i;
import net.minecraft.util.math.Vec3i;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class NBTReader {
    private Path path;
    public static String[] SEED_PATH = {"value", "Data", "value", "WorldGenSettings", "value", "seed", "value"};
    public static String[] PLAYER_PATH = {"value", "Data", "value", "Player", "value"};
    public static String[] INVENTORY_PATH = {"value", "Data", "value", "Player", "value", "Inventory", "value", "list"};
    public static String[] PLAYER_DIMENSION = {"value", "Data", "value", "Player", "value", "Dimension", "value"};
    public static String[] PLAYER_POS = {"value", "Data", "value", "Player", "value", "Pos", "value", "list"};

    public NBTReader(Path path) {
        this.path = path;
    }

    public NBTReader setPath(Path path) {
        this.path = path;
        return this;
    }

    public JsonObject read() {
        NamedTag tag;
        try {
            tag = NBTUtil.read(this.path.toFile());
        } catch (IOException e) {
            ExceptionUtil.showError(e);
            throw new RuntimeException(e);
        }
        return JsonParser.parseString(tag.getTag().toString()).getAsJsonObject();
    }

    public Vec2i getPlayerLocation() {
        Integer[] loc = Arrays.stream(this.get(NBTReader.PLAYER_POS).replaceAll("[\\[\\]]", "").split(","))
                .map((l) -> (int) Double.parseDouble(l))
                .toArray(Integer[]::new);
        return new Vec2i(loc[0], loc[2]);
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
