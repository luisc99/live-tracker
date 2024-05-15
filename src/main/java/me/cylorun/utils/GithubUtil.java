package me.cylorun.utils;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import me.cylorun.Tracker;
import org.apache.logging.log4j.Level;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;

public class GithubUtil {

    public static GitHub getGithub() {
        try {
            return GitHub.connectAnonymously();
        } catch (IOException e) {
            Tracker.log(Level.ERROR, "Could not connect to github");
            throw new RuntimeException(e);
        }
    }
}
