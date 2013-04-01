package com.joshondesign.treegui.modes.aminolang;

import com.joshondesign.treegui.StringUtils;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.joshy.gfx.event.AminoAction;

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
        subs.put("tree",tree);

        StringUtils.applyTemplate(new File(templatedir, "aminolangcanvas_template.html"),
                new File(outdir,"test.html"), subs);
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
        Page page = doc.get(0);
        StringBuffer sb = new StringBuffer();
        sb.append("{ type:'Group', children:[\n");
        for(Layer layer : page.children()) {
            for(SketchNode node : layer.children()) {
                DynamicNode dnode = (DynamicNode) node;
                sb.append(exportNode(dnode));
                sb.append(",\n");
            }
        }
        sb.append("]}\n");
        return sb.toString();
    }

    private StringBuffer exportNode(DynamicNode dnode) {
        StringBuffer sb = new StringBuffer();
        sb.append("{ type:'"+dnode.getName()+"', \n");
        for(Property prop : dnode.getProperties()) {
            if("class".equals(prop.getName())) continue;
            String name = prop.getName();
            if(prop.getExportName() != null) {
                name = prop.getExportName();
            }
            if(prop.getType().isAssignableFrom(String.class)) {
                sb.append("  "+name+":'"+prop.getRawValue()+"',\n");
            } else {
                sb.append("  "+name+":"+prop.getRawValue()+",\n");
            }
        }
        sb.append("}\n");
        return sb;
    }
}
