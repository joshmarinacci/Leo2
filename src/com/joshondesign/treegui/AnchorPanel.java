package com.joshondesign.treegui;

import com.joshondesign.xml.Elem;
import java.util.HashMap;
import java.util.Map;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.Panel;

public class AnchorPanel extends Panel {

    public Elem element;
    private Map<Control, AnchorSettings> anchors = new HashMap<Control, AnchorSettings>();

    @Override
    public void doLayout() {
        for(Control control : controlChildren()) {
            AnchorSettings an = anchors.get(control);
            if(an.leftSet && an.rightSet) {
                control.setTranslateX(an.left);
                control.setWidth(getWidth()-an.left-an.right);
            }
            if(!an.leftSet && an.rightSet) {
                control.setTranslateX(getWidth()-control.getPrefWidth());
            }
            if(an.topSet && an.bottomSet) {
                control.setTranslateY(an.top);
                control.setHeight(getHeight()-an.top-an.bottom);
            }
        }
        super.doLayout();
    }

    public void add(Control childControl, AnchorSettings anchorSettings) {
        this.add(childControl);
        this.anchors.put(childControl,anchorSettings);
    }

    public static class AnchorSettings {

        private double left;
        private boolean leftSet;
        private double right;
        private boolean rightSet;
        private double top;
        private boolean topSet;
        private double bottom;
        private boolean bottomSet;

        public AnchorSettings(Elem child) {
            if(child.hasAttr("left")) {
                left = Double.parseDouble(child.attr("left"));
                leftSet = true;
            }
            if(child.hasAttr("right")) {
                right = Double.parseDouble(child.attr("right"));
                rightSet = true;
            }
            if(child.hasAttr("top")) {
                top = Double.parseDouble(child.attr("top"));
                topSet = true;
            }
            if(child.hasAttr("bottom")) {
                bottom = Double.parseDouble(child.attr("bottom"));
                bottomSet = true;
            }
        }
    }
}
