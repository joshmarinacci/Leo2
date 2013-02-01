package com.joshondesign.treegui.actions;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.Mode;
import com.joshondesign.treegui.docmodel.*;
import com.joshondesign.treegui.modes.aminojava.AminoJavaMode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import com.joshondesign.treegui.modes.aminojs.AminoJSMode;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.util.u;

public class XMLImport {
    public static SketchDocument read(File file) throws Exception {
        Doc xml = XMLParser.parse(file);
        SketchDocument doc = new SketchDocument();
        Page page = processPage(xml.root(), doc);
        doc.clear();
        doc.add(page);
        doc.setFile(file);
        return doc;
    }
    public static Page processPage(Elem root, SketchDocument doc) throws XPathExpressionException, ClassNotFoundException {
        Page page = new Page();
        u.p("mode id = " + root.attr("mode"));
        Mode mode = null;
        if(root.attrEquals("mode","com.joshondesign.modes.aminojs")) {
            mode = new AminoJSMode();
        } else {
            mode = new AminoJavaMode();
        }

        Layer layer = new Layer();
        page.add(layer);

        Map<String, SketchNode> ids = new HashMap<String, SketchNode>();
        for(Elem vis : root.xpath("nodes/node")) {
            DynamicNode node = processNode(vis,ids, mode.getDrawMap());
            layer.add(node);
        }
        //bind them together
        doc.getBindings().clear();
        for(Elem binding : root.xpath("bindings/binding")) {
            Binding b = processBinding(binding, ids);
            doc.getBindings().add(b);
        }
        return page;
    }

    private static Binding processBinding(Elem elem, Map<String, SketchNode> ids) {
        u.p("restoring: " + elem.attr("sourceprop") + " => " + elem.attr("targetprop"));
        u.p("virtual = " + elem.attr("sourcevirtual"));
        Binding binding = new Binding();
        DynamicNode srcObj = (DynamicNode) ids.get(elem.attr("sourceid"));
        binding.setSource(srcObj);
        DynamicNode tgtObj = (DynamicNode) ids.get(elem.attr("targetid"));
        binding.setTarget(tgtObj);
        binding.setSourceProperty(srcObj.getProperty(elem.attr("sourceprop")));
        binding.setTargetProperty(tgtObj.getProperty(elem.attr("targetprop")));
        u.p("source set to : "+ binding.getSourceProperty());
        u.p("target set to : " + binding.getTargetProperty());

        if(elem.attrEquals("sourcevirtual","true")) {
            String virtualSrc = elem.attr("sourceprop");
            String masterSrc = elem.attr("sourcemaster");

            //create the new proxy prop to map these. identical to what was created manually the first time.
            Property px = new Property(virtualSrc,Object.class,null);
            px.setProxy(true);
            px.setMasterProperty(masterSrc);
            binding.setSourceProperty(px);
        }
        return binding;
    }

    private static DynamicNode processNode(Elem xml, Map<String, SketchNode> ids, Map<String, DynamicNode.DrawDelegate> drawMap) throws XPathExpressionException, ClassNotFoundException {
        DynamicNode node = new DynamicNode();
        node.setName(xml.attr("name"));
        node.addProperty(new Property("class", String.class, xml.attr("class")));
        node.setVisual(xml.attrEquals("visual", "true"));
        node.setContainer(xml.attrEquals("container", "true"));
        node.setResize(Resize.valueOf(xml.attr("resize")));
        node.setCustom(xml.attrEquals("custom", "true"));
        if(node.isCustom()) {
            node.addProperty(new Property("customClass",String.class,xml.attr("customClass")));
        }
        node.setId(xml.attr("id"));
        ids.put(node.getId(),node);
        for(Elem prop : xml.xpath("property")) {
            Object val = null;
            Class type = null;
            if(prop.attrEquals("type","boolean")) {
                type = Boolean.TYPE;
            }
            if(prop.attrEquals("type","int")) {
                type = Integer.TYPE;
            }

            if(type == null) {
                type = Class.forName(prop.attr("type"));
            }
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
            property.setCompound(prop.attrEquals("compound","true"));
            if(prop.hasAttr("masterprop")) {
                property.setMasterProperty(prop.attr("masterprop"));
            }
            node.addProperty(property);
        }
        u.p("restored node " + node.getName());
        for(Property p : node.getSortedProperties()) {
            u.p("   " + p.getName() + " " + p.getType());
        }


        node.setDrawDelegate(drawMap.get(node.getName()));
        for(Elem echild : xml.xpath("children/node")) {
            node.add(processNode(echild, ids, drawMap));
        }
        return node;
    }

}
