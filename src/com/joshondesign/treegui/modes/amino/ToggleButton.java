package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.docmodel.SketchNode;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/8/13
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class ToggleButton extends PushButton {
    public ToggleButton() {
        setText("toggle button");
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new ToggleButton();
        }
        return super.duplicate(node);
    }
}
