package com.joshondesign.treegui.tools;

import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.docmodel.Units;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;

public class SnappingManager {
    private List<VSnapper> vSnappers = new ArrayList<VSnapper>();
    private List<HSnapper> hSnappers = new ArrayList<HSnapper>();

    private Point2D.Double pt(double x, double y) {
        return new Point2D.Double(x,y);
    }

    public List<VSnapper> getVSnappers() {
        return vSnappers;
    }
    public List<HSnapper> getHSnappers() {
        return hSnappers;
    }

    public static interface Snapper {
        public boolean canSnap(Point2D pt, SketchDocument doc, SketchNode node);
        public double snap(Point2D pt, SketchDocument doc, SketchNode node);
        public void drawSnap(GFX gfx, double pt);
    }

    public abstract class VSnapper implements Snapper {
        abstract double getPoint(SketchDocument doc, SketchNode node);
        public void drawSnap(GFX gfx, double pt) {
            gfx.setPaint(FlatColor.GREEN);
            gfx.drawLine(pt, -300, pt, 600);
        }
    }


    private static final double THRESH = 5;
    public SnappingManager() {
        //threshold value for snapping

        abstract class LeftSnapper extends VSnapper {
            public boolean canSnap(Point2D pt, SketchDocument doc, SketchNode node) {
                return Math.abs(pt.getX() - getPoint(doc, node)) < THRESH;
            }

            public double snap(Point2D pt, SketchDocument doc, SketchNode node) {
                node.setTranslateX(getPoint(doc, node));
                return getPoint(doc, node);
            }
        }

        abstract class RightSnapper extends VSnapper {
            public boolean canSnap(Point2D pt, SketchDocument doc, SketchNode node) {
                double x2 = pt.getX() + node.getInputBounds().getX2();
                double w = getPoint(doc, node);
                return Math.abs(x2 - w) < THRESH;
            }

            public double snap(Point2D pt, SketchDocument doc, SketchNode node) {
                double x = pt.getX();
                double x2 = node.getInputBounds().getX2();
                double w = getPoint(doc, node);
                if(Math.abs(x + x2 - w) < THRESH) {
                    node.setTranslateX(w - x2);
                }
                return w;
            }
        }

        abstract class TopSnapper extends HSnapper {
            public boolean canSnap(Point2D pt, SketchDocument doc, SketchNode node) {
                return Math.abs(pt.getY() - getPoint(doc, node)) < THRESH;
            }
            public double snap(Point2D pt, SketchDocument doc, SketchNode node) {
                double v = getPoint(doc, node);
                node.setTranslateY(v);
                return v;
            }
        }

        abstract class BottomSnapper extends HSnapper {
            public boolean canSnap(Point2D pt, SketchDocument doc, SketchNode node) {
                double y2 = node.getInputBounds().getY2();
                return Math.abs(pt.getY() + y2 - getPoint(doc, node)) < THRESH;
            }
            public double snap(Point2D pt, SketchDocument doc, SketchNode node) {
                double y2 = node.getInputBounds().getY2();
                double v = getPoint(doc, node);
                node.setTranslateY(v - y2);
                return v;
            }
        }


        final double DOC_MARGIN = 30;
        final double PARENT_MARGIN = 15;



        // =========== document bounds

        //snap to the left doc bounds
        vSnappers.add(new LeftSnapper() {
            public double getPoint(SketchDocument doc, SketchNode node) {
                return 0;
            }
        });
        //left doc margin
        vSnappers.add(new LeftSnapper() {
            public double getPoint(SketchDocument doc, SketchNode node) {
                return DOC_MARGIN;
            }
        });

        //right doc bounds
        vSnappers.add(new RightSnapper() {
            public double getPoint(SketchDocument doc, SketchNode node) {
                return doc.getMasterSize().getWidth(Units.Pixels);
            }
        });
        //right doc margin
        vSnappers.add(new RightSnapper() {
            public double getPoint(SketchDocument doc, SketchNode node) {
                return doc.getMasterSize().getWidth(Units.Pixels) - DOC_MARGIN;
            }
        });





        // ============== parent bounds

        //left inner margin of parent
        vSnappers.add(new LeftSnapper() {
            @Override
            double getPoint(SketchDocument doc, SketchNode node) {
                if(!(node.getParent() instanceof SketchNode)) return 0;
                return PARENT_MARGIN;
            }
        });

        //top inner margin of parent
        hSnappers.add(new TopSnapper() {
            @Override
            double getPoint(SketchDocument doc, SketchNode node) {
                if(!(node.getParent() instanceof SketchNode)) return 0;
                return PARENT_MARGIN;
            }
        });

        //right edge of parent
        vSnappers.add(new RightSnapper() {
            @Override
            double getPoint(SketchDocument doc, SketchNode node) {
                if(!(node.getParent() instanceof SketchNode)) return 0;
                return ((SketchNode) node.getParent()).getWidth();
            }
        });

        //inner right margin of parent
        vSnappers.add(new RightSnapper() {
            @Override
            double getPoint(SketchDocument doc, SketchNode node) {
                if(!(node.getParent() instanceof SketchNode)) return 0;
                return ((SketchNode) node.getParent()).getWidth() - PARENT_MARGIN;
            }
        });

        //look for other nodes with similar X positions
        vSnappers.add(new VSnapper() {
            double getPoint(SketchDocument doc, SketchNode node) {
                return 0;
            }

            public boolean canSnap(Point2D pt, SketchDocument doc, SketchNode node) {
                if(node.getParent() instanceof Layer) {
                    for(SketchNode nd : ((Layer) node.getParent()).children()) {
                        if(nd == node) continue;
                        double x = nd.getTranslateX();
                        if(Math.abs(x-pt.getX()) < THRESH) {
                            return true;
                        }
                    }
                }
                return false;
            }

            public double snap(Point2D pt, SketchDocument doc, SketchNode node) {
                if(node.getParent() instanceof Layer) {
                    for(SketchNode nd : ((Layer) node.getParent()).children()) {
                        if(nd == node) continue;
                        double x = nd.getTranslateX();
                        if(Math.abs(x-pt.getX()) < THRESH) {
                            node.setTranslateX(x);
                            return x;
                        }
                    }
                }
                return 0;
            }
        });

        //look for other nodes with similar Y positions
        hSnappers.add(new HSnapper() {
            double getPoint(SketchDocument doc, SketchNode node) {
                return 0;
            }

            public boolean canSnap(Point2D pt, SketchDocument doc, SketchNode node) {
                if(node.getParent() instanceof Layer) {
                    for(SketchNode nd : ((Layer) node.getParent()).children()) {
                        if(nd == node) continue;
                        double y = nd.getTranslateY();
                        if(Math.abs(y-pt.getY()) < THRESH) {
                            return true;
                        }
                    }
                }
                return false;
            }

            public double snap(Point2D pt, SketchDocument doc, SketchNode node) {
                if(node.getParent() instanceof Layer) {
                    for(SketchNode nd : ((Layer) node.getParent()).children()) {
                        if(nd == node) continue;
                        double y = nd.getTranslateY();
                        if(Math.abs(y-pt.getY()) < THRESH) {
                            node.setTranslateY(y);
                            return y;
                        }
                    }
                }
                return 0;
            }
        });



        //center of doc bounds vertically
        vSnappers.add(new VSnapper() {
            @Override
            double getPoint(SketchDocument doc, SketchNode node) {
                return doc.getMasterSize().getWidth(Units.Pixels)/2;
            }

            public boolean canSnap(Point2D pt, SketchDocument doc, SketchNode node) {
                double x2 = pt.getX() + node.getInputBounds().getCenterX();
                double w = getPoint(doc, node);
                return Math.abs(x2 - w) < THRESH;
            }

            public double snap(Point2D pt, SketchDocument doc, SketchNode node) {
                double x = pt.getX();
                double x2 = node.getInputBounds().getCenterX();
                double w = getPoint(doc, node);
                if(Math.abs(x + x2 - w) < THRESH) {
                    node.setTranslateX(w - x2);
                }
                return w;
            }
        });




        //top bounds
        hSnappers.add(buildit(
                new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) {
                        return node.getInputBounds().getY();
                    }
                },
                new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) {
                        return 0;
                    }
                }));
        //top margin
        hSnappers.add(buildit(
                new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) {
                        return node.getInputBounds().getY();
                    }
                },
                new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) {
                        return DOC_MARGIN;
                    }
                }));
        //doc center horizontally
        hSnappers.add(buildit(
                new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) { return node.getInputBounds().getCenterY(); }
                },
                new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) { return doc.getMasterSize().getHeight(Units.Pixels)/2;
                    }
                }));
        //bottom doc bounds
        hSnappers.add(buildit(
                new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) {
                        return node.getInputBounds().getY2();
                    }
                },
                new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) {
                        return doc.getMasterSize().getHeight(Units.Pixels);
                    }
                }));
        //bottom doc margin
        hSnappers.add(buildit(
                new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) {
                        return node.getInputBounds().getY2();
                    }
                },
                new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) { return doc.getMasterSize().getHeight(Units.Pixels)-DOC_MARGIN;
                    }
                }));


        //bottom edge of parent;
        hSnappers.add(new BottomSnapper() {
            @Override
            double getPoint(SketchDocument doc, SketchNode node) {
                if(!(node.getParent() instanceof SketchNode)) return 0;
                return ((SketchNode) node.getParent()).getHeight();
            }
        });
        //bottom margin of parent;
        hSnappers.add(new BottomSnapper() {
            @Override
            double getPoint(SketchDocument doc, SketchNode node) {
                if(!(node.getParent() instanceof SketchNode)) return 0;
                return ((SketchNode) node.getParent()).getHeight() - PARENT_MARGIN;
            }
        });



    }

    abstract static class ZHSnapper extends HSnapper {
        public abstract double getPart(SketchDocument doc, SketchNode node);

        public boolean canSnap(Point2D pt, SketchDocument doc, SketchNode node) {
            double v1 = pt.getY();
            double vx = getPart(doc, node);
            double v2 = getPoint(doc, node);
            return Math.abs(v1+vx-v2) < THRESH;
        }

        public double snap(Point2D pt, SketchDocument doc, SketchNode node) {
            double v1 = pt.getY();
            double vx = getPart(doc, node);
            double v2 = getPoint(doc, node);
            if(Math.abs(v1+vx-v2) < THRESH) {
                node.setTranslateY(v2-vx);
            }
            return v2;
        }
    }
    private static interface Takeit {
        public double take(SketchDocument doc, SketchNode node);
    }
    private static HSnapper buildit(final Takeit t1, final Takeit t2) {
        return new ZHSnapper() {
            @Override
            public double getPart(SketchDocument doc, SketchNode node) {
                return t1.take(doc, node);
            }
            @Override
            double getPoint(SketchDocument doc, SketchNode node) {
                return t2.take(doc, node);
            }
        };
    }


    public abstract static class HSnapper implements Snapper {
        abstract double getPoint(SketchDocument doc, SketchNode node);
        public void drawSnap(GFX gfx, double pt) {
            gfx.setPaint(FlatColor.GREEN);
            gfx.drawLine(-300, pt, 1000, pt);
        }
    }
}

