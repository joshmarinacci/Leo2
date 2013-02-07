package com.joshondesign.treegui.modes.bootstrap;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.DynamicNodeMode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import static com.joshondesign.treegui.modes.bootstrap.Defs.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joshy.gfx.node.control.Menu;
import org.joshy.gfx.util.u;

public class BootstrapMode extends DynamicNodeMode {
    public static Map<String, DynamicNode.DrawDelegate> drawMap = new HashMap<String, DynamicNode.DrawDelegate>();

    public BootstrapMode() {
        setId("com.joshondesign.modes.bootstrap");
        add(new TreeNode<JAction>());

        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        symbols.setId("symbols");
        add(symbols);

        drawMap.put("VisualBase", VisualBaseDelegate);
        DynamicNode visualBase = parse(new VisualBase(), VisualBaseDelegate, null);
        for(Property prop : visualBase.getProperties()) {
            u.p("  " + prop.getName() + " " + prop.getType().getName());
        }

        drawMap.put("PushButton", PushButtonDelegate);
        symbols.add(parse(new PushButton(), PushButtonDelegate, visualBase));
        u.p("pushbutton = " + findSymbol("PushButton"));
        DynamicNode pb = findSymbol("PushButton");
        for(Property prop : pb.getProperties()) {
            u.p("  " + prop.getName() + " " + prop.getType().getName());
        }
        pb.getProperty("width").setDoubleValue(100);
        pb.getProperty("height").setDoubleValue(30);

    }

    @Override
    public String getName() {
        return "Bootstrap";
    }

    @Override
    public SketchDocument createEmptyDoc() {
        SketchDocument doc = new SketchDocument();
        doc.setModeId(this.getId());
        Layer layer = new Layer();
        layer.add(findSymbol("PushButton").duplicate(null));
        Page page = new Page();
        page.add(layer);
        doc.add(page);
        return doc;
    }

    @Override
    public void modifyFileMenu(Menu fileMenu, SketchDocument doc) {
        fileMenu.addItem("Test HTML", "R", new BootstrapHTMLExport(doc,true));
    }

    @Override
    public Map<String, DynamicNode.DrawDelegate> getDrawMap() {
        return drawMap;
    }

    @Override
    public void filesDropped(List<File> files, Canvas canvas) {
    }
}
