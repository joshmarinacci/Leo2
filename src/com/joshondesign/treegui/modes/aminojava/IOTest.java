package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.BindingUtils;
import com.joshondesign.treegui.Leo2;
import com.joshondesign.treegui.Mode;
import com.joshondesign.treegui.actions.XMLExport;
import com.joshondesign.treegui.actions.XMLImport;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojs.AminoJSMode;
import com.joshondesign.treegui.modes.aminojs.TriggerProp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import org.joshy.gfx.util.u;

public class IOTest {
    public static  void main(String ... args) throws Exception {
        Leo2.initModes();
        testJavaExport();
        testJSExport();
    }

    private static void testJavaExport() throws Exception {
        AminoJavaMode mode = new AminoJavaMode();

        SketchDocument doc = mode.createEmptyDoc();
        Layer layer = doc.get(0).get(0);
        layer.clear();
        DynamicNode listViewMaster = findSymbol(mode,"ListView");
        layer.add(listViewMaster.duplicate(null));
        DynamicNode buttonMaster = findSymbol(mode,"PushButton");
        DynamicNode buttonMasterDupe = (DynamicNode) buttonMaster.duplicate(null);
        layer.add(buttonMasterDupe);

        DynamicNode flickrMaster = findSymbol(mode,"FlickrQuery");
        DynamicNode flickrMasterDupe = (DynamicNode) flickrMaster.duplicate(null);
        layer.add(flickrMasterDupe);
        Binding binding = BindingUtils.createBinding(buttonMasterDupe, "trigger", flickrMasterDupe, "execute");
        doc.getBindings().add(binding);

        File file = File.createTempFile("foo","xml");
        XMLExport.exportToXML(new PrintWriter(new FileOutputStream(file)), doc.get(0), doc);
        u.p("exported to : \n" + file.getAbsolutePath());

        SketchDocument doc2 = XMLImport.read(file);

        assertEquals(doc2.getSize(), 1);
        DynamicNode listView = (DynamicNode) doc2.get(0).get(0).get(0);
        assertEquals(listView.getSize(),0);
        //assertEquals(listView.getProperty("width").getDoubleValue(),60.0);

        Property subProp = listView.getProperty("selectedObject");
        assertEquals(subProp.isBindable(),true);
        assertEquals(subProp.isCompound(),true);
        assertEquals(subProp.getMasterProperty(),"model");

        DynamicNode button = (DynamicNode) doc2.get(0).get(0).get(1);
        assertEquals(button.getSize(),0);
        assertEquals(button.getProperty("trigger").getType(), TriggerProp.class);

        assertEquals(doc2.getBindings().size(),1);
        Binding bind = doc2.getBindings().get(0);
        assertEquals(bind.getSource(), button);
        assertEquals(bind.getSourceProperty().getName(),"trigger");
        assertEquals(bind.getSourceProperty().getType(),TriggerProp.class);
        assertEquals(listView.getProperty("class").getName(),"class");
        assertEquals(listView.getProperty("class").getStringValue(),("org.joshy.gfx.node.control.ListView"));

        File file2 = File.createTempFile("foo","xml");
        u.p("exporting to \n" + file2.getAbsolutePath());
        XMLExport.exportToXML(new PrintWriter(new FileOutputStream(file2)), doc2.get(0), doc2);
        SketchDocument doc3 = XMLImport.read(file2);
        DynamicNode lv3 = (DynamicNode) doc2.get(0).get(0).get(0);
        assertEquals(lv3.getSize(), 0);
    }

    private static void testJSExport() throws Exception {
        AminoJSMode mode = new AminoJSMode();
        SketchDocument doc = mode.createEmptyDoc();

        DynamicNode flickrMaster = findSymbol(mode,"FlickrQuery");
        DynamicNode buttonMaster = findSymbol(mode,"PushButton");
        DynamicNode sliderMaster = findSymbol(mode,"Slider");
        DynamicNode panelMaster = findSymbol(mode, "PlainPanel");
        Layer layer = doc.get(0).get(0);
        layer.add(flickrMaster.duplicate(null));
        layer.add(buttonMaster.duplicate(null));

        DynamicNode sliderOrig = (DynamicNode) sliderMaster.duplicate(null);
        sliderOrig.getProperty("minValue").setDoubleValue(45.6);
        sliderOrig.getProperty("maxValue").setDoubleValue(46.6);

        layer.add(sliderOrig);
        layer.add(findSymbol(mode, "Label").duplicate(null));
        layer.add(panelMaster.duplicate(null));

        File file = File.createTempFile("foo","xml");
        XMLExport.exportToXML(new PrintWriter(new FileOutputStream(file)), doc.get(0), doc);
        u.p("exported to : " + file.getAbsolutePath());

        SketchDocument doc2 = XMLImport.read(file);
        DynamicNode buttonLoaded = (DynamicNode) doc2.get(0).get(0).get(3);

        File file2 = File.createTempFile("foo","xml");
        u.p("exporting to \n" + file2.getAbsolutePath());
        XMLExport.exportToXML(new PrintWriter(new FileOutputStream(file2)), doc2.get(0), doc2);
        SketchDocument doc3 = XMLImport.read(file2);


        //test that the panel's class property is restored
        DynamicNode panelLoaded = (DynamicNode) doc3.get(0).get(0).get(5);
        assertNotNull(panelLoaded.getProperty("class").getRawValue());



        DynamicNode sliderLoaded = (DynamicNode) doc3.get(0).get(0).get(3);
        assertNotNull(sliderLoaded.getProperty("class").getRawValue());
        assertEquals(sliderLoaded.getProperty("minValue").getDoubleValue(),45.6);
        assertEquals(sliderLoaded.getProperty("maxValue").getDoubleValue(),46.6);


        DynamicNode labelLoaded = (DynamicNode) doc3.get(0).get(0).get(4);
    }

    private static void assertNotNull(Object object) throws Exception {
        if(object == null) throw new Exception();
    }

    private static void assertEquals(boolean a, boolean b) throws Exception {
        if(a != b) throw new Exception();
    }
    private static void assertEquals(Object a, Object b) throws Exception {
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

    private static DynamicNode findSymbol(Mode mode, String name) {
        for(SketchNode node : mode.getSymbols().children()) {
            DynamicNode nd = (DynamicNode) node;
            if(nd.getName().equals(name)) {
                return nd;
            }
        }
        return null;
    }
}
