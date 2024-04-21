package me.cylorun.io.minecraft;

import com.google.gson.JsonObject;
import me.cylorun.io.TrackerOptions;
import me.cylorun.io.minecraft.world.WorldFile;
import me.cylorun.utils.Assert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

        this.adv = recordFile.getJson().get("advancements").getAsJsonObject();
        this.stats = this.getStats();

    }

    public synchronized Run gatherAll() {
        if (this.stats == null) {
            this.stats = this.getStats();
        }

        this.add(this.getDate());
        this.add(this.getIronSource());
        this.add(this.getEnterType());
        this.add(this.getGoldSource());
        return this;
    }

    private JsonObject getStats(){
        Set<String> uuids = this.recordFile.getJson().get("stats").getAsJsonObject().keySet();
        if (!uuids.isEmpty()){
            return this.recordFile.getJson().get("stats").getAsJsonObject().get(uuids.toArray()[0].toString()).getAsJsonObject().get("stats").getAsJsonObject();
        }
        return null;
    }

    private String getDate() {
        Date date = new Date(this.recordFile.getJson().get("date").getAsLong());
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);
    }

    private String getEnterType() {
        String enterType = "None";
        if (this.adv.has("minecraft:story/enter_the_nether")) {
            enterType = "Obsidian";
            if (this.stats.has("minecraft:mined") && this.stats.get("minecraft:mined").getAsJsonObject().has("minecraft:magma_block")) {
                if (this.adv.has("minecraft:story/lava_bucket")) {
                    enterType = "Magma Ravine";
                } else {
                    enterType = "Bucketless";
                }
            } else if (this.adv.has("minecraft:story/lava_bucket")) {
                enterType = "Lava Pool";

            }
        }
        return enterType;
    }

    private String getGoldSource() {
        String goldSource = "None";

        // Check if "gold_ingot" is either dropped or picked up
        if ((stats.has("minecraft:dropped") && stats.get("minecraft:dropped").getAsJsonObject().has("minecraft:gold_ingot")) ||
                (stats.has("minecraft:picked_up") && (stats.get("minecraft:picked_up").getAsJsonObject().has("minecraft:gold_ingot") ||
                        stats.get("minecraft:picked_up").getAsJsonObject().has("minecraft:gold_block")))) {
            goldSource = "Classic";

            // Check if "dark_prismarine" is mined
            if (stats.has("minecraft:mined") && stats.get("minecraft:mined").getAsJsonObject().has("minecraft:dark_prismarine")) {
                goldSource = "Monument";
            } else if (adv.has("minecraft:nether/find_bastion")) {
                goldSource = "Bastion";
            }
        }
        return goldSource;
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
            } else if (stats.has("minecraft:crafted") && stats.getAsJsonObject("minecraft:crafted").has("minecraft:furnace") &&
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
                        } else if ((this.stats.has("minecraft:crafted") && this.stats.getAsJsonObject("minecraft:crafted").has("minecraft:diamond_pickaxe")) ||
                                (this.stats.has("minecraft:crafted") && this.stats.getAsJsonObject("minecraft:crafted").has("minecraft:diamond_sword"))) {
                            ironSource = "Buried Treasure";
                        } else if (this.stats.has("minecraft:mined") &&
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
                        } else {
                            ironSource = "Buried Treasure";
                        }
                    }
                }
            }
        }
        return ironSource;
    }

    public  boolean shouldPush() {
        TrackerOptions options = TrackerOptions.getInstance();
        Assert.isNotNull(this.stats);
        String runType =  this.recordFile.getJson().get("run_type").getAsString();
        if (options.detect_ssg && runType.equals("set_seed")) {
            return false;
        }

        return this.adv.has("minecraft:story/smelt_iron") || this.adv.has("minecraft:story/enter_the_nether");
    }



}
