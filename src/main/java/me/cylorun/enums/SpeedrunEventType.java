package me.cylorun.enums;

public enum SpeedrunEventType {
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

    private final String label;

    private SpeedrunEventType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
