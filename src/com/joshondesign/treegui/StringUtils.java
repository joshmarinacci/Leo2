package com.joshondesign.treegui;

import org.joshy.gfx.util.u;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class StringUtils {
    public static File createTempDir() throws IOException {
        File tempdir = File.createTempFile("foo","dir");
        tempdir.delete();
        tempdir.mkdir();
        return tempdir;
    }

    public static void applyTemplate(File in, File out, Map<String, String> subs) {
        try {
            String str = u.fileToString(new FileInputStream(in));
            for(Map.Entry<String, String> item : subs.entrySet()) {
                str = str.replaceAll("\\$\\{" + item.getKey() + "\\}", item.getValue());
            }
            u.stringToFile(str,out);
            u.p("writing");
            u.p(out.getAbsolutePath());
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

    public static void copyFileToDir(File infile, File outdir) throws IOException {
        u.streamToFile(new FileInputStream(infile),
                new File(outdir,infile.getName())
                );
    }
}
