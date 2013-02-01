package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.util.ArrayListModel;

@Metadata(name = "AlarmList", visual = false, resize = Resize.None,
        exportClass = "com.joshondesign.treegui.modes.aminojava.AlarmList")
public class AlarmList {

    private ArrayListModel<Alarm> results = new ArrayListModel<Alarm>();
    public AlarmList() {
        results.add(new Alarm().setLabel("l1").setTime(5));
        results.add(new Alarm().setLabel("l2").setTime(10));
        results.add(new Alarm().setLabel("l3").setTime(20));
    }
    @Prop(visible = false, bindable = true)
    public ListModel<Alarm> getData() {
        return results;
    }

    public void setData(ListModel<Alarm> results) {
    }
}
