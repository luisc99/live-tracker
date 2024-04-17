package me.cylorun.io.minecraft.logs;

import me.cylorun.enums.LogEventType;
import me.cylorun.io.minecraft.world.WorldFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        String chatLogRegex = "^\\[\\d{2}:\\d{2}:\\d{2}\\]\\s*\\[Render\\s+thread\\/INFO\\]:\\s*\\[CHAT\\]\\s*(?!.*(?:\\[Debug\\]:|<\\w+>)).*$";
        String excludeUserRegex = String.format("^(?!.*%s).*$", username);
        String serverDeathRegex = String.format("^\\[\\d{2}:\\d{2}:\\d{2}\\] \\[Server thread\\/INFO\\]: %s (\\S.*)$", username);


        Pattern chatLogPattern = Pattern.compile(chatLogRegex);
        Pattern serverDeathPattern = Pattern.compile(serverDeathRegex);
        Pattern excludeUserPattern = Pattern.compile(excludeUserRegex);
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
            System.out.println(l);
            if (chatLogPattern.matcher(l).find()) {
//                System.out.println(lines.get(lines.indexOf(l)-1));
                if (lines.indexOf(l) != 0 && serverDeathPattern.matcher(lines.get(lines.indexOf(l)-1)).find()) { // checks if the previous line is the server death msg
                    System.out.println("yoooo");
                    if (setRespawn) {
                        res.add(LogEventType.HUNGER_RESET);
                    } else {
                        res.add(LogEventType.DEATH);
                    }
                }

                // ALSO GETS TRIGGERED FROM MOST COMMANDS, doesnt really matter, but theres probably some other cases too
                if (excludeUserPattern.matcher(l).find()) { // doesn't contain the username and is a "chat-log" message
                    setRespawn = true;
                }

            }

        }

        return res;
    }

    public static int timeBetween(String lineA, String lineB) { // in seconds

        return 0;
    }
    public static long getTime(String line){
        return 0L;
    }

}
