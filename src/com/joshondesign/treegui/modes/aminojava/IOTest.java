package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import org.joshy.gfx.util.u;

public class IOTest {
    public static  void main(String ... args) throws Exception {
        AminoJavaMode mode = new AminoJavaMode();
        DynamicNode master = findSymbol(mode,"ListView");

        SketchDocument doc = new SketchDocument();
        Layer layer = new Layer();
        layer.add(master.duplicate(null));
        Page page = new Page();
        page.add(layer);
        doc.add(page);

        File file = File.createTempFile("foo","xml");
        AminoJavaXMLExport.exportToXML(new PrintWriter(new FileOutputStream(file)), doc.get(0), doc);
        u.p("exported to : " + file.getAbsolutePath());

        SketchDocument doc2 = AminoJavaXMLImport.read(file);
        u.p("doc 2 ");

        assertEquals(doc2.getSize(), 1);
        DynamicNode listView = (DynamicNode) doc2.get(0).get(0).get(0);
        assertEquals(listView.getSize(),0);
        assertEquals(listView.getProperty("width").getDoubleValue(),60.0);

        Property subProp = listView.getProperty("selectedObject");
        assertEquals(subProp.isBindable(),true);
        assertEquals(subProp.isCompound(),true);
        assertEquals(subProp.getMasterProperty(),"model");


        /*
                Property subProp = new Property("selectedObject",Object.class,null);
        subProp.setBindable(true);
        subProp.setCompound(true);
        subProp.setMasterProperty("model");

         */


    }

    private static void assertEquals(boolean a, boolean b) throws Exception {
        if(a != b) throw new Exception();
    }
    private static void assertEquals(int a, int b) throws Exception {
        if(a != b) throw new Exception();
    }
    private static void assertEquals(double a, double b) throws Exception {
        if(a != b) throw new Exception();
    }
    private static void assertEquals(String a, String b) throws Exception {
        if(!a.equals(b)) throw new Exception();
    }

    private static DynamicNode findSymbol(AminoJavaMode mode, String name) {
        for(SketchNode node : mode.getSymbols().children()) {
            DynamicNode nd = (DynamicNode) node;
            if(nd.getName().equals(name)) {
                return nd;
            }
        }
        return null;
    }
}
