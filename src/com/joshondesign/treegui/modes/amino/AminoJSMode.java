package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.Mode;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/15/13
 * Time: 7:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class AminoJSMode extends Mode {
    public AminoJSMode() {
        setId("com.joshondesign.modes.aminojs");
        TreeNode<JAction> actions = new TreeNode<JAction>();
        actions.add(new JAction() {
            @Override
            public void execute() {
            }
            @Override
            public String getShortName() {
                return "Save XML";
            }
        });
        add(actions);


        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        Rect rect = new Rect();
        rect.setId("Rect");
        symbols.add(rect);

        PushButton button = new PushButton();
        button.setId("PushButton");
        symbols.add(button);

        ToggleButton togg = new ToggleButton();
        togg.setId("toggle");
        symbols.add(togg);

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

        com.joshondesign.treegui.modes.amino.ListView lv = new com.joshondesign.treegui.modes.amino.ListView();
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
}
