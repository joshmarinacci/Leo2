package com.joshondesign.treegui.modes.aminojava;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/16/13
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Alarm {
    private String label;
    private int time;
    private boolean repeat;

    public Alarm() {
        setLabel("Label");
        setTime(12);
        setRepeat(true);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isRepeat() {
        return repeat;
    }
}
