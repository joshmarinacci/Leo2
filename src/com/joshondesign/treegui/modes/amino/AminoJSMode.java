package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.Mode;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.node.control.Menu;

public class AminoJSMode extends Mode {
    public AminoJSMode() {
        setId("com.joshondesign.modes.aminojs");
        TreeNode<JAction> actions = new TreeNode<JAction>();
        add(actions);


        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        Rect rect = new Rect();
        rect.setId("Rect");
        symbols.add(rect);

        PushButton pushButton = new PushButton();
        pushButton.setId("PushButton");
        symbols.add(pushButton);

        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setId("toggle");
        symbols.add(toggleButton);

        CheckButton checkButton = new CheckButton();
        checkButton.setId("check button");
        symbols.add(checkButton);

        com.joshondesign.treegui.modes.amino.Slider slider = new Slider();
        slider.setId("Slider");
        symbols.add(slider);

        Image image = new Image();
        image.setId("image");
        symbols.add(image);

        Textbox tb = new Textbox();
        tb.setId("textbox");
        symbols.add(tb);

        Label label = new Label();
        label.setId("label");
        symbols.add(label);


        //complex controls

        ListView lv = new ListView();
        lv.setId("ListView");
        symbols.add(lv);

        // panels

        PlainPanel plainPanel = new PlainPanel();
        plainPanel.setId("plain panel");
        symbols.add(plainPanel);

        TabPanel tabPanel = new TabPanel();
        tabPanel.setId("tab panel");
        symbols.add(tabPanel);

        StringListModel stringList = new StringListModel();
        stringList.setId("StringList");
        symbols.add(stringList);

        ControlListModel controlList = new ControlListModel();
        controlList.setId("ControList");
        symbols.add(controlList);

        Spinner spinner = new Spinner();
        spinner.setId("Spinner");
        symbols.add(spinner);

        FlickrQuery fq = new FlickrQuery();
        fq.setId("FlickrQuery");
        symbols.add(fq);


        add(symbols);

    }

    @Override
    public String getName() {
        return "Amino JS";
    }

    @Override
    public SketchDocument createEmptyDoc() {
        SketchDocument doc = new SketchDocument();
        Layer layer = new Layer();
        layer.add(new Rect().setFill(FlatColor.GREEN).setWidth(50).setHeight(50));
        Page page = new Page();
        page.add(layer);
        doc.add(page);
        return doc;
    }

    @Override
    public void modifyFileMenu(Menu fileMenu, SketchDocument doc) {
        fileMenu.addItem("Export HTML", new HTMLBindingExport(doc));
    }
}
