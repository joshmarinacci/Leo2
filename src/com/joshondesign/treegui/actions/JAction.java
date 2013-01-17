package com.joshondesign.treegui.actions;

import com.joshondesign.treegui.model.TreeNode;

public abstract class JAction extends TreeNode {
    public abstract void execute();
    public abstract String getShortName();
}
