package com.joshondesign.treegui.model;

public class FilterTreeNode<C extends TreeNode> extends TreeNode {
    private Filter filter;
    private TreeNode<C> realData;

    public FilterTreeNode(TreeNode<C> data) {
        this.realData = data;
        this.realData.addListener(new TreeListener() {
            public void added(Object node) {
            }
            public void removed(Object node) {
            }
            public void modified(Object node) {
            }
            public void selfModified(TreeNode self) {
            }
        });
        applyFilter();
    }
    private void applyFilter() {
        this.clear();
        for(C child : realData.children()) {
            if(filter == null) {
                this.add(child);
                continue;
            }
            if(filter.include(child)) {
                this.add(child);
            }
        }
        markModified(null);
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
        applyFilter();
    }

    public static interface Filter<C extends TreeNode> {
        public boolean include(C node);
    }
}
