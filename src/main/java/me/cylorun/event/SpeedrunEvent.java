package me.cylorun.event;

public class SpeedrunEvent {
    public SpeedrunEventType type;
    public Integer igt;
    public Integer rta;

    public SpeedrunEvent(String logString) { // common.multiplayer rta igt
        String[] split = logString.split(" ");
        this.type = stringToSREvent(split[0].split("\\.")[1]);
        this.rta = Integer.parseInt(split[1]);
        this.igt = Integer.parseInt(split[2]);
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
