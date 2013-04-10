package com.joshondesign.treegui.actions;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.Leo2;
import com.joshondesign.treegui.Mode;
import com.joshondesign.treegui.docmodel.*;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import com.joshondesign.treegui.modes.aminolang.DynamicGroup;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.node.control.ListView;
import org.joshy.gfx.util.u;

public class XMLImport {
    public static SketchDocument read(File file) throws Exception {
        Doc xml = XMLParser.parse(file);
        SketchDocument doc = new SketchDocument();
        doc.clear();
        processDocument(xml.root(), doc);
        doc.setFile(file);
        return doc;
    }

    public static void processDocument(Elem root, SketchDocument doc) throws XPathExpressionException, ClassNotFoundException {
        Map<String, SketchNode> ids = new HashMap<String, SketchNode>();
        Mode mode = Leo2.modeMap.get(root.attr("mode"));
        u.p("attr = " + root.attr("mode") + mode);
        doc.setModeId(mode.getId());
        for(Elem epage : root.xpath("page")) {
            Page page = processPage(epage,doc, mode, ids);
            doc.add(page);
        }
        //bind them together
        doc.getBindings().clear();
        for(Elem binding : root.xpath("bindings/binding")) {
            try {
                Binding b = processBinding(binding, ids);
                doc.getBindings().add(b);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Page processPage(Elem root, SketchDocument doc, Mode mode, Map<String, SketchNode> ids) throws XPathExpressionException, ClassNotFoundException {
        Page page = new Page();
        Layer layer = new Layer();
        page.add(layer);

        for(Elem vis : root.xpath("layer/children/*")) {
            if(vis.name().equals("node")) {
                DynamicNode node = processNode(vis,ids, mode.getDrawMap());
                layer.add(node);
            }
            if(vis.name().equals("group")) {
                SketchNode group = processGroup(vis, ids, mode.getDrawMap());
                layer.add(group);
            }
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

    private static SketchNode processGroup(Elem xml, Map<String, SketchNode> ids, Map<String, DynamicNode.DrawDelegate> drawMap) throws XPathExpressionException, ClassNotFoundException {
        u.p("procssing group");
        Group node = new Group();
        node.setId(xml.attr("id"));
        node.setTranslateX(Double.parseDouble(xml.attr("translateX")));
        node.setTranslateY(Double.parseDouble(xml.attr("translateY")));
        for(Elem echild : xml.xpath("children/node")) {
            node.add(processNode(echild, ids, drawMap));
        }
        return node;
    }
    private static DynamicNode processNode(Elem xml, Map<String, SketchNode> ids, Map<String, DynamicNode.DrawDelegate> drawMap) throws XPathExpressionException, ClassNotFoundException {
        u.p("processing xml element: '" + xml.name()+"'");
        DynamicNode node = new DynamicNode();
        if(xml.attrEquals("class","com.joshondesign.treegui.modes.aminolang.DynamicGroup")) {
            node = new DynamicGroup();
        }
        node.setName(xml.attr("name"));
        node.addProperty(new Property("class", String.class, xml.attr("class")));
        node.setVisual(xml.attrEquals("visual", "true"));
        node.setContainer(xml.attrEquals("container", "true"));
        if(xml.hasAttr("resize")) {
            node.setResize(Resize.valueOf(xml.attr("resize")));
        }
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
            if(prop.attrEquals("type","double")) {
                type = Double.TYPE;
            }

            if(type == null) {
                type = Class.forName(prop.attr("type"));
            }
            String sval = prop.attr("value");
            if(type == Double.class || type == Double.TYPE) {
                val = (Double)Double.parseDouble(sval);
            }
            if(type == Integer.class || type == Integer.TYPE) {
                val = (Integer)Integer.parseInt(sval);
            }
            if(type == Boolean.class || type == Boolean.TYPE) {
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

            if(type == ListModel.class) {
                String[] strings = sval.split(",");
                val = ListView.createModel(strings);
                u.p("converting string : " + sval + " to a list model");
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

            if(val == null) {
                u.p("WARNING: couldn't restore value for property: " + name);
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


        if(drawMap.get(node.getName()) == null) {
            u.p("WARNING:  couldn't find draw delegate for " + node.getName());
        }
        node.setDrawDelegate(drawMap.get(node.getName()));
        for(Elem echild : xml.xpath("children/node")) {
            node.add(processNode(echild, ids, drawMap));
        }
        return node;
    }

}
