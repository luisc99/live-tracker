package me.cylorun.io.minecraft.logs;

import me.cylorun.enums.LogEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LogParser {

    public static List<LogEvent> getAllEvents(List<String> lines) {
        List<LogEvent> res = new ArrayList<>();
        String respawnRegex = "^\\[\\d{2}:\\d{2}:\\d{2}\\]\\s*\\[Render\\s+thread\\/INFO\\]:\\s*\\[CHAT\\]\\s*(?!.*(?:\\[Debug\\]:|<\\w+>)).*$";

        Pattern respawnPattern = Pattern.compile(respawnRegex);
        for (String l : lines) {
                        /*
                        [15:51:29] [Render thread/INFO]: [CHAT] Respawn point set -- pass
                        [15:51:48] [Render thread/INFO]: [CHAT] [Debug]: Render Distance: 9 -- ignore
                        [15:51:05] [Render thread/INFO]: [CHAT] <cylorun> hi -- ignore
                        */

            if (respawnPattern.matcher(l).find()) {
                res.add(LogEvent.RESPAWN_SET);
                continue;
            }

        }

        return res;
    }
}
