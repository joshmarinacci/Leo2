package com.joshondesign.treegui.modes.aminolang;

import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojs.ActionProp;
import com.joshondesign.treegui.modes.aminojs.TriggerProp;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;

public abstract class Defs {

    @Metadata(visual = true)
    public static class VisualBase {
        @Prop public double translateX = 0;
        @Prop public double translateY = 0;
        @Prop public double width = 100;
        @Prop public double height = 50;
        @Prop public boolean anchorLeft = true;
        @Prop public boolean anchorRight = false;
        @Prop public boolean anchorTop = true;
        @Prop public boolean anchorBottom = false;
    }

    public static final  DynamicNode.DrawDelegate VisualBaseDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getWidth();
            double h = node.getHeight();
            g.setPaint(FlatColor.YELLOW);
            g.fillRoundRect(0, 0, w, h, 10, 10);
            g.setPaint(FlatColor.BLACK);
            g.drawRoundRect(0, 0, w, h, 10, 10);
            g.drawText(node.getName(), Font.DEFAULT, 5, 15);
        }
    };

    @Metadata(visual = true)
    public static class PushButton extends VisualBase {
        @Prop(visible = true, exported = true) public String text = "push button";
        @Prop public double fontSize = 20.0;
        @Prop public boolean enabled = true;
        @Prop(visible = false, bindable = true) public ActionProp action;
        @Prop public IconSymbols icon = IconSymbols.None;
    }

    public static final DynamicNode.DrawDelegate PushButtonDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getWidth();
            double h = node.getHeight();
            String t = node.getProperty("text").getStringValue();
            g.setPaint(FlatColor.GRAY);
            g.fillRect(0, 0, w, h);

            g.setPaint(FlatColor.BLACK);

            double xoff = 0;
            if(node.hasProperty("icon")) {
                IconSymbols symbol = (IconSymbols) node
                        .getProperty("icon").getEnumValue();
                if(symbol != IconSymbols.None) {
                    Font font = Font.name("FontAwesome").size(30).resolve();
                    xoff += 10;
                    Font.drawCenteredVertically(g, symbol.getChar()+"",font,xoff,0,w,h, false);
                    xoff += font.getWidth(symbol.getChar()+"");
                    xoff += 10;
                }
            }

            double size = 20;
            if(node.hasProperty("fontSize")) {
                size = node.getProperty("fontSize").getDoubleValue();
            }
            Font font = Font.name(Font.DEFAULT.getName()).size((float) size).resolve();
            //double y = font.getAscender() + font.getDescender();
            //g.drawText(t,font,5,y);

            Font.drawCentered(g, t, font, xoff,0,w-xoff-10,h,false);
            g.drawRect(0, 0, w, h);
        }
    };

    @Metadata(visual = true)
    public static class ToggleButton extends VisualBase {
        @Prop public String text = "toggle button";
        @Prop public boolean enabled = true;
        @Prop public boolean selected = false;
    }

    public static final DynamicNode.DrawDelegate ToggleButtonDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getWidth();
            double h = node.getHeight();
            String t = node.getProperty("text").getStringValue();
            g.setPaint(FlatColor.GRAY);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawText(t, Font.DEFAULT, 5, 15);
            g.drawRect(0, 0, w, h);
        }
    };

    @Metadata(visual = true)
    public static class Rect extends VisualBase {
        @Prop public FlatColor fill = FlatColor.BLUE;
        @Prop public double opacity = 1.0;
    }
    public static final DynamicNode.DrawDelegate RectDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getWidth();
            double h = node.getHeight();
            FlatColor color = node.getProperty("fill").getColorValue();
            double opacity = node.getProperty("opacity").getDoubleValue();
            g.setPaint(color.deriveWithAlpha(opacity));
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawRect(0, 0, w, h);
        }
    };


    @Metadata(visual = true)
    public static class Slider extends VisualBase {
        @Prop public Double minValue = 0.0;
        @Prop public double maxValue = 100;
        @Prop public double value = 50;
        @Prop public boolean enabled = true;
    }

    public static final DynamicNode.DrawDelegate SliderDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getWidth();
            double h = node.getHeight();
            g.setPaint(FlatColor.GRAY);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.fillRect(0, 0, h, h);
        }
    };

    @Metadata(name ="Label")
    public static class Label {
        @Prop(bindable = true)
        public CharSequence text = "a label";
        @Prop  public FlatColor color = FlatColor.BLACK;
        @Prop  public double fontSize = 20.0;
    }

    public static final DynamicNode.DrawDelegate LabelDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            String t = node.getProperty("text").getStringValue();
            FlatColor color = node.getProperty("color").getColorValue();
            double size = node.getProperty("fontSize").getDoubleValue();
            g.setPaint(color);
            Font font = Font.name(Font.DEFAULT.getName()).size((float) size).resolve();
            double y = font.getAscender() + font.getDescender();
            g.drawText(t,font,5,y);
        }
    };


    public enum ListViewLayout {  vert, horiz, horizwrap }

    @Metadata(name = "ListView")
    public static class ListView {
        @Prop public double cellHeight = 30;
        @Prop public double cellWidth  = 30;
        //@Prop(visible = false) public ListView.ItemRenderer renderer = null;
        //@Prop(bindable = true, exported = false) public ListModel model = null;
        @Prop public ListViewLayout layout = ListViewLayout.vert;
        @Prop public int selectedIndex = 0;
        //@Prop(bindable = true, compound = true, exported = false, master = "model", visible = false)
        //public Object selectedObject = null;
    }

    public static final DynamicNode.DrawDelegate ListViewDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            double cw = node.getProperty("cellWidth").getDoubleValue();
            double ch = node.getProperty("cellHeight").getDoubleValue();
            ListViewLayout layout = (ListViewLayout) node.getProperty("layout").getEnumValue();

            g.setPaint(FlatColor.GRAY);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawRect(0, 0, w, h);

            if(layout == ListViewLayout.vert) {
                for (int i=0; i<h; i += ch) {
                    g.drawLine(0, i, w, i);
                }
            }
            if(layout == ListViewLayout.horiz) {
                for (int i=0; i<w; i += cw) {
                    g.drawLine(i, 0, i, h);
                }
            }
            if(layout == ListViewLayout.horizwrap) {
                for (int i=0; i<w; i+=cw) {
                    g.drawLine(i,0,i,h);
                }
                for (int i=0; i<h; i += ch) {
                    g.drawLine(0, i, w, i);
                }
            }
        }
    };

    @Metadata(name = "AnchorPanel", container = true, resize = Resize.Any)
    public static class AnchorPanel {
        @Prop(bindable = true, visible = false) public Object self = this;
        @Prop public FlatColor fill = FlatColor.GRAY;
    }

    public static final DynamicNode.DrawDelegate AnchorPanelDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            FlatColor color = node.getProperty("fill").getColorValue();
            g.setPaint(color);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawRect(0, 0, w, h);
        }
    };



    @Metadata(name = "Transition", visual = false)
    public static class Transition {

        @Prop(bindable = true, visible = false)
        public TriggerProp pushTrigger;

        @Prop(bindable = true, visible = false)
        public Object pushTarget;

        //@Prop(visible = false, bindable = true) public ActionProp action;
    }

    public static final DynamicNode.DrawDelegate ServiceBaseDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getWidth();
            double h = node.getHeight();
            g.setPaint(FlatColor.YELLOW);
            g.fillRoundRect(0, 0, w, h, 10, 10);
            g.setPaint(FlatColor.BLACK);
            g.drawRoundRect(0, 0, w, h, 10, 10);
            g.drawText(node.getName(), Font.DEFAULT, 5, 15);
        }
    };


    @Metadata(name = "TextField", container = false, resize = Resize.Any)
    public static class TextField {
        @Prop public String text = "textfield";
    }

    public static final DynamicNode.DrawDelegate TextFieldDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getWidth();
            double h = node.getHeight();
            String t = node.getProperty("text").getStringValue();
            g.setPaint(FlatColor.WHITE);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawText(t, Font.DEFAULT, 5, 15);
            g.drawRect(0, 0, w, h);
        }
    };

    @Metadata(name = "TextArea", container = false, resize = Resize.Any)
    public static class TextArea {
        @Prop public String text = "textarea";
    }

    public static final DynamicNode.DrawDelegate TextAreaDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getWidth();
            double h = node.getHeight();
            String t = node.getProperty("text").getStringValue();
            g.setPaint(FlatColor.WHITE);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawText(t, Font.DEFAULT, 5, 15);
            g.drawRect(0, 0, w, h);
        }
    };



    public enum IconSymbols {  None('\0'), Heart('\uf004'), Star('\uf005'), Camera('\uf030');
        private final char symbol;

        IconSymbols(char symbol) {
            this.symbol = symbol;
        }
        public char getChar() {
            return symbol;
        }
    }

    @Metadata(name = "ImageView", container = false, resize = Resize.Any)
    public static class ImageView {
        @Prop(exported = true) public IconSymbols symbol = IconSymbols.Heart;
    }
    public static final DynamicNode.DrawDelegate ImageViewDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getWidth();
            double h = node.getHeight();
            IconSymbols symbol = (IconSymbols) node.getProperty("symbol").getEnumValue();
            g.setPaint(FlatColor.BLACK);
            Font font = Font.name("FontAwesome").size(30).resolve();
            g.drawText(""+symbol.getChar(),font,0,30);
            g.setPaint(FlatColor.GRAY);
            g.drawRect(0, 0, w, h);
        }
    };
}
