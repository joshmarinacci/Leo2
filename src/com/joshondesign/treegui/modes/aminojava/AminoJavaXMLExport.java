package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.XMLParser;
import com.joshondesign.xml.XMLWriter;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;


public class AminoJavaXMLExport extends JAction {
    private final SketchDocument document;
    private final Page page;
    private Stage demoStage;

    public AminoJavaXMLExport(SketchDocument document, Page page) {
        super();
        this.document = document;
        this.page = page;
    }

    @Override
    public void execute() {
        try {
            File file = File.createTempFile("foo",".xml");
            //PrintWriter pw = new PrintWriter(new FileOutputStream(file));
            PrintWriter pw = new PrintWriter(System.out);
            exportToXML(pw, page, document);
            loadAndRun(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAndRun(File file) throws Exception {
        Doc xml = XMLParser.parse(file);
        Node node = AminoParser.parsePage(xml.root());
        if(demoStage == null) {
            demoStage = Stage.createStage();
            demoStage.setWidth(600);
            demoStage.setHeight(400);
            demoStage.setAlwaysOnTop(true);
        }
        node.setTranslateX(0);
        node.setTranslateY(0);
        demoStage.setContent(node);
        demoStage.raiseToTop();
    }

    @Override
    public String getShortName() {
        return "Run";
    }

    public static void exportToXML(PrintWriter printWriter, Page page, SketchDocument document) throws URISyntaxException {
        XMLWriter xml = new XMLWriter(printWriter, new URI(""));
        xml.header();
        xml.start("page");
        // render non-visual nodes first

        xml.start("nodes");
        //render visual nodes next
        for(Layer layer : page.children()) {
            for(SketchNode node : layer.children()) {
                if(node instanceof DynamicNode) {
                    if(node.isVisual()) {
                        visualNodeToXML(xml, (DynamicNode) node, 200, 200, false);
                    } else {
                        nonvisualNodeToXML(node, xml);
                    }
                }
            }
        }
        xml.end();

        //bindings
        xml.start("bindings");
        for(Binding binding : document.getBindings()) {
            xml.start("binding");
            xml.attr("sourceid", binding.getSource().getId());
            xml.attr("sourceprop", binding.getSourceProperty());
            xml.attr("sourcetype",binding.getSourceType().getName());
            xml.attr("targetid",binding.getTarget().getId());
            xml.attr("targetprop",binding.getTargetProperty());
            xml.attr("targettype",binding.getTargetType().getName());
            xml.end();
        }
        xml.end();

        xml.end();
        xml.close();
    }

    private static void nonvisualNodeToXML(SketchNode node, XMLWriter xml) {
        if(!node.isVisual() && node instanceof DynamicNode) {
            DynamicNode nd = (DynamicNode) node;
            u.p("spitting out " + nd);
            xml.start("node")
                    .attr("class", nd.getProperty("class").encode())
                    .attr("name", nd.getName())
                    .attr("id", nd.getId())
            ;
            for (Property prop : nd.getSortedProperties()) {
                xml.start("property");
                xml.attr("name", prop.getName());
                xml.attr("value", prop.encode());
                xml.attr("type", prop.getType().getName());
                xml.attr("exported",Boolean.toString(prop.isExported()));
                xml.attr("visible", Boolean.toString(prop.isVisible()));
                xml.attr("bindable", Boolean.toString(prop.isBindable()));
                if(prop.getExportName() != null) {
                    xml.attr("exportname", prop.getExportName());
                }
                xml.end();
            }
            xml.end();
        }
        for(SketchNode child : node.children()) {
            nonvisualNodeToXML(child, xml);
        }
    }

    private static void visualNodeToXML(XMLWriter xml, DynamicNode node, double width, double height, boolean parentAnchor) {
        xml.start("node")
                .attr("id", node.getId())
                .attr("class", node.getProperty("class").encode())
                .attr("visual", Boolean.toString(node.isVisual()))
                .attr("resizable", Boolean.toString(node.isResizable()))
                .attr("container", Boolean.toString(node.isContainer()))
                .attr("custom", Boolean.toString(node.isCustom()))
                .attr("name", node.getName())
        ;
        if(node.isCustom()) {
            xml.attr("customClass",node.getProperty("customClass").getStringValue());
        }
        for (Property prop : node.getSortedProperties()) {

            if(prop.getName().equals("customClass")) continue;
            xml.start("property");
            xml.attr("name", prop.getName());
            if(prop.getExportName() != null) {
                xml.attr("exportname", prop.getExportName());
            }
            if(prop.getType().isEnum()) {
                xml.attr("enum", "true");
            }
            xml.attr("value", prop.encode())
                .attr("type", prop.getType().getName());
            xml.attr("exported",Boolean.toString(prop.isExported()));
            xml.attr("visible", Boolean.toString(prop.isVisible()));
            xml.attr("bindable", Boolean.toString(prop.isBindable()));
            xml.end();
        }

        if(parentAnchor) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            double tx = node.getTranslateX();
            double ty = node.getTranslateY();
            xml.start("property","name","right")
                    .attr("type","java.lang.Double")
                    .attr("value",""+(width-tx - w))
                .end()
            ;
            xml.start("property","name","bottom")
                    .attr("type","java.lang.Double")
                    .attr("value",""+(height-ty-h))
                    .end();
        }

        xml.start("children");
        if(node.getSize() > 0) {
            for(SketchNode nd : node.children()) {
                DynamicNode nd2 = (DynamicNode) nd;
                if(nd2.isVisual()) {
                    visualNodeToXML(xml, nd2,
                            node.getProperty("width").getDoubleValue(),
                            node.getProperty("height").getDoubleValue(), true);
                } else {
                    nonvisualNodeToXML(nd2, xml);
                }
            }
        }
        xml.end();
        xml.end();
    }

    public static class Save extends JAction {

        private final SketchDocument doc;
        private final Canvas canvas;

        public Save(Canvas canvas, SketchDocument doc) {
            this.doc = doc;
            this.canvas = canvas;
        }

        @Override
        public void execute() {

            File file = null;

            //get a file to write to
            if(doc.getFile() != null) {
                file = doc.getFile();
            } else {
                java.awt.FileDialog fd = new java.awt.FileDialog((Frame) null);
                fd.setMode(FileDialog.SAVE);
                fd.setVisible(true);
                if(fd.getFile() == null) {
                    return;
                }
                String filename = fd.getFile();
                if(!filename.toLowerCase().endsWith(".xml")) {
                    filename += ".xml";
                }
                file = new File(fd.getDirectory(),filename);
            }


            //write to the file
            try {
                if(file != null) {
                    exportToXML(new PrintWriter(new FileOutputStream(file)), doc.get(0), doc);
                    u.p("exported to : " + file.getAbsolutePath());
                    doc.setFile(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getShortName() {
            return "Save";
        }
    }

    public static class Test extends JAction {
        private final Canvas canvas;
        private final SketchDocument document;
        Stage demoStage = null;

        public Test(Canvas canvas, SketchDocument document) {
            this.canvas = canvas;
            this.document = document;
        }

        @Override
        public void execute() {
            try {
                File file = document.getFile();
                Doc xml = XMLParser.parse(file);
                Node node = AminoParser.parsePage(xml.root());
                if(demoStage == null) {
                    demoStage = Stage.createStage();
                    demoStage.setWidth(600);
                    demoStage.setHeight(400);
                    demoStage.setAlwaysOnTop(true);
                }
                node.setTranslateX(0);
                node.setTranslateY(0);
                demoStage.setContent(node);
                demoStage.raiseToTop();
            } catch (Exception ex) {
                u.p(ex);
            }
        }

        @Override
        public String getShortName() {
            return "Test";
        }
    }
}
