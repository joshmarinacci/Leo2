package com.joshondesign.treegui.modes.aminojava;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/16/13
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Alarm {
    @Prop(bindable = true)
    public String label;
    @Prop(bindable = true)
    public int time;
    @Prop(bindable = true)
    public boolean repeat;

    public Alarm() {
        setLabel("Label");
        setTime(12);
        setRepeat(true);
    }

    public Alarm setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Alarm setTime(int time) {
        this.time = time;
        return this;
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
