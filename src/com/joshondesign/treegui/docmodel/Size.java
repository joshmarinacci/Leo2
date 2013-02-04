package com.joshondesign.treegui.docmodel;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/4/13
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class Size {
    private final double width;
    private final double height;
    private final Units unit;

    public Size(double width, double height, Units unit) {
        this.width = width;
        this.height = height;
        this.unit = unit;
    }

    public double getWidth(Units targetUnit) {
        return targetUnit.fromPixels(unit.toPixels(width));
    }

    public double getHeight(Units targetUnit) {
        return targetUnit.fromPixels(unit.toPixels(height));
    }
}
