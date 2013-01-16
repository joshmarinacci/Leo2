package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import java.awt.geom.Point2D;
import org.joshy.gfx.node.Bounds;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/4/13
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class MathUtils {
    public static Bounds unionBounds(TreeNode<SketchNode> root) {
        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = Double.MIN_VALUE;
        double maxy = Double.MIN_VALUE;
        for(SketchNode child : root.children()) {
            Bounds bounds = child.getInputBounds();
            bounds = transform(bounds,child.getTranslateX(),child.getTranslateY());
            minx = Math.min(minx, bounds.getX());
            miny = Math.min(miny, bounds.getY());
            maxx = Math.max(maxx, bounds.getX2());
            maxy = Math.max(maxy, bounds.getY2());
        }
        return new Bounds(minx,miny,maxx-minx,maxy-miny);
    }
    public static Bounds transform(Bounds b, double x, double y) {
        return new Bounds(
                b.getX()+x,
                b.getY()+y,
                b.getWidth(),b.getHeight()
        );
    }

    public static Point2D transform(Point2D point, double x, double y) {
        return new Point2D.Double(point.getX()+x, point.getY()+y);
    }

    public static Bounds transform(Bounds b, Point2D pt) {
        return MathUtils.transform(b,pt.getX(),pt.getY());
    }
}
