package com.joshondesign.treegui.modes.aminolang;

import com.joshondesign.treegui.StringUtils;
import com.joshondesign.treegui.docmodel.*;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.util.u;

public class AminoLangJSONExport extends AminoAction {
    private final SketchDocument doc;
    private final boolean useRandomFile;

    public AminoLangJSONExport(SketchDocument doc, boolean b) {
        this.doc = doc;
        this.useRandomFile =  b;
    }

    @Override
    public void execute() throws Exception {
        File outdir = new File("/Users/josh/projects/temp");
        outdir.mkdir();
        File templatedir = new File("resources/");

        Map<String,String> subs = new HashMap<String,String>();
        String tree = exportTree();
        //subs.put("tree",tree);

        StringUtils.applyTemplate(new File(templatedir, "aminolangcanvas_template.html"),
                new File(outdir,"test.html"), subs);
        File jsonOut = new File(outdir,"scene.json");
        u.stringToFile(tree,jsonOut);
        /*
        StringUtils.copyFile(new File(templatedir, "bootstrap.min.css"),
                new File(outdir, "bootstrap.min.css"));
                */
        /*
        File trimfile = new File("/Users/josh/");
        String partialPath = outdir.getAbsolutePath().substring((int)trimfile.getAbsolutePath().length());
        OSUtil.openBrowser("http://localhost/~josh/" + partialPath + "/test.html");
        */
    }

    private String exportTree() {
        JSONPrinter json = new JSONPrinter();
        json.open().set("type","Document").openArray("children");
        for(Page page : doc.children()) {
            json.open().set("type","Group")
                    .openArray("children");
            for(Layer layer : page.children()) {
                for(SketchNode node : layer.children()) {
                    if(node instanceof Group) {
                        exportGroupNode((Group)node,json);
                    } else {
                        exportNode((DynamicNode)node,json);
                    }
                }
            }
            json.closeArray().close();
        }
        json.closeArray().close();
        return json.toStringBuffer().toString();
    }

    private void exportGroupNode(Group group, JSONPrinter json) {
        json.open()
                .set("type","Group")
                .set("id",group.getId())
                .set("tx",group.getTranslateX())
                .set("ty",group.getTranslateY());
        json.openArray("children");
        for(SketchNode node : group.children()) {
            if(node instanceof Group) {
                exportGroupNode((Group)node,json);
            } else {
                exportNode((DynamicNode)node,json);
            }
        }
        json.closeArray();
        json.close();
    }
    private void exportNode(DynamicNode dnode, JSONPrinter json) {
        json.open()
                .set("type",dnode.getName())
                .set("id",dnode.getId());
        for(Property prop : dnode.getProperties()) {
            if("class".equals(prop.getName())) continue;
            String name = prop.getName();
            if(prop.getExportName() != null) {
                name = prop.getExportName();
            }

            if(!prop.isExported()) continue;
            if(String.class.isAssignableFrom(prop.getType())) {
                json.set(name, prop.getStringValue());
                continue;
            }
            if(CharSequence.class.isAssignableFrom(prop.getType())) {
                json.set(name, prop.getStringValue());
                continue;
            }

            if(prop.getType().isAssignableFrom(Boolean.TYPE)) {
                json.set(name, prop.getBooleanValue());
                continue;
            }
            if(prop.getType().isAssignableFrom(Double.TYPE)) {
                json.set(name, prop.getDoubleValue());
                continue;
            }
            if(prop.getType().isAssignableFrom(Integer.TYPE)) {
                json.set(name, prop.getIntegerValue());
                continue;
            }
            if(FlatColor.class.isAssignableFrom(prop.getType())) {
                FlatColor fc = (FlatColor) prop.getColorValue();
                String color = "#"+Integer.toHexString(fc.getRGBA()).substring(2);
                json.set(name, color);
                continue;
            }
            json.set(name, prop.getRawValue().toString());
        }
        json.close();
    }
}
