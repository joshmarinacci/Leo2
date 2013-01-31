package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.Prop;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/8/13
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class ToggleButton extends PushButton {
    @Prop
    public Boolean selected;

    public ToggleButton() {
        setText("toggle button");
        setWidth(90);
        setSelected(false);
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new ToggleButton();
        }
        return super.duplicate(node);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
