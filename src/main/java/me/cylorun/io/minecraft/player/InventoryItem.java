package me.cylorun.io.minecraft.player;

public class InventoryItem {

    private String name;
    private int count;
    public InventoryItem(String name, int count) {
        this.name = name;
        this.count = count;
    }

    @Override
    public String toString() {
        return String.format("{name: %s, count: %s}", this.name, this.count);
    }
}
