package com.joshondesign.treegui;

import com.joshondesign.xml.Elem;
import java.util.HashMap;
import java.util.Map;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.util.u;

public class AnchorPanel extends Panel {

    public Elem element;
    private Map<Control, AnchorSettings> anchors = new HashMap<Control, AnchorSettings>();

    public AnchorPanel() {
    }

    public boolean DEBUG = false;

    @Override
    public void doLayout() {
        for(Control control : controlChildren()) {
            AnchorSettings an = anchors.get(control);
            if(DEBUG) u.p("anchor = " + an);
            if(an == null) {
                continue;
            }
            if(an.leftSet && an.rightSet) {
                control.setTranslateX(an.left);
                control.setWidth(getWidth()-an.left-an.right);
            }
            if(!an.leftSet && an.rightSet) {
                u.p("setting from the right edge");
                control.setTranslateX(getWidth()-control.getPrefWidth());
            }
            if(an.topSet && an.bottomSet) {
                control.setTranslateY(an.top);
                control.setHeight(getHeight()-an.top-an.bottom);
            }
            if(DEBUG) {
                u.p("fonal control = " + control.getTranslateX() + " " + control.getTranslateY() + " " + control.getWidth() + " " + control.getHeight());
                u.p("pref = " + control.getPrefWidth() + " " + control.getPrefHeight());
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

        public AnchorSettings(double left, boolean leftSet, double right, boolean rightSet, double top, boolean topSet, double bottom, boolean bottomSet) {
            this.left = left;
            this.leftSet = leftSet;
            this.right = right;
            this.rightSet = rightSet;
            this.top = top;
            this.topSet = topSet;
            this.bottom = bottom;
            this.bottomSet = bottomSet;
        }

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

        @Override
        public String toString() {
            return "AnchorSettings{" +
                    "left=" + left +
                    ", leftSet=" + leftSet +
                    ", right=" + right +
                    ", rightSet=" + rightSet +
                    ", top=" + top +
                    ", topSet=" + topSet +
                    ", bottom=" + bottom +
                    ", bottomSet=" + bottomSet +
                    '}';
        }
    }
}
