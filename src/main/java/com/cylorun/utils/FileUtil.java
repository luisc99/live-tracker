package com.cylorun.utils;

import java.io.File;

public class FileUtil {
    public static File getLastModified(File parentFolder) {
        if (parentFolder == null) {
            return null;
        }

        File res = null;
        long l = 0;
        for (File f : parentFolder.listFiles()) {
            if (f.lastModified() > l) {
                l = f.lastModified();
                res = f;
            }
        }
        return res;
    }
}
