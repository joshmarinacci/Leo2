package com.joshondesign.treegui.leo2;

import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.control.ListView;

public class SymbolRenderer implements ListView.ItemRenderer<TreeNode> {
    public void draw(GFX gfx, ListView listView, TreeNode treeNode, int index, double x, double y, double w, double h) {
        gfx.setPaint(new FlatColor("#303030"));
        gfx.fillRect(x, y, w, h);

        gfx.setPaint(FlatColor.fromRGBInts(100,100,100));
        gfx.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 10, 10);

        if(treeNode == null) return;

        //node itself
        SketchNode node = (SketchNode) treeNode;
        gfx.translate( x+5, y+22);
        Bounds oldClip = gfx.getClipRect();
        gfx.setClipRect(new Bounds(0,0,w-5,h-22-2));
        node.draw(gfx);
        gfx.setClipRect(oldClip);
        gfx.translate(-x-5,-y-22);



        //text overlay
        gfx.setPaint(new FlatColor("#cccccc"));
        gfx.fillRoundRect(x + 2, y + 2, w - 4, 18, 10, 10);
        gfx.setPaint(FlatColor.BLACK);
        if(node instanceof DynamicNode) {
            gfx.drawText(((DynamicNode) node).getName(), Font.DEFAULT, x+5, y+15);
        } else {
            gfx.drawText(node.getId(), Font.DEFAULT,x+5,y+15);
        }


        //selection overlay
        if(listView.getSelectedIndex() == index) {
            gfx.setPaint(new FlatColor(1.0,0,0,0.3));
            gfx.fillRect(x,y,w,h);
        }

        //border
        gfx.setPaint(FlatColor.BLACK);
        //gfx.drawRect(x,y,w,h);
    }
}
