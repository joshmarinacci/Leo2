package com.joshondesign.treegui.leo2;

import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.control.ListView;

public class SymbolRenderer implements ListView.ItemRenderer<TreeNode> {
    public void draw(GFX gfx, ListView listView, TreeNode treeNode, int index, double x, double y, double w, double h) {
        if(treeNode == null) return;
        SketchNode node = (SketchNode) treeNode;
        gfx.translate(x, y);
        node.draw(gfx);
        gfx.setPaint(FlatColor.WHITE);

        gfx.fillRect(0, h-20, w, 20);
        gfx.setPaint(FlatColor.BLACK);
        if(node instanceof DynamicNode) {
            gfx.drawText(((DynamicNode) node).getName(), Font.DEFAULT, 5, h-4);
        } else {
            gfx.drawText(node.getId(), Font.DEFAULT,5,h-4);
        }
        gfx.translate(-x,-y);
        if(listView.getSelectedIndex() == index) {
            gfx.setPaint(new FlatColor(1.0,0,0,0.3));
            gfx.fillRect(x,y,w,h);
        }
        gfx.setPaint(FlatColor.BLACK);
        gfx.drawRect(x,y,w,h);
    }
}
