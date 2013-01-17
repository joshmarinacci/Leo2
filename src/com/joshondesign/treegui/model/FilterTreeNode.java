package com.joshondesign.treegui.model;

public class FilterTreeNode extends TreeNode {
    private Filter filter;

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public static interface Filter {
        public boolean include(TreeNode node);
    }
}
