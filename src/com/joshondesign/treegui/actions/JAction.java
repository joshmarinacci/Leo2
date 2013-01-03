package com.joshondesign.treegui.actions;

import com.joshondesign.treegui.model.TreeNode;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/1/13
 * Time: 9:08 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class JAction extends TreeNode {
    public abstract void execute();
    public abstract String getShortName();
}
