package com.cylorun.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    public static String toDetailedString(Throwable t) {
        StringWriter out = new StringWriter();
        out.write(t.toString() + "\n");
        t.printStackTrace(new PrintWriter(out));
        return out.toString();
    }
}
