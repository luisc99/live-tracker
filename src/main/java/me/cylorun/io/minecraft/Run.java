package me.cylorun.io.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.cylorun.io.minecraft.world.WorldFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Run extends ArrayList<Object> {

    private final WorldFile worldFile;
    private final RecordFile recordFile;
    private JsonObject stats;
    private JsonObject adv;
    private final int size = 150; // header count / col count

    public Run(WorldFile worldFile, RecordFile recordFile) {
        this.worldFile = worldFile;
        this.recordFile = recordFile;

        Set<String> uuids = this.recordFile.getJson().get("stats").getAsJsonObject().keySet();
        this.adv = recordFile.getJson().get("advancements").getAsJsonObject();
        this.stats = recordFile.getJson().get("stats").getAsJsonObject().get(uuids.toArray()[0].toString()).getAsJsonObject().get("stats").getAsJsonObject();

    }

    public Run gatherAll(){
        this.add(this.getDate());
        this.add(this.getIronSource());
        System.out.println(this.getIronSource());
        return this;
    }

    private String getIronSource() {
        String ironSource = "None";

        if (this.adv.has("minecraft:story/smelt_iron") || this.adv.has("minecraft:story/iron_tools") ||
                (stats.has("minecraft:crafted") && (stats.getAsJsonObject("minecraft:crafted").has("minecraft:diamond_pickaxe")))) {
            ironSource = "Misc";

            // If iron not obtained before nether enter
            if (this.adv.has("minecraft:story/enter_the_nether") && this.adv.has("minecraft:story/smelt_iron")) {
                int netherIgt = this.adv.getAsJsonObject("minecraft:story/enter_the_nether").get("igt").getAsInt();
                int ironIgt = this.adv.getAsJsonObject("minecraft:story/smelt_iron").get("igt").getAsInt();
                if (netherIgt < ironIgt) {
                    ironSource = "Nether";
                }
            }
            else if (stats.has("minecraft:crafted") && stats.getAsJsonObject("minecraft:crafted").has("minecraft:furnace") &&
                    stats.has("minecraft:mined") && stats.getAsJsonObject("minecraft:mined").has("minecraft:iron_ore")) {
                ironSource = "Structureless";
            }
            // If haybale mined or iron golem killed or iron pickaxe obtained from chest
            else if ((stats.has("minecraft:mined") && stats.getAsJsonObject("minecraft:mined").has("minecraft:hay_block")) ||
                    (stats.has("minecraft:killed") && stats.getAsJsonObject("minecraft:killed").has("minecraft:iron_golem")) ||
                    (!(stats.has("minecraft:crafted") && (stats.getAsJsonObject("minecraft:crafted").has("minecraft:iron_pickaxe") ||
                            stats.getAsJsonObject("minecraft:crafted").has("minecraft:diamond_pickaxe"))) &&
                            this.adv.has("minecraft:story/iron_tools"))) {
                ironSource = "Village";
            }
            // If more than 7 tnt mined
            else if (stats.has("minecraft:mined") && stats.getAsJsonObject("minecraft:mined").has("minecraft:tnt") &&
                    stats.getAsJsonObject("minecraft:mined").get("minecraft:tnt").getAsInt() > 7) {
                ironSource = "Desert Temple";
            }
            // If stepped foot in ocean or beach under 3 minutes
            else if (this.adv.has("minecraft:adventure/adventuring_time")) {
                JsonObject adventuringTime = this.adv.getAsJsonObject("minecraft:adventure/adventuring_time");
                for (String biome : adventuringTime.getAsJsonObject("criteria").keySet()) {
                    if ((biome.contains("beach") || biome.contains("ocean")) &&
                            adventuringTime.getAsJsonObject("criteria").getAsJsonObject(biome).get("igt").getAsInt() < 180000) {
                        // If potato, wheat, or carrot obtained
                        if (this.adv.has("minecraft:recipes/food/baked_potato") ||
                                this.adv.has("minecraft:recipes/food/bread") ||
                                this.adv.has("minecraft:recipes/transportation/carrot_on_a_stick")) {
                            ironSource = "Full Shipwreck";
                        }
                        // If sus stew or rotten flesh eaten
                        else if (this.stats.has("minecraft:used") &&
                                (this.stats.getAsJsonObject("minecraft:used").has("minecraft:suspicious_stew") ||
                                        this.stats.getAsJsonObject("minecraft:used").has("minecraft:rotten_flesh"))) {
                            ironSource = "Full Shipwreck";
                        }
                        // If tnt exploded
                        else if (this.stats.has("minecraft:used") && this.stats.getAsJsonObject("minecraft:used").has("minecraft:tnt")) {
                            ironSource = "Buried Treasure w/ tnt";
                        }
                        // If cooked salmon or cod eaten
                        else if (this.stats.has("minecraft:used") &&
                                (this.stats.getAsJsonObject("minecraft:used").has("minecraft:cooked_salmon") ||
                                        this.stats.getAsJsonObject("minecraft:used").has("minecraft:cooked_cod"))) {
                            ironSource = "Buried Treasure";
                        }
                        // If sand/gravel mined before iron acquired
                        else if (this.adv.has("minecraft:recipes/building_blocks/magenta_concrete_powder") &&
                                this.adv.getAsJsonObject("minecraft:recipes/building_blocks/magenta_concrete_powder").getAsJsonObject("criteria")
                                        .getAsJsonObject("has_the_recipe").get("igt").getAsInt() < this.adv.getAsJsonObject("minecraft:story/smelt_iron").get("igt").getAsInt()) {
                            ironSource = "Buried Treasure";
                        }
                        // If wood mined before iron obtained
                        else if (((this.adv.has("minecraft:story/smelt_iron") && this.adv.has("minecraft:recipes/misc/charcoal")) &&
                                this.adv.getAsJsonObject("minecraft:story/smelt_iron").get("igt").getAsInt() > this.adv.getAsJsonObject("minecraft:recipes/misc/charcoal").get("igt").getAsInt()) ||
                                (this.adv.has("minecraft:recipes/misc/charcoal") && !(this.adv.has("minecraft:story/iron_tools"))) &&
                                        ((this.stats.has("minecraft:custom") && (this.stats.getAsJsonObject("minecraft:custom").get("minecraft:open_chest").getAsInt() == 1)) ||
                                                this.adv.has("minecraft:nether/find_bastion"))) {
                            ironSource = "Half Shipwreck";
                        }
                        else if ((this.stats.has("minecraft:crafted") && this.stats.getAsJsonObject("minecraft:crafted").has("minecraft:diamond_pickaxe")) ||
                                (this.stats.has("minecraft:crafted") && this.stats.getAsJsonObject("minecraft:crafted").has("minecraft:diamond_sword"))) {
                            ironSource = "Buried Treasure";
                        }
                        else if (this.stats.has("minecraft:mined") &&
                                ((this.stats.getAsJsonObject("minecraft:mined").has("minecraft:oak_log") &&
                                        this.stats.getAsJsonObject("minecraft:mined").get("minecraft:oak_log").getAsInt() <= 4) ||
                                        (this.stats.getAsJsonObject("minecraft:mined").has("minecraft:dark_oak_log") &&
                                                this.stats.getAsJsonObject("minecraft:mined").get("minecraft:dark_oak_log").getAsInt() <= 4) ||
                                        (this.stats.getAsJsonObject("minecraft:mined").has("minecraft:birch_log") &&
                                                this.stats.getAsJsonObject("minecraft:mined").get("minecraft:birch_log").getAsInt() <= 4) ||
                                        (this.stats.getAsJsonObject("minecraft:mined").has("minecraft:jungle_log") &&
                                                this.stats.getAsJsonObject("minecraft:mined").get("minecraft:jungle_log").getAsInt() <= 4) ||
                                        (this.stats.getAsJsonObject("minecraft:mined").has("minecraft:spruce_log") &&
                                                this.stats.getAsJsonObject("minecraft:mined").get("minecraft:spruce_log").getAsInt() <= 4) ||
                                        (this.stats.getAsJsonObject("minecraft:mined").has("minecraft:acacia_log") &&
                                                this.stats.getAsJsonObject("minecraft:mined").get("minecraft:acacia_log").getAsInt() <= 4))) {
                            ironSource = "Half Shipwreck";
                        }
                        else {
                            ironSource = "Buried Treasure";
                        }
                    }
                }
            }
        }
        return ironSource;
    }
    private String getDate(){
        return new Date(this.recordFile.getJson().get("date").getAsLong()).toString();
    }


}
