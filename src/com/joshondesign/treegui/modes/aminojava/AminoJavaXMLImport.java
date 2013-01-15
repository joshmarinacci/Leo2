package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.TreeGui;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.util.u;

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
            Page page = processPage(xml.root());
            doc.clear();
            doc.add(page);
            canvas.setMasterRoot(page.get(0));
            canvas.setEditRoot(page.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Page processPage(Elem root) throws XPathExpressionException, ClassNotFoundException {
        Page page = new Page();

        Layer layer = new Layer();
        page.add(layer);

        Map<String, SketchNode> ids = new HashMap<String, SketchNode>();
        for(Elem vis : root.xpath("nodes/node")) {
            DynamicNode node = processNode(vis,ids);
            layer.add(node);
        }
        //bind them together
        canvas.getBindings().clear();
        for(Elem binding : root.xpath("bindings/binding")) {
            Binding b = processBinding(binding, ids);
            canvas.getBindings().add(b);
        }
        return page;
    }

    private Binding processBinding(Elem elem, Map<String, SketchNode> ids) {
        Binding binding = new Binding();
        binding.setSource(ids.get(elem.attr("sourceid")));
        binding.setSourceProperty(elem.attr("sourceprop"));
        binding.setTarget(ids.get(elem.attr("targetid")));
        binding.setTargetProperty(elem.attr("targetprop"));
        return binding;
    }

    private DynamicNode processNode(Elem xml, Map<String, SketchNode> ids) throws XPathExpressionException, ClassNotFoundException {
        DynamicNode node = new DynamicNode();
        node.setName(xml.attr("name"));
        node.addProperty(new Property("class", String.class, xml.attr("class")));
        node.setVisual(xml.attrEquals("visual", "true"));
        node.setContainer(xml.attrEquals("container", "true"));
        node.setResizable(xml.attrEquals("resizable", "true"));
        node.setCustom(xml.attrEquals("custom", "true"));
        if(node.isCustom()) {
            node.addProperty(new Property("customClass",String.class,xml.attr("customClass")));
        }
        node.setId(xml.attr("id"));
        ids.put(node.getId(),node);
        for(Elem prop : xml.xpath("property")) {
            Object val = null;
            Class type = Class.forName(prop.attr("type"));
            String sval = prop.attr("value");
            if(type == Double.class) {
                val = (Double)Double.parseDouble(sval);
            }
            if(type == Boolean.class) {
                val = (Boolean)Boolean.parseBoolean(sval);
            }
            if(type == String.class) {
                val = (String)sval;
            }
            if(type == CharSequence.class) {
                val = (CharSequence)sval;
            }
            if(type == FlatColor.class) {
                val = new FlatColor(sval);
            }
            if(prop.attrEquals("enum","true")) {
                val = Enum.valueOf(type, sval);
            }
            String name = prop.attr("name");
            if(name.equals("prefWidth")) {
                name = "width";
            }
            if(name.equals("prefHeight")) {
                name = "height";
            }
            Property property = new Property(name, type, val);
            property.setExported(prop.attrEquals("exported","true"));
            if(prop.hasAttr("exportname")) {
                property.setExportName(prop.attr("exportname"));
            }
            property.setVisible(prop.attrEquals("visible","true"));
            property.setBindable(prop.attrEquals("bindable","true"));
            node.addProperty(property);
        }
        u.p("restored node " + node.getName());
        for(Property p : node.getSortedProperties()) {
            u.p("   " + p.getName() + " " + p.getType());
        }


        node.setDrawDelegate(TreeGui.drawMap.get(node.getName()));
        if(!node.isVisual()) {
            node.setDrawDelegate(TreeGui.drawMap.get("servicebase"));

        }
        for(Elem echild : xml.xpath("children/node")) {
            node.add(processNode(echild, ids));
        }
        return node;
    }

    @Override
    public String getShortName() {
        return "Open";
    }
}
