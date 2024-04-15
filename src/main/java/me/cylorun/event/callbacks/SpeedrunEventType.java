package me.cylorun.event.callbacks;

public enum SpeedrunEventType {
    // common
    LEAVE_WORLD("common.leave_world"),
    REJOIN_WORLD("common.rejoin_world"),
    MULTIPLAYER("common.multiplayer"),
    ENABLE_CHEATS("common.enable_cheats"),

    //rsg
    ENTER_END("rsg.enter_end"),
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
