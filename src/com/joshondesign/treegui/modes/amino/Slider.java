package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;

public class Slider extends ResizableRectNode {
    private double minValue;
    private double maxValue;
    private double value;

    public Slider() {
        setWidth(70);
        setHeight(20);
        setMinValue(0);
        setMaxValue(100);
        setValue(50);
        setConstraint(ResizeConstraint.HorizontalOnly);
    }



    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.GRAY);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setPaint(FlatColor.BLACK);
        g.fillRect(0,0,getHeight(),getHeight());
    }
    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new Slider();
        }
        return super.duplicate(node);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
