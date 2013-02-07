package com.joshondesign.treegui.modes.bootstrap;

import com.joshondesign.treegui.StringUtils;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.util.OSUtil;

public class BootstrapHTMLExport extends AminoAction {
    private final SketchDocument doc;
    private final boolean useRandomFile;

    public BootstrapHTMLExport(SketchDocument doc, boolean useRandomFile) {
        this.doc = doc;
        this.useRandomFile =  useRandomFile;
    }

    @Override
    public void execute() throws Exception {
        File dir = new File("/Users/josh/projects/temp");
        File templatedir = new File("resources/");

        StringWriter treeContent = new StringWriter();
        PrintWriter treeOut = new PrintWriter(treeContent);
        StringWriter cssOut = new StringWriter();
        exportTree(treeOut, new PrintWriter(cssOut));

        Map<String,String> subs = new HashMap<String,String>();
        subs.put("tree",treeContent.toString());
        subs.put("css",cssOut.toString());
        //subs.put("setup", setupContent.toString());
        File html = new File(dir,"boot.html");
        StringUtils.applyTemplate(new File(templatedir, "boot_template.html"), html, subs);
        StringUtils.copyFile(new File(templatedir, "bootstrap.min.css"),
                new File(dir, "bootstrap.min.css"));
        File trimfile = new File("/Users/josh/");
        String partialPath = dir.getAbsolutePath().substring((int)trimfile.getAbsolutePath().length());
        OSUtil.openBrowser("http://localhost/~josh/" + partialPath + "/" + html.getName());
    }

    private void exportTree(PrintWriter treeOut, PrintWriter cssOut) {
        Page page = doc.get(0);
        for(Layer layer : page.children()) {
            for(SketchNode node : layer.children()) {
                DynamicNode dnode = (DynamicNode) node;
                exportNode(treeOut, cssOut, dnode);
            }
        }
        cssOut.close();
    }

    private void exportNode(PrintWriter treeOut, PrintWriter cssOut, DynamicNode dnode) {
        StringBuffer cssClass = new StringBuffer();
        cssClass.append("btn");
        if(dnode.hasProperty("primary")) {
            if(dnode.getProperty("primary").getBooleanValue()) {
                cssClass.append(" btn-primary");
            }
        }
        if(dnode.hasProperty("enabled")) {
            if(!dnode.getProperty("enabled").getBooleanValue()) {
                cssClass.append(" disabled");
            }
        }
        treeOut.println("<button id='"+dnode.getId()+"' type='button' class='"+cssClass.toString()+"'>"+
                dnode.getProperty("text").getStringValue()+
                "</button>");

        int x = (int) dnode.getProperty("translateX").getDoubleValue();
        int y = (int) dnode.getProperty("translateY").getDoubleValue();
        cssOut.println("    #" + dnode.getId() + " { \n" +
                "        position: absolute; \n" +
                "        left: " + x + "px;\n" +
                "        top: " + y + "px;\n" +
                "    }\n");
    }
}
