package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.Metadata;
import com.joshondesign.treegui.modes.aminojava.Prop;
import java.awt.geom.Point2D;
import java.util.List;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

@Metadata(visual = false, exportClass = "FlickrQuery")
public class FlickrQuery extends SketchNode {
    private boolean active;

    public FlickrQuery() {
        setActive(false);
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.YELLOW);
        g.fillRoundRect(0, 0, 80, 80, 10, 10);
        g.setPaint(FlatColor.BLACK);
        g.drawRoundRect(0, 0, 80, 80, 10, 10);
        g.drawText("Flickr Query", Font.DEFAULT, 10, 15);
    }

    @Override
    public boolean contains(Point2D pt) {
        Point2D.Double pt2 = new Point2D.Double(pt.getX() - getTranslateX(), pt.getY() - getTranslateY());
        return getInputBounds().contains(pt2);
    }

    @Override
    public boolean isVisual() {
        return false;
    }

    @Override
    public Bounds getInputBounds() {
        return new Bounds(0,0,80,80);
    }

    @Prop
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    private String query = "";

    @Prop(exported = false, visible = false)
    public List<Object> getResults() {
        return results;
    }

    public void setResults(List<Object> results) {
        this.results = results;
    }

    List<Object> results;

    @Prop(visible = false, exported = false)
    public ActionProp getExecute() {
        return new ActionProp();
    }


    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null)  {
            node = new FlickrQuery();
        }
        return super.duplicate(node);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Prop
    public boolean isActive() {
        return active;
    }
}
