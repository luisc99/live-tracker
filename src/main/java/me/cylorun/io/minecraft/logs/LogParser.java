package me.cylorun.io.minecraft.logs;

import com.google.gson.JsonObject;
import me.cylorun.enums.LogEventType;
import me.cylorun.io.TrackerOptions;
import me.cylorun.io.minecraft.LogEvent;
import me.cylorun.io.minecraft.world.WorldFile;
import me.cylorun.utils.I18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    private int lastRespawnSet = -1;
    private boolean respawnSet = false;

    public LogParser() {

    }

    public List<LogEvent> getAllEvents(List<String> lines, WorldFile file) {
        List<LogEvent> res = new ArrayList<>();

        for (String l : lines) {
            /*
            [15:51:29] [Render thread/INFO]: [CHAT] Respawn point set -- pass
            [15:51:48] [Render thread/INFO]: [CHAT] [Debug]: Render Distance: 9 -- ignore
            [15:51:05] [Render thread/INFO]: [CHAT] <cylorun> hi -- ignore
            */

            /*
            [18:02:11] [Server thread/INFO]: cylorun fell from a high place -- server death pattern
            [18:02:11] [Render thread/INFO]: [CHAT] cylorun fell from a high place --  chat log regex
            */

            if (this.respawnSet) {
                if (getTime(l) - this.lastRespawnSet > TrackerOptions.getInstance().respawn_to_hr) {
                    this.respawnSet = false;
                }
            }

            if (isChatLogMessage(l)) { // possible duplication bug
                if (l.contains(I18n.get("chat.respawn_set"))) {
                    this.lastRespawnSet = getTime(l);
                    this.respawnSet = true;
                    res.add(new LogEvent(LogEventType.RESPAWN_SET, l));
                }

                if (containsDeath(l)) {
                    if (this.respawnSet) {
                        res.add(new LogEvent(LogEventType.HUNGER_RESET, l));
                        this.respawnSet = false;
                    } else {
                        res.add(new LogEvent(LogEventType.DEATH, l));
                    }
                }
            }


        }

        return res;
    }

    private static String getPrevLine(String line, List<String> lines) {
        if (lines.indexOf(line) == 0) return line;
        return lines.get(lines.indexOf(line) - 1);
    }

    public static int timeBetween(String lineA, String lineB) { // in seconds
        int a = getTime(lineA);
        int b = getTime(lineB);
        if (a == -1 || b == -1) {
            return 0;
        }
        return a - b;
    }

    public static int getTime(String line) {
        String pattern = "\\[(\\d{2}:\\d{2}:\\d{2})\\]";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(line);
        if (!matcher.find()) {
            return -1;
        }
        String time = matcher.group(1);

        String[] splitTime = time.split(":");
        if (splitTime.length != 3) {
            return -1;
        }

        return (Integer.parseInt(splitTime[0]) * 3600) + (Integer.parseInt(splitTime[1]) * 60) + Integer.parseInt(splitTime[2]);
    }

    public static boolean containsDeath(String line) {
//        if (isServerThread(line)) {
            JsonObject deaths = I18n.getAllDeaths();
            for (String s : deaths.keySet()) {
                String v = deaths.get(s).getAsString();
                line = line.toLowerCase();
                if (line.contains(v.toLowerCase())) {
                    return true;
                }
            }
//        }

        return false;
    }

    public static boolean isServerThread(String line) {
        Pattern pattern = Pattern.compile("\\\\[(\\\\d{2}:\\\\d{2}:\\\\d{2})\\\\] \\\\[Server thread/INFO\\\\]: (.*)");
        return pattern.matcher(line).find();
    }

    public static boolean isRenderThread(String line) {
        Pattern pattern = Pattern.compile("^\\[\\d{2}:\\d{2}:\\d{2}\\] \\[Render thread/INFO\\]:.*");
        return pattern.matcher(line).find();
    }

    public static boolean isChatLogMessage(String line) { // something not typed by the player or debug, advancements deaths and commands
        Pattern pattern = Pattern.compile("^\\[\\d{2}:\\d{2}:\\d{2}\\]\\s*\\[Render\\s+thread\\/INFO\\]:\\s*\\[CHAT\\]\\s*(?!.*(?:\\[Debug\\]:|<\\w+>)).*$");
        return pattern.matcher(line).find();
    }

}
