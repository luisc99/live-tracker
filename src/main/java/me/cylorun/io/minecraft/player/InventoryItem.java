package me.cylorun.io.minecraft.player;

public class InventoryItem {

    public String name;
    public int count;
    public InventoryItem(String name, int count) {
        this.name = name;
        this.count = count;
    }

    @Override
    public String toString() {
        return String.format("{\"name\": %s, \"count\": %s}", this.name, this.count);
    }
}
