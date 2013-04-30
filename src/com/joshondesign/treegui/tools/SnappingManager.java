package com.joshondesign.treegui.tools;

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



    private static final double THRESH = 5;
    public SnappingManager() {
        final double DOC_MARGIN = 30;
        final double PARENT_MARGIN = 15;

        /*
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
        */
        //look for other nodes with similar Y positions
        /*
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
        */

        Takeit takeX1 = new Takeit() {
            public double take(SketchDocument doc, SketchNode node) {
                return node.getInputBounds().getX();
            }
        };
        Takeit takeXC = new Takeit() {
            public double take(SketchDocument doc, SketchNode node) {
                return node.getInputBounds().getCenterX();
            }
        };
        Takeit takeX2 = new Takeit() {
            public double take(SketchDocument doc, SketchNode node) {
                return node.getInputBounds().getX2();
            }
        };
        final Takeit docW = new Takeit() {
            public double take(SketchDocument doc, SketchNode node) {
                return doc.getMasterSize().getWidth(Units.Pixels);
            }
        };
        final Takeit takePW = new Takeit() {
            public double take(SketchDocument doc, SketchNode node) {
                if(!(node.getParent() instanceof SketchNode)) return 0;
                return ((SketchNode) node.getParent()).getWidth();
            }
        };


        /*
        //left doc bounds
        vSnappers.add(builditV(takeX1, new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) {
                        return 0;
                    }
                }));
        //left doc margin
        vSnappers.add(builditV(takeX1, new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) {
                        return DOC_MARGIN;
                    }
                }));
        //left parent margin
        vSnappers.add(builditV(takeX1, new Takeit() {
            public double take(SketchDocument doc, SketchNode node) {
                return PARENT_MARGIN;
            }
        }));
        //center doc vertical
        vSnappers.add(builditV(takeXC, new Takeit() {
            public double take(SketchDocument doc, SketchNode node) { return docW.take(doc,node)/2;     }
        }));
        //right parent margin
        vSnappers.add(builditV(takeX2, new Takeit() {
            public double take(SketchDocument doc, SketchNode node) { return takePW.take(doc,node) - PARENT_MARGIN;  }
        }));
        //right parent bounds
        vSnappers.add(builditV(takeX2, takePW));
        //doc right margin
        vSnappers.add(builditV(takeX2, new Takeit(){
            public double take(SketchDocument doc, SketchNode node) { return docW.take(doc,node) - DOC_MARGIN; }
        }));
        //doc right edge
        vSnappers.add(builditV(takeX2, docW));





        Takeit takeY1 = new Takeit() {
            public double take(SketchDocument doc, SketchNode node) {
                return node.getInputBounds().getY();
            }
        };
        Takeit takeYC = new Takeit() {
            public double take(SketchDocument doc, SketchNode node) {
                return node.getInputBounds().getCenterY();
            }
        };
        Takeit takeY2 = new Takeit() {
            public double take(SketchDocument doc, SketchNode node) {
                return node.getInputBounds().getY2();
            }
        };
        final Takeit docH = new Takeit() {
            public double take(SketchDocument doc, SketchNode node) {
                return doc.getMasterSize().getHeight(Units.Pixels);
            }
        };
        final Takeit takePH = new Takeit() {
            public double take(SketchDocument doc, SketchNode node) {
                if(!(node.getParent() instanceof SketchNode)) return 0;
                return ((SketchNode) node.getParent()).getHeight();
            }
        };

        //top bounds
        hSnappers.add(builditH(takeY1, new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) {
                        return 0;
                    }
                }));
        //top margin
        hSnappers.add(builditH(takeY1, new Takeit() {
                    public double take(SketchDocument doc, SketchNode node) {
                        return DOC_MARGIN;
                    }
                }));
        //top parent margin
        hSnappers.add(builditH(takeY1, new Takeit() { public double take(SketchDocument doc, SketchNode node) { return PARENT_MARGIN;  } }));
        //doc center horizontally
        hSnappers.add(builditH(takeYC, new Takeit() { public double take(SketchDocument doc, SketchNode node) { return docH.take(doc, node) / 2; }  }));
        //bottom parent margin
        hSnappers.add(builditH(takeY2, new Takeit() { public double take(SketchDocument doc, SketchNode node) { return takePH.take(doc,node) - PARENT_MARGIN; } }));
        //bottom parent edge
        hSnappers.add(builditH(takeY2, takePH));
        //bottom doc margin
        hSnappers.add(builditH(takeY2, new Takeit() {
            public double take(SketchDocument doc, SketchNode node) { return docH.take(doc, node) - DOC_MARGIN;  }
        }));
        //bottom doc bounds
        hSnappers.add(builditH(takeY2, docH));

        */

        vSnappers.add(new VGridSnapper(10));
        hSnappers.add(new HGridSnapper(10));
    }

    static class VGridSnapper extends VSnapper {
        private final double w;
        public VGridSnapper(double w) {
            this.w = w;
        }
        public boolean canSnap(Point2D pt, SketchDocument doc, SketchNode node) {
            if((pt.getX() % w ) < THRESH) return true;
            return false;
        }

        public double snap(Point2D pt, SketchDocument doc, SketchNode node) {
            double out = (int)(pt.getX()/this.w)*this.w;
            node.setTranslateX(out);
            return out;
        }

        @Override
        double getPoint(SketchDocument doc, SketchNode node) {  return 0; }
    }
    static class HGridSnapper extends HSnapper {
        private final double w;
        public HGridSnapper(double w) {
            this.w = w;
        }
        public boolean canSnap(Point2D pt, SketchDocument doc, SketchNode node) {
            if((pt.getY() % w ) < THRESH) return true;
            return false;
        }

        public double snap(Point2D pt, SketchDocument doc, SketchNode node) {
            double out = (int)(pt.getY()/this.w)*this.w;
            node.setTranslateY(out);
            return out;
        }

        @Override
        double getPoint(SketchDocument doc, SketchNode node) {  return 0; }
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
    abstract static class ZVSnapper extends VSnapper {
        public abstract double getPart(SketchDocument doc, SketchNode node);

        public boolean canSnap(Point2D pt, SketchDocument doc, SketchNode node) {
            double v1 = pt.getX();
            double vx = getPart(doc, node);
            double v2 = getPoint(doc, node);
            return Math.abs(v1+vx-v2) < THRESH;
        }

        public double snap(Point2D pt, SketchDocument doc, SketchNode node) {
            double v1 = pt.getX();
            double vx = getPart(doc, node);
            double v2 = getPoint(doc, node);
            if(Math.abs(v1+vx-v2) < THRESH) {
                node.setTranslateX(v2 - vx);
            }
            return v2;
        }
    }
    private static interface Takeit {
        public double take(SketchDocument doc, SketchNode node);
    }
    private static HSnapper builditH(final Takeit t1, final Takeit t2) {
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
    private static VSnapper builditV(final Takeit t1, final Takeit t2) {
        return new ZVSnapper() {
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


    public static abstract class HSnapper implements Snapper {
        abstract double getPoint(SketchDocument doc, SketchNode node);
        public void drawSnap(GFX gfx, double pt) {
            gfx.setPaint(FlatColor.GREEN);
            gfx.drawLine(-300, pt, 1000, pt);
        }
    }
    public static abstract class VSnapper implements Snapper {
        abstract double getPoint(SketchDocument doc, SketchNode node);
        public void drawSnap(GFX gfx, double pt) {
            gfx.setPaint(FlatColor.GREEN);
            gfx.drawLine(pt, -300, pt, 600);
        }
    }
}

