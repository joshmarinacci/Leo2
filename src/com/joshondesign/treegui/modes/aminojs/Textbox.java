package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.Metadata;
import com.joshondesign.treegui.modes.aminojava.Prop;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;

@Metadata(visual = true, exportClass = "Textbox")
public class Textbox extends ResizableRectNode {

    @Prop public String text;
    @Prop public Class clazz = Textbox.class;

    public Textbox() {
        setWidth(80);
        setHeight(20);
        setText("a text box");
        setConstraint(ResizeConstraint.HorizontalOnly);
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setPaint(FlatColor.BLACK);
        //g.drawRect(0+10,0+10,getWidth()-10*2,getHeight()-10*2);
        g.drawText(getText(), Font.DEFAULT,5,15);
        g.drawRect(0, 0, getWidth(), getHeight());
    }

    public Textbox setText(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return text;
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new Textbox();
        }
        return super.duplicate(node);
    }
}
