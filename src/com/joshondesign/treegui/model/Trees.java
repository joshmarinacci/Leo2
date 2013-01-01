package com.joshondesign.treegui.model;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 12/31/12
 * Time: 9:50 AM
 * To change this template use File | Settings | File Templates.
 */
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
