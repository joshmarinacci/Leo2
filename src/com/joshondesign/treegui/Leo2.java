package com.joshondesign.treegui;

import org.joshy.gfx.Core;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/13/13
 * Time: 6:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Leo2 {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new Runnable() {
            public void run() {
                init();
            }
        });
    }

    private static void init() {

    }
}
