package com.joshondesign.treegui.modes.aminolang;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.DynamicNodeMode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import static com.joshondesign.treegui.modes.aminolang.Defs.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joshy.gfx.node.control.Menu;

public class AminoLangMode extends DynamicNodeMode {

    public static Map<String, DynamicNode.DrawDelegate> drawMap = new HashMap<String, DynamicNode.DrawDelegate>();

    public AminoLangMode() {
        setId("com.joshondesign.modes.aminolang");
        add(new TreeNode<JAction>());

        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        symbols.setId("symbols");
        add(symbols);

        drawMap.put("VisualBase", VisualBaseDelegate);
        DynamicNode visualBase = parse(new Defs.VisualBase(), VisualBaseDelegate, null);
        /*
        for(Property prop : visualBase.getProperties()) {
            u.p("  " + prop.getName() + " " + prop.getType().getName() + " " + prop.isVisible() + " blah");
        }
        */
        visualBase.getProperty("width").setExportName("w");
        visualBase.getProperty("height").setExportName("h");
        visualBase.getProperty("translateX").setExportName("tx");
        visualBase.getProperty("translateY").setExportName("ty");

        drawMap.put("PushButton", PushButtonDelegate);
        symbols.add(parse(new Defs.PushButton(), PushButtonDelegate, visualBase));
        DynamicNode pb = findSymbol("PushButton");
        pb.getProperty("width").setDoubleValue(100);
        pb.getProperty("height").setDoubleValue(30);

        drawMap.put("ToggleButton", ToggleButtonDelegate);
        symbols.add(parse(new Defs.ToggleButton(), ToggleButtonDelegate, visualBase));
        DynamicNode tb = findSymbol("ToggleButton");
        tb.getProperty("width").setDoubleValue(100);
        tb.getProperty("height").setDoubleValue(30);

        drawMap.put("Slider", SliderDelegate);
        symbols.add(parse(new Defs.Slider(), SliderDelegate, visualBase));
        findSymbol("Slider").getProperty("height").setDoubleValue(25);

        drawMap.put("Label", LabelDelegate);
        symbols.add(parse(new Defs.Label(), LabelDelegate, visualBase));


        drawMap.put("Rect",RectDelegate);
        symbols.add(parse(new Defs.Rect(), RectDelegate, visualBase));

        drawMap.put("ListView",Defs.ListViewDelegate);
        symbols.add(parse(new Defs.ListView(), ListViewDelegate, visualBase));

    }

    @Override
    public String getName() {
        return "Amino Lang";
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
        fileMenu.addItem("Test HTML", "T", new AminoLangJSONExport(doc,true));
    }

    @Override
    public Map<String, DynamicNode.DrawDelegate> getDrawMap() {
        return drawMap;
    }

    @Override
    public void filesDropped(List<File> files, Canvas canvas) {
    }
}
