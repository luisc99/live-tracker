package me.cylorun.io.minecraft.logs;

import me.cylorun.enums.LogEventType;
import me.cylorun.io.minecraft.world.WorldFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    public static int maxSpawnSetToDeathTime = 30; // max time from setting spawn point to dying

    public static List<LogEventType> getAllEvents(List<String> lines, WorldFile file) {
        List<LogEventType> res = new ArrayList<>();
        String username = null;
        try {
            username = file.getUsername();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // any message not sent by a user or Debug



        boolean setRespawn = false;

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

            if (isChatLogMessage(l)) {
                if(isRenderThread(l)) {
                    setRespawn = true;
                    res.add(LogEventType.RESPAWN_SET);
                }

                if (l.contains(username) && isServerThread(getPrevLine(l, lines))) {
                    if(setRespawn){
                        res.add(LogEventType.HUNGER_RESET);
                        setRespawn = false;
                    } else {
                        res.add(LogEventType.DEATH);
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

    public static int getTime(String line) { // returns seconds since 00:00
        String pattern = "\\[(\\d{2}:\\d{2}:\\d{2})\\]";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(line);
        if (!matcher.find()) {
            return -1;
        }
        String time = matcher.group();

        if (time.split(":").length != 3) {
            return -1;
        }

        String[] splitTime = time.split(":");
        return (Integer.parseInt(splitTime[0]) * 120) + (Integer.parseInt(splitTime[1]) * 60) + (Integer.parseInt(splitTime[2]));
    }

    public static boolean containsDeath(String line, String username) {
        String regex = String.format("^\\[\\d{2}:\\d{2}:\\d{2}\\] \\[Server thread\\/INFO\\]: %s (\\S.*)$", username);
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(line).find();
    }

    public static boolean isServerThread(String line) {
        Pattern pattern = Pattern.compile("^\\[\\d{2}:\\d{2}:\\d{2}\\] \\[Server thread/INFO\\]:\n");
        return pattern.matcher(line).find();
    }

    public static boolean isRenderThread(String line) {
        Pattern pattern = Pattern.compile("^\\[\\d{2}:\\d{2}:\\d{2}\\] \\[Render thread/INFO\\]:\n");
        return pattern.matcher(line).find();
    }

    public static boolean isChatLogMessage(String line) { // something not typed by the player or debug, advancements deaths and commands
        Pattern pattern = Pattern.compile("^\\[\\d{2}:\\d{2}:\\d{2}\\]\\s*\\[Render\\s+thread\\/INFO\\]:\\s*\\[CHAT\\]\\s*(?!.*(?:\\[Debug\\]:|<\\w+>)).*$");
        return pattern.matcher(line).find();
    }

}
