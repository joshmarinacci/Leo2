package com.joshondesign.treegui.modes.aminojava;

import org.joshy.gfx.node.control.ListModel;

public class StringListModel implements ListModel {
    private final String[] data;

    public StringListModel() {
        this.data =new String[] {"foo","bar","baz"};
    }

    public Object get(int i) {
        return data[i];
    }

    public int size() {
        return data.length;
    }
}
