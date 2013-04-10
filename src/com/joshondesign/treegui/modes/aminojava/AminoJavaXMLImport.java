package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.actions.XMLImport;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.XMLParser;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import org.joshy.gfx.event.Callback;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/12/13
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class AminoJavaXMLImport extends JAction {
    private final Canvas canvas;
    private final SketchDocument doc;

    public AminoJavaXMLImport(Canvas canvas, SketchDocument doc) {
        super();
        this.canvas = canvas;
        this.doc = doc;
    }

    @Override
    public void execute() {
        FileDialog fd = new FileDialog((Frame)null);
        fd.setMode(FileDialog.LOAD);
        fd.setVisible(true);
        if(fd.getFile() == null) return;
        File file = new File(fd.getDirectory(),fd.getFile());
        try {
            Doc xml = XMLParser.parse(file);
            XMLImport.processDocument(xml.root(),doc);
            doc.setFile(file);
            canvas.setMasterRoot(doc.get(0).get(0));
            canvas.setEditRoot(doc.get(0).get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getShortName() {
        return "Open";
    }

    public static SketchDocument open(File file, Canvas canvas) throws Exception {
        SketchDocument doc = XMLImport.read(file);
        canvas.setMasterRoot(doc.get(0).get(0));
        canvas.setEditRoot(doc.get(0).get(0));
        return doc;
    }


    public static class Open extends JAction {
        private Callback<SketchDocument> callback;

        @Override
        public void execute() {
            FileDialog fd = new FileDialog((Frame)null);
            fd.setMode(FileDialog.LOAD);
            fd.setVisible(true);
            if(fd.getFile() == null) return;
            File file = new File(fd.getDirectory(),fd.getFile());
            try {
                SketchDocument doc = XMLImport.read(file);
                if(callback != null) {
                    callback.call(doc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getShortName() {
            return "Open";
        }

        public void onOpened(Callback<SketchDocument> callback) {
            this.callback = callback;
        }
    }
}


