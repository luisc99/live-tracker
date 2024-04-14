package me.cylorun.event;

public class SpeedrunEvent {
    public String name;
    public Integer igt;
    public SpeedrunEvent(String name, Integer igt){
        this.name = name;
        this.igt = igt;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof SpeedrunEvent other)){
            return false;
        }

        return other.name.equals(this.name) && other.igt.equals(this.igt);
    }
}
