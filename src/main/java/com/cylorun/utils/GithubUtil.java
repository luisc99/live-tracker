package com.cylorun.utils;

import com.cylorun.Tracker;
import org.apache.logging.log4j.Level;
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
