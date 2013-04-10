package com.joshondesign.treegui.tools;

import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.layout.Panel;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 4/10/13
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusBar extends Panel {
    private String text = "Page 1 of 1";

    @Override
    protected void drawSelf(GFX g) {
        super.drawSelf(g);
        g.setPaint(new FlatColor(0.8,0.8,0.8,1.0));
        g.drawText(text, Font.DEFAULT,5,10);
    }

    public void update(SketchDocument doc) {
        Page page = doc.getSelectedPage();
        int n = doc.indexOf(page);
        int s = doc.getSize();
        this.text = "Page " + (n+1) + " of " + s;
    }
}
