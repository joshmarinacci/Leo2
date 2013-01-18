package com.joshondesign.treegui.modes.aminojava;

import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.util.ArrayListModel;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/16/13
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlarmList {

    private ArrayListModel<Alarm> results = new ArrayListModel<Alarm>();
    public AlarmList() {
        results.add(new Alarm().setLabel("l1").setTime(5));
        results.add(new Alarm().setLabel("l2").setTime(10));
        results.add(new Alarm().setLabel("l3").setTime(20));
    }
    public ListModel<Alarm> getData() {
        return results;
    }

    public void setData(ListModel<Alarm> results) {
        //this.results = results;
    }
}
