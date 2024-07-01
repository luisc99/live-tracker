package com.cylorun.instance;

import com.cylorun.utils.Assert;
import com.cylorun.enums.SpeedrunEventType;

public class SpeedrunEvent {
    public SpeedrunEventType type;
    public Long igt;
    public Long rta;

    public SpeedrunEvent(String logString) { // common.multiplayer rta igt
        String[] split = logString.split(" ");
        Assert.isTrue(split.length == 3, "invalid speedrunevent string");
        this.type = stringToSREvent(split[0].split("\\.")[1]);
        this.rta = Long.parseLong(split[1]);
        this.igt = Long.parseLong(split[2]);
    }

    public static SpeedrunEventType stringToSREvent(String s) {
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
}
