package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.model.Metadata;
import org.joshy.gfx.draw.GFX;

@Metadata(visual = true, exportClass = "CheckButton", resize= Resize.None)
public class CheckButton extends ToggleButton {
    public CheckButton() {
        text = "checkbutton";
        setWidth(100);
        setHeight(30);
    }

    @Override
    public void draw(GFX g) {
    }
}
