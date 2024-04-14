package me.cylorun.minecraft.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;

import java.io.IOException;
import java.nio.file.Path;

public class NBTReader {
    private Path path;
    public static String[] SEED_PATH = {"value", "Data", "value", "WorldGenSettings", "value", "seed", "value"};
    public static String[] PLAYER_PATH = {"value","Data","value","Player"};
    public static String[] PLAYER_DIMENSION = {"value","Data","value","Player","value","Dimension","value"};
    public static String[] PLAYER_POS = {"value","Data","value","Player","value","Pos", "value", "list"};

    public NBTReader(Path path) {
        this.path = path;
    }
    public NBTReader setPath(Path path){
        this.path = path;
        return this;
    }

    public JsonObject read() {
        NamedTag tag;
        try {
            tag = NBTUtil.read(this.path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return JsonParser.parseString(tag.getTag().toString()).getAsJsonObject();
    }

    public String get(String[] tags) {
        return this.getNestedValue(tags, this.read());
    }
    public String get(String[] tags, JsonObject start) {
        return this.getNestedValue(tags, start);
    }
    public JsonObject getAsJson(String[] tags, JsonObject start){
        return JsonParser.parseString(this.getNestedValue(tags, start)).getAsJsonObject();
    }
    public JsonObject getAsJson(String[] tags){
        return JsonParser.parseString(this.getNestedValue(tags, this.read())).getAsJsonObject();
    }
    private String getNestedValue(String[] tags, JsonObject curr){
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
