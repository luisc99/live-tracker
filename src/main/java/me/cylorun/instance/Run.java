package me.cylorun.instance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.cylorun.Tracker;
import me.cylorun.io.TrackerOptions;
import me.cylorun.instance.world.WorldFile;
import me.cylorun.utils.Assert;
import me.cylorun.utils.ResourceUtil;
import me.cylorun.utils.Vec2i;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Run extends ArrayList<Object> {

    private final WorldFile worldFile;
    private final RecordFile recordFile;
    public JsonObject stats;
    public JsonObject adv;
    private List<SpeedrunEvent> eventLog;
    private long seed;

    public Run(WorldFile worldFile, RecordFile recordFile) {
        this.worldFile = worldFile;
        this.recordFile = recordFile;
        this.eventLog = worldFile.eventHandler.events;
        this.adv = recordFile.getJson().get("advancements").getAsJsonObject();
        this.stats = this.getStats();
        try {
            this.seed = Long.parseLong(new NBTReader(this.worldFile.getLevelDatPath()).get(NBTReader.SEED_PATH));
        } catch (NumberFormatException e) {
            this.seed = 0;
        }
    }

    public Run gatherAll() {
        Assert.isNotNull(this.stats);

        String[] majorSplits = {"rsg.obtain_iron_pickaxe",
                "rsg.enter_nether",
                "rsg.enter_bastion",
                "rsg.enter_fortress",
                "rsg.first_portal",
                "rsg.second_portal",
                "rsg.enter_stronghold",
                "rsg.enter_end"};

        this.add(this.getDate());
        this.add(this.getIronSource());
        this.add(this.getEnterType());
        this.add(this.getGoldSource());
        this.add(this.getSpawnBiome());
        this.add(msToString(this.recordFile.getJson().get("final_rta").getAsLong()));
        this.add(this.getWoodTime());

        for (String split : majorSplits) {
            this.add(this.getSplitTime(split));
        }
        this.add(msToString(this.recordFile.getJson().get("final_igt").getAsLong()));
        this.add(this.worldFile.strongholdTracker.getFinalData());
        this.add(getStrongholdRing(this.worldFile.strongholdTracker.endPoint));
        this.add(String.valueOf(this.getExplosivesUsed()));
        this.add("Gold");
        this.addAll(this.getMiscStats());
        this.add(worldFile.getName());
        this.addAll(this.getFinalBarters());
        this.addAll(this.getMobKills());
        this.addAll(this.getFoods());
        this.addAll(this.getTravelled());
        this.add(this.seed == 0 ? "Failed to get seed" : this.seed);
        return this;
    }

    private JsonObject getStats() {
        Set<String> uuids = this.recordFile.getJson().get("stats").getAsJsonObject().keySet();
        if (!uuids.isEmpty()) {
            return this.recordFile.getJson().get("stats").getAsJsonObject().get(uuids.toArray()[0].toString()).getAsJsonObject().get("stats").getAsJsonObject();
        }
        return null;
    }

    private String getDate() {
        Date date = new Date(this.recordFile.getJson().get("date").getAsLong());
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);
    }

    private String getWoodTime() {
        try {
            long ms = this.adv.get("minecraft:recipes/misc/charcoal").getAsJsonObject().get("criteria").getAsJsonObject().get("has_log").getAsJsonObject().get("igt").getAsLong();
            return msToString(ms);
        } catch (Exception ignored) {
            return "";
        }
    }

    private int getExplosivesUsed() {
        if (!this.stats.has("minecraft:used")) return 0;
        int explUsed = 0;
        JsonObject used = this.stats.get("minecraft:used").getAsJsonObject();
        for (Map.Entry<String, JsonElement> e : used.asMap().entrySet()) {
            if (e.getKey().contains("_bed") || e.getKey().equals("minecraft:respawn_anchor"))
                explUsed += e.getValue().getAsInt();
        }
        return explUsed - this.worldFile.hungerResetHandler.respawnPointsSet;
    }

    public static String getStrongholdRing(Vec2i strongholdLoc) {
        if (strongholdLoc == null) {
            return "";
        }

        int dist = strongholdLoc.distanceTo(Vec2i.ZERO);
        if (dist <= 2816) return "1";
        if (dist <= 5888) return "2";
        if (dist <= 8960) return "3";
        if (dist <= 12032) return "4";
        if (dist <= 15104) return "5";
        if (dist <= 18146) return "6";
        if (dist <= 21248) return "7";
        return "8";
    }

    private String getSplitTime(String splitName) {
        long time = Long.MAX_VALUE;
        for (SpeedrunEvent e : this.eventLog) {
            if (e.type.label.equals(splitName)) {
                time = e.igt;
            }
        }

        if (time == Long.MAX_VALUE) {
            return "";
        }

        return Run.msToString(time);
    }

    private String getSpawnBiome() {
        String spawnBiome = "None";
        if (adv.has("minecraft:adventure/adventuring_time")) {
            JsonObject adventuringTime = adv.getAsJsonObject("minecraft:adventure/adventuring_time");
            if (adventuringTime.has("criteria")) {
                JsonObject criteria = adventuringTime.getAsJsonObject("criteria");
                for (String biome : criteria.keySet()) {
                    JsonObject biomeData = criteria.getAsJsonObject(biome);
                    if (biomeData.has("igt") && biomeData.get("igt").getAsInt() == 0) {
                        spawnBiome = biome.split(":")[1];
                        break;
                    }
                }
            }
        }
        return spawnBiome;
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

    public List<String> getFinalBarters() {
        List<String> res = new ArrayList<>();
        URL url = Tracker.class.getClassLoader().getResource("tracked.json");
        Assert.isNotNull(url, "Resource not found: events/tracked.json");

        JsonObject o = ResourceUtil.loadJsonResource(url);
        JsonArray barters = o.get("TRACKED_BARTERS").getAsJsonArray();
        JsonObject pickedUp;
        if (this.stats.has("minecraft:picked_up")) {
            pickedUp = this.stats.get("minecraft:picked_up").getAsJsonObject();
        } else {
            for (JsonElement e : barters) {
                res.add("0");
            }
            return res;
        }

        for (JsonElement barterItem : barters) {
            if (pickedUp.has(barterItem.getAsString())) {
                int diff = 0;
                if (this.worldFile.hungerResetHandler.itemDiffs.containsKey(barterItem.getAsString())) {
                    diff = this.worldFile.hungerResetHandler.itemDiffs.get(barterItem.getAsString());
                }
                res.add(String.valueOf(pickedUp.get(barterItem.getAsString()).getAsInt() - diff));
            } else {
                res.add("0");
            }
        }

        return res;
    }

    public List<String> getMiscStats() {
        List<String> res = new ArrayList<>();
        URL url = Tracker.class.getClassLoader().getResource("tracked.json");

        JsonArray checks = ResourceUtil.loadJsonResource(url).getAsJsonArray("MISC_CHECKS");

        for (JsonElement e : checks) {
            JsonArray check = e.getAsJsonArray();
            try {
                String d = this.stats.getAsJsonObject(check.get(0).getAsString()).get(check.get(1).getAsString()).getAsString();
//                System.out.printf("Checks: %s, Val: %s\n", check, d);
                res.add(d);
            } catch (Exception ex) {
                res.add("0");
            }

        }
        return res;
    }

    public List<String> getMobKills() {
        List<String> res = new ArrayList<>();
        URL url = Tracker.class.getClassLoader().getResource("tracked.json");
        JsonArray mobs = ResourceUtil.loadJsonResource(url).getAsJsonArray("TRACKED_MOBS");
        for (JsonElement mob : mobs) {
            try{
                String val = this.stats.getAsJsonObject("minecraft:killed").get(mob.getAsString()).getAsString();
                res.add(val);
            } catch (Exception e) {
                res.add("0");
            }
        }
        return res;
    }

    public List<String> getFoods() {
        List<String> res = new ArrayList<>();
        URL url = Tracker.class.getClassLoader().getResource("tracked.json");
        JsonArray foods = ResourceUtil.loadJsonResource(url).getAsJsonArray("TRACKED_FOODS");
        for (JsonElement food : foods) {
            try{
                String val = this.stats.getAsJsonObject("minecraft:used").get(food.getAsString()).getAsString();
                res.add(val);
            } catch (Exception e) {
                res.add("0");
            }
        }
        return res;
    }

    public List<String> getTravelled() {
        List<String> res = new ArrayList<>();
        URL url = Tracker.class.getClassLoader().getResource("tracked.json");
        JsonArray methods = ResourceUtil.loadJsonResource(url).getAsJsonArray("TRAVEL_METHODS");
        for (JsonElement method : methods) {
            try{
                String val = this.stats.getAsJsonObject("minecraft:custom").get(method.getAsString()).getAsString();
                res.add(val);
            } catch (Exception e) {
                res.add("0");
            }
        }
        return res;
    }

    public boolean shouldPush() {
        TrackerOptions options = TrackerOptions.getInstance();
        Assert.isNotNull(this.stats, "Stats is null");
        String runType = this.recordFile.getJson().get("run_type").getAsString();
        if ((options.detect_ssg && runType.equals("set_seed")) || recordFile.getJson().get("category").getAsString().equals("pratice_world") ) { // yes its pratice not practice
            return false;
        }

        return this.adv.has("minecraft:story/smelt_iron") || this.adv.has("minecraft:story/enter_the_nether");
    }

    public static String msToString(long time) {

        long totalSeconds = time / 1000;
        long hours = totalSeconds / 3600;
        long remainingSeconds = totalSeconds % 3600;
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
