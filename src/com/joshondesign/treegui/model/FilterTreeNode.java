package com.joshondesign.treegui.model;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 12/31/12
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class FilterTreeNode extends TreeNode {
    private Filter filter;

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public static interface Filter {
        public boolean include(TreeNode node);
    }
}
