package com.joshondesign.treegui.model;

public class Trees {
    public static int getTotalTreeCount(TreeNode<TreeNode> node) {
        int accum = 0;
        for(TreeNode<TreeNode> n : node.children()) {
            accum += getTotalTreeCount(n);
        }
        accum += 1; //count ourselves
        return accum;
    }
}
