package com.joshondesign.treegui.modes.aminolang;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.docmodel.*;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import com.joshondesign.treegui.modes.aminojs.ActionProp;
import com.joshondesign.treegui.modes.aminojs.TriggerProp;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.util.u;

public class AminoLangJSONExport extends AminoAction {
    private final SketchDocument doc;
    private final boolean useRandomFile;
    private ArrayList<DynamicNode> transitions;

    private File exportFile = null;

    public AminoLangJSONExport(SketchDocument doc, boolean b) {
        this.doc = doc;
        this.useRandomFile =  b;
    }

    @Override
    public void execute() throws Exception {
        if(exportFile == null) {

            FileDialog fd = new FileDialog((Frame)null);
            fd.setTitle("Choose JSON Export File");
            fd.setMode(FileDialog.SAVE);
            fd.setVisible(true);
            if(fd.getFile() == null) return;
            exportFile = new File(fd.getDirectory(),fd.getFile());
            if(!exportFile.getName().toLowerCase().endsWith(".json")) {
                exportFile = new File(exportFile.getParent(),exportFile.getName()+".json");
            }
        }


        /*
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

        */

        String tree = exportTree(exportFile.getParentFile());
        u.stringToFile(tree, exportFile);

        u.p("exported to file " + exportFile.getAbsolutePath());

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

    private String exportTree(File parentFile) {
        transitions = new ArrayList<DynamicNode>();
        JSONPrinter json = new JSONPrinter();
        json.openObject().set("type","Document");
        json.openArray("children");
        for(Page page : doc.children()) {
            json.openObject().set("type","Group");
            json.openArray("children");
            for(Layer layer : page.children()) {
                for(SketchNode node : layer.children()) {
                    if(node instanceof Group) {
                        exportGroupNode((Group)node,json, parentFile);
                    } else {
                        exportNode((DynamicNode)node,json, parentFile);
                    }
                }
            }
            json.closeArray();
            json.closeObject();
        }
        json.closeArray();
        json.openArray("bindings");

        for(DynamicNode trans : transitions) {
            json.openObject();
            json.set("id",trans.getId());
            json.set("type", "Transition");
            json.set("kind","slideInRight");

            for(Binding binding : doc.getBindings()) {
                if(binding.getSource() == trans) {
                    u.p("found the push target " + binding.getTarget().getId());
                    json.set("pushTarget",binding.getTarget().getId());
                }
                if(binding.getTarget() == trans) {
                    u.p("found the push trigger " + binding.getSource().getId());
                    json.set("pushTrigger",binding.getSource().getId());
                }
            }
            json.closeObject();
        }

        json.closeArray();
        json.closeObject();
        return json.toStringBuffer().toString();
    }

    private void exportGroupNode(Group group, JSONPrinter json, File parentFile) {
        json.openObject()
                .set("type","Group")
                .set("id",group.getId())
                .set("tx",group.getTranslateX())
                .set("ty",group.getTranslateY());
        json.openArray("children");
        for(SketchNode node : group.children()) {
            if(node instanceof Group) {
                exportGroupNode((Group)node,json, parentFile);
            } else {
                exportNode((DynamicNode)node,json, parentFile);
            }
        }
        json.closeArray();
        json.closeObject();
    }
    private void exportNode(DynamicNode dnode, JSONPrinter json, File parentFile) {
        if(dnode.getName().equals("Transition")) {
            transitions.add(dnode);
            return;
        }
        json.openObject()
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
            if(prop.getType() == ActionProp.class) {
                continue;
            }
            if(prop.getType() == TriggerProp.class) {
                continue;
            }

            if(Defs.IconSymbols.class.isAssignableFrom(prop.getType())) {
                Defs.IconSymbols icon = (Defs.IconSymbols) prop.getEnumValue();
                if(icon == Defs.IconSymbols.None) continue;
                File file = exportIcon(icon, parentFile);
                json.set("url",file.getName());
                continue;
            }


            u.p("SHOULDN'T BE HERE. exporting node property " + name);
            if(prop.getRawValue() == null) {
                u.p("  it's null. skipping");
                continue;
            }
            json.set(name, prop.getRawValue().toString());
        }

        //assume they are all controls
        if(dnode.getParent() instanceof DynamicNode) {
            DynamicNode parent = (DynamicNode) dnode.getParent();
            if("AnchorPanel".equals(parent.getName())) {
                double pw = parent.getWidth();
                double ph = parent.getHeight();
                double cw = dnode.getWidth();
                double ch = dnode.getHeight();
                double cx = dnode.getTranslateX();
                double cy = dnode.getTranslateY();
                json.set("left",cx).set("top",cy).set("right",pw-cx-cw).set("bottom",ph-cy-ch);
            }
        }

        if(dnode.isContainer()) {
            json.openArray("children");
            for(SketchNode node : dnode.children()) {
                if(node instanceof Group) {
                    exportGroupNode((Group)node,json, parentFile);
                } else {
                    exportNode((DynamicNode)node,json, parentFile);
                }
            }
            json.closeArray();
        }

        json.closeObject();
    }

    private File exportIcon(Defs.IconSymbols symbol, File parentFile) {
        Font font = Font.name("FontAwesome").size(30).resolve();
        BufferedImage img = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(font.getAWTFont());
        g2.setPaint(java.awt.Color.BLACK);
        g2.drawString(symbol.getChar()+"",0,30);
        g2.dispose();
        File file = new File(parentFile,symbol.name()+".png");
        try {
            ImageIO.write(img,"png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
