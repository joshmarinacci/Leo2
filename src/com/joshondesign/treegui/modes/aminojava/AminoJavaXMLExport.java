package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.actions.XMLExport;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.XMLParser;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import org.joshy.gfx.event.AminoAction;
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
            XMLExport.exportToXML(pw, page, document);
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
                    XMLExport.exportToXML(new PrintWriter(new FileOutputStream(file)), doc.get(0), doc);
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

    public static class Test extends AminoAction {
        private final SketchDocument document;
        Stage demoStage = null;

        public Test(SketchDocument document) {
            this.document = document;
        }

        @Override
        public void execute() {
            try {
                File file = document.getFile();
                if(file == null) {
                    file = File.createTempFile("blah","xml");
                }
                //save the file
                XMLExport.exportToXML(new PrintWriter(new FileOutputStream(file)), document.get(0), document);
                //now reload it

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
        public CharSequence getDisplayName() {
            return "Test";
        }
    }
}
