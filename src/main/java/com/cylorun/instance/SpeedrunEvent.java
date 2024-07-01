package com.cylorun.instance;

import com.cylorun.utils.Assert;

public class SpeedrunEvent {
    public SpeedrunEventType type;
    public Long igt;
    public Long rta;

    public SpeedrunEvent(String logString) { // common.multiplayer rta igt
        String[] split = logString.split(" ");
        Assert.isTrue(split.length == 3, "invalid speedrunevent string");
        this.type = stringToType(split[0].split("\\.")[1]);
        this.rta = Long.parseLong(split[1]);
        this.igt = Long.parseLong(split[2]);
    }

    public static SpeedrunEventType stringToType(String s) {
        return SpeedrunEventType.valueOf(s.toUpperCase());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SpeedrunEvent other)) {
            return false;
        }
        return other.type.equals(this.type) && other.igt.equals(this.igt);
    }

    @Override
    public String toString() {
        return String.format("SpeedrunEvent{name: %s, igt: %s}", this.type, this.igt);
    }

    public  enum SpeedrunEventType {
        // common
        LEAVE_WORLD("common.leave_world"),
        REJOIN_WORLD("common.rejoin_world"),
        MULTIPLAYER("common.multiplayer"),
        ENABLE_CHEATS("common.enable_cheats"),
        VIEW_SEED("common.view_seed"),
        OLD_WORLD("common.old_world"),

        //rsg
        OBTAIN_IRON_INGOT("rsg.obtain_iron_ingot"),
        OBTAIN_IRON_PICKAXE("rsg.obtain_iron_pickaxe"),
        OBTAIN_LAVA_BUCKET("rsg.obtain_lava_bucket"),
        ENTER_NETHER("rsg.enter_nether"),
        DISTRACT_PIGLIN("rsg.distract_piglin"),
        ENTER_BASTION("rsg.enter_bastion"),
        LOOT_BASTION("rsg.loot_bastion"),
        OBTAIN_OBSIDIAN("rsg.obtain_obsidian"),
        OBTAIN_CRYING_OBSIDIAN("rsg.obtain_crying_obsidian"),
        ENTER_FORTRESS("rsg.enter_fortress"),
        OBTAIN_BLAZE_ROD("rsg.obtain_blaze_rod"),
        FIRST_PORTAL("rsg.first_portal"),
        SECOND_PORTAL("rsg.second_portal"),
        ENTER_STRONGHOLD("rsg.enter_stronghold"),
        ENTER_END("rsg.enter_end"),
        KILL_DRAGON("rsg.kill_dragon"),
        CREDITS("rsg.credits");

        public final String label;

        SpeedrunEventType(String label) {
            this.label = label;
        }
        @Override
        public String toString() {
            return this.label;
        }
    }
}
