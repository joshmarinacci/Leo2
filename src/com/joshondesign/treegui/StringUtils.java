package com.joshondesign.treegui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import org.joshy.gfx.util.u;

public class StringUtils {
    public static void applyTemplate(File in, File out, Map<String, String> subs) {
        try {
            String str = u.fileToString(new FileInputStream(in));
            for(Map.Entry<String, String> item : subs.entrySet()) {
                str = str.replaceAll("\\$\\{" + item.getKey() + "\\}", item.getValue());
            }
            u.stringToFile(str,out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File infile, File outfile) throws IOException {
        u.streamToFile(new FileInputStream(infile),outfile);
    }
    public static void copyFile(URL url, File outfile) throws IOException {
        u.streamToFile(url.openStream(),outfile);
    }

}
