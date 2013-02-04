package com.joshondesign.treegui.docmodel;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/4/13
 * Time: 11:31 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Units {
    Pixels(1.0),
    Inches(72.0),
    Millimeters(2),
    Centimeters(20),;


    private final double ratio;
    private Units(double v) {
        ratio = v;
    }

    public double toPixels(double value) {
        return value/ratio;
    }

    public double fromPixels(double value) {
        return value * ratio;
    }
}
