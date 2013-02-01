package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;

@Metadata(visual = false, name = "Alarm")
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
