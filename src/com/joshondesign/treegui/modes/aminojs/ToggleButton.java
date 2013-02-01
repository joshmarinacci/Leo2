package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;

@Metadata(resize=Resize.None)
public class ToggleButton extends PushButton {
    @Prop
    public Boolean selected = false;

    public ToggleButton() {
        text = "togglebutton";
        setWidth(90);
    }
}
