package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.BindingUtils;
import com.joshondesign.treegui.Mode;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.*;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.aminojs.ActionProp;
import com.joshondesign.treegui.modes.aminojs.TriggerProp;
import java.util.*;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.node.control.ListView;
import org.joshy.gfx.node.control.Menu;
import org.joshy.gfx.node.control.ScrollPane;

public class AminoJavaMode extends Mode {
    public static Map<String, DynamicNode.DrawDelegate> drawMap = new HashMap<String, DynamicNode.DrawDelegate>();


    @Metadata(name ="PushButton", exportClass = "org.joshy.gfx.node.control.Button")
    public static class PushbuttonProxy {
        @Prop(bindable = true)
        public CharSequence text = "push button";

        @Prop(exported = false, bindable = true, visible = false)
        public TriggerProp trigger;
    }

     DynamicNode.DrawDelegate pushButtonDelegate = new DynamicNode.DrawDelegate() {
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


    @Metadata(name = "ToggleButton", exportClass = "org.joshy.gfx.node.control.Togglebutton")
    public static class TogglebuttonProxy extends PushbuttonProxy {
        public TogglebuttonProxy() {
            text = "toggle button";
        }

        @Prop
        public boolean selected = false;

        @Prop(visible = false, bindable = true)
        public Object toggleGroup = null;
    }

    private final DynamicNode.DrawDelegate toggleButtonDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            String t = node.getProperty("text").getStringValue();
            boolean b = node.getProperty("selected").getBooleanValue();

            g.setPaint(FlatColor.GRAY);
            if (b) {
                g.setPaint(FlatColor.BLUE);
            }
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawText(t, Font.DEFAULT, 5, 15);
            g.drawRect(0, 0, w, h);
        }
    };

    @Metadata(name ="Label", exportClass = "org.joshy.gfx.node.control.Label")
    public static class LabelProxy {
        @Prop(bindable = true)
        public CharSequence text = "a label";

        @Prop(exported = false, bindable = true, visible = false)
        public TriggerProp trigger;
    }

    private final DynamicNode.DrawDelegate labelDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            String t = node.getProperty("text").getStringValue();
            g.setPaint(FlatColor.BLACK);
            g.drawText(t, Font.DEFAULT, 5, 15);
        }
    };


    @Metadata(name = "Checkbox", exportClass = "org.joshy.gfx.node.control.Checkbox")
    public static class CheckboxProxy extends TogglebuttonProxy {
        public CheckboxProxy() {
            text = "checkbox";
        }
    }

    private final DynamicNode.DrawDelegate checkboxDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double h = node.getHeight();
            String t = node.getProperty("text").getStringValue();

            g.setPaint(FlatColor.GRAY);
            g.fillRect(0, 0, h , h);
            g.setPaint(FlatColor.BLACK);
            g.drawText(t, Font.DEFAULT, 5 + h, 15);
            g.drawRect(0,0, h, h);
        }
    };

    @Metadata(name = "Panel", container = true,
            exportClass = "com.joshondesign.treegui.AnchorPanel")
    public static class PanelProxy {
        @Prop public FlatColor fill = FlatColor.GRAY;
    }


    private final DynamicNode.DrawDelegate panelDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            g.setPaint(FlatColor.GRAY);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawRect(0, 0, w, h);
        }
    };



    @Metadata(name = "ListView", exportClass = "org.joshy.gfx.node.control.ListView")
    public static class ListViewProxy {
        @Prop double rowheight = 20;
        @Prop double columnWidth = 100;
        @Prop(visible = false) ListView.ItemRenderer renderer = null;
        @Prop(bindable = true, exported = false) ListModel model = null;
        @Prop  public ListView.Orientation orientation = ListView.Orientation.Vertical;
        @Prop(bindable = true) int selectedIndex = 0;
        @Prop(bindable = true) Object selectedItem = null;
    }

    private final DynamicNode.DrawDelegate listviewDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            g.setPaint(FlatColor.GRAY);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawRect(0, 0, w, h);
            //lines
            for (int i = 0; i < 5; i++) {
                g.drawRect(0, i * 15, w, 15);
            }
        }
    };


    @Metadata(name = "ScrollPane", container = true,
            exportClass = "org.joshy.gfx.node.control.ScrollPane")
    public static class ScrollPaneProxy {
        @Prop ScrollPane.VisiblePolicy horizontalVisiblePolicy =
                ScrollPane.VisiblePolicy.WhenNeeded;
        @Prop ScrollPane.VisiblePolicy verticalVisiblePolicy =
                ScrollPane.VisiblePolicy.WhenNeeded;
    }

    private final DynamicNode.DrawDelegate scrollDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            g.setPaint(FlatColor.GRAY);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawRect(0, 0, w, h);
            g.drawRect(0, h - 10, w, 10);
            g.drawRect(w - 10, 0, 10, h);
        }
    };


    @Metadata(name = "Spinner", exportClass = "com.joshondesign.treegui.modes.aminojava.Spinner")
    public static class SpinnerProxy {
        @Prop(bindable = true) public boolean active = false;
    }
    private final DynamicNode.DrawDelegate spinnerDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            g.setPaint(FlatColor.GRAY);
            g.fillOval(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawOval(0, 0, w, h);
        }
    };

    @Metadata(name = "CustomView", exportClass = "org.joshy.gfx.node.control.ScrollPane")
    public static class CustomViewProxy {
        @Prop public String customClass = "none";
    }

    private final DynamicNode.DrawDelegate customDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            g.setPaint(FlatColor.GRAY);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawRect(0, 0, w, h);
            g.drawLine(0, 0, w, h);
            g.drawLine(0, h, w, 0);
        }
    };

    @Metadata(name = "Textbox", exportClass = "org.joshy.gfx.node.control.Textbox")
    public static class TextboxProxy {
        @Prop(bindable = true) public CharSequence hintText = "hint";
        @Prop(bindable = true) public String text = "text box";
    }

    private final DynamicNode.DrawDelegate textboxDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            String t = node.getProperty("text").getStringValue();
            g.setPaint(FlatColor.GRAY);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawRect(0, 0, w, h);
            g.drawText(t, Font.DEFAULT, 5, 15);
        }
    };


    @Metadata(name = "Radiobutton", exportClass = "org.joshy.gfx.node.control.Radiobutton")
    public static class RadiobuttonProxy extends TogglebuttonProxy {
        public RadiobuttonProxy() {
            this.text = "radiobutton";
        }
    }

    private final DynamicNode.DrawDelegate radiobuttonDelegate =  new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double h = node.getHeight();
            String t = node.getProperty("text").getStringValue();

            g.setPaint(FlatColor.GRAY);
            g.fillOval(0, 0, h, h);
            g.setPaint(FlatColor.BLACK);
            g.drawOval(0, 0, h, h);
            g.drawText(t, Font.DEFAULT, 5 + h, 15);
        }
    };


    @Metadata(name = "PopupMenuButton", exportClass = "org.joshy.gfx.node.control.PopupMenuButton")
    public static class PopupMenuButtonProxy {
        @Prop public Boolean selected = false;
        @Prop(bindable = true) public Integer selectedIndex = 0;
    }

    private final DynamicNode.DrawDelegate popupbuttonDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getWidth();
            double h = node.getHeight();
            Property pmodel  = node.getProperty("model");
            ListModel model = (ListModel) pmodel.getRawValue();
            g.setPaint(FlatColor.GRAY);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            if (model != null && model.size() >= 1) {
                String t = model.get(0).toString();
                g.drawText(t, Font.DEFAULT, 5, 15);
            }
            g.drawRect(0, 0, w, h);
        }
    };


    @Metadata(name = "ToggleGroup", exportClass = "org.joshy.gfx.node.control.Togglegroup")
    public static class ToggleGroupProxy {
        @Prop public Integer selectedIndex = 0;
        @Prop public Object  selectedObject = null;
    }


    @Metadata(name = "StringList", visual = false, exportClass = "com.joshondesign.flickr.FlickrQuery" , resize = Resize.None)
    public static class StringList {
        @Prop(bindable = true, visible = true)
        public List data = Arrays.asList(new String[]{"foo","bar","baz"});
    }

    @Metadata(name = "CompoundList", exportClass = "org.joshy.gfx.node.control.CompoundListView", resize = Resize.Any, container = true)
    public static class CompoundListProxy {
        @Prop public double rowHeight = 30;
        @Prop(bindable = true, exported = false, visible = false)
        public ListModel model = null;
    }


    @Metadata(name = "ServiceBase", resize = Resize.None)
    public static class ServiceBase {
        @Prop(exported = false) public double translateX = 0;
        @Prop(exported = false) public double translateY = 0;
        @Prop(exported = false) public double width = 90;
        @Prop(exported = false) public double height = 30;
    }

    private final DynamicNode.DrawDelegate servicebaseDelegate = new DynamicNode.DrawDelegate() {
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

    public AminoJavaMode() {
        setId("com.joshondesign.modes.aminojava");

        add(new TreeNode<JAction>());

        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        add(symbols);

        DynamicNode visualBase = new DynamicNode();
        visualBase
                .addProperty(new Property("translateX", Double.class, 0))
                .addProperty(new Property("translateY", Double.class, 0))
                .addProperty(new Property("width", Double.class, 80)
                    .setExportName("prefWidth"))
                .addProperty(new Property("height", Double.class, 30)
                        .setExportName("prefHeight"))
                .addProperty(new Property("anchorLeft", Boolean.class, true)
                    .setBindable(false))
                .addProperty(new Property("anchorRight", Boolean.class, false)
                    .setBindable(false))
                .addProperty(new Property("anchorTop", Boolean.class, true)
                    .setBindable(false))
                .addProperty(new Property("anchorBottom", Boolean.class, false)
                    .setBindable(false))
                ;


        drawMap.put("PushButton", pushButtonDelegate);
        symbols.add(parse(new PushbuttonProxy(), pushButtonDelegate, visualBase));
        drawMap.put("ToggleButton", toggleButtonDelegate);
        symbols.add(parse(new TogglebuttonProxy(), toggleButtonDelegate, visualBase));
        drawMap.put("Label", labelDelegate);
        symbols.add(parse(new LabelProxy(), labelDelegate,visualBase));
        drawMap.put("Checkbox", checkboxDelegate);
        symbols.add(parse(new CheckboxProxy(), checkboxDelegate, visualBase));
        drawMap.put("Radiobutton", radiobuttonDelegate);
        symbols.add(parse(new RadiobuttonProxy(), radiobuttonDelegate, visualBase));

        Property popupSelected = new Property("selectedObject",Object.class,null);
        popupSelected.setBindable(true);
        popupSelected.setCompound(true);
        popupSelected.setMasterProperty("model");
        drawMap.put("Popupbutton", popupbuttonDelegate);

        symbols.add(parse(new PopupMenuButtonProxy(), popupbuttonDelegate,visualBase)
                .addProperty(new Property("model", ListModel.class,
                        ListView.createModel(new String[]{"Ethernet", "WiFi", "Bluetooth", "FireWire", "USB hack"}))
                        .setBindable(true).setExported(true).setVisible(true).setList(true))
                .addProperty(popupSelected)
        );

        drawMap.put("Panel", panelDelegate);
        symbols.add(parse(new PanelProxy(), panelDelegate, visualBase));

        drawMap.put("ListView", listviewDelegate);
        DynamicNode lv = parse(new ListViewProxy(), listviewDelegate, visualBase);
        Property subProp = new Property("selectedObject",Object.class,null);
        subProp.setExported(false);
        subProp.setBindable(true);
        subProp.setCompound(true);
        subProp.setMasterProperty("model");
        lv.addProperty(subProp);
        symbols.add(lv);


        drawMap.put("Scroll", scrollDelegate);
        symbols.add(parse(new ScrollPaneProxy(), scrollDelegate, visualBase));

        drawMap.put("Spinner", spinnerDelegate);
        symbols.add(parse(new SpinnerProxy(), spinnerDelegate, visualBase));

        drawMap.put("Custom", customDelegate);
        symbols.add(parse(new CustomViewProxy(), customDelegate, visualBase));

        drawMap.put("Textbox", textboxDelegate);
        symbols.add(parse(new TextboxProxy(), textboxDelegate, visualBase));

        drawMap.put("servicebase", servicebaseDelegate);
        DynamicNode serviceBase = parse(new ServiceBase(), servicebaseDelegate, null);

        //flickr query
        DynamicNode photo = parse(new FlickrQuery.Photo("a","b"), servicebaseDelegate, serviceBase);
        DynamicNode flickrQuery = parse(new FlickrQuery(), servicebaseDelegate, serviceBase);
        flickrQuery.addProperty(new Property("execute", ActionProp.class, null)
                        .setBindable(true).setVisible(false))
                    .addProperty(new Property("results", ListModel.class, null)
                            .setVisible(false).setBindable(true).setList(true)
                            .setItemPrototype(photo))
                ;
        symbols.add(flickrQuery);

        symbols.add(parse(new StringList(), servicebaseDelegate, serviceBase));

        DynamicNode alarmList = parse(new AlarmList(), servicebaseDelegate, serviceBase);
        DynamicNode alarm = parse(new Alarm(), servicebaseDelegate, serviceBase);
        alarmList.getProperty("data").setItemPrototype(alarm);
        symbols.add(alarmList);

        DynamicNode compoundList = parse( new CompoundListProxy(), listviewDelegate, visualBase);
        DynamicNode template = parse(new PanelProxy(), panelDelegate, visualBase);
        template.setPositionLocked(true);
        DynamicNode mirror = new DynamicNode();
        mirror.setMirror(true);
        mirror.setResize(Resize.None);
        mirror.setName("Mirror");
        mirror.setMirrorTarget("model");
        mirror.copyPropertiesFrom(serviceBase);
        mirror.setDrawDelegate(servicebaseDelegate);
        mirror.getProperty("translateX").setDoubleValue(-100);
        template.add(mirror);
        compoundList.add(template);
        symbols.add(compoundList);

        symbols.add(parse(new ToggleGroupProxy(), servicebaseDelegate, serviceBase));
    }

    private static DynamicNode parse(Object o, DynamicNode.DrawDelegate del, DynamicNode base) {
        DynamicNode nd = BindingUtils.parseAnnotatedPOJO(o, del);
        if(base != null) {
            nd.copyPropertiesFrom(base);
        }
        return nd;
    }

    @Override
    public String getName() {
        return "Amino Java";
    }

    @Override
    public SketchDocument createEmptyDoc() {
        SketchDocument doc = new SketchDocument();
        Layer layer = new Layer();
        layer.add(findSymbol("Panel").duplicate(null));
        Page page = new Page();
        page.add(layer);
        doc.add(page);
        return doc;
    }

    @Override
    public void modifyNodeMenu(Menu nodeMenu, final SketchDocument doc) {
        super.modifyNodeMenu(nodeMenu, doc);
        nodeMenu.addItem("Align Left", AlignLeft(doc));
        nodeMenu.addItem("Align Center Horizontal", AlignCenterH(doc));
        nodeMenu.addItem("Align Right", AlignRight(doc));
        nodeMenu.addItem("Align Top", AlignTop(doc));
        nodeMenu.addItem("Align Center Vertical", AlignCenterV(doc));
        nodeMenu.addItem("Align Bottom", AlignBottom(doc));

        nodeMenu.addItem("Lower Node To Bottom", "shift CLOSE_BRACKET", LowerNodeToBottom(doc));
        nodeMenu.addItem("Lower Node", "CLOSE_BRACKET", LowerNode(doc));
        nodeMenu.addItem("Raise Node", "OPEN_BRACKET", RaiseNode(doc));
        nodeMenu.addItem("Raise Node To Top", "shift OPEN_BRACKET", RaiseNodeToTop(doc));

        nodeMenu.addItem("Same Width", SameWidth(doc));
        nodeMenu.addItem("Same Height", SameHeight(doc));
    }

    @Override
    public void modifyDocumentMenu(Menu documentMenu, SketchDocument doc) {
        super.modifyDocumentMenu(documentMenu, doc);
        documentMenu.addItem("Add Page", AddPageAction(doc));
        documentMenu.addItem("Previous Page", "LEFT", PrevPageAction(doc));
        documentMenu.addItem("Next Page", "RIGHT", NextPageAction(doc));
    }

    @Override
    public void modifyFileMenu(Menu fileMenu, SketchDocument doc) {
        fileMenu.addItem("Run", "R",  new AminoJavaXMLExport.Test(doc));
    }

    @Override
    public Map<String, DynamicNode.DrawDelegate> getDrawMap() {
        return drawMap;
    }

    @Override
    public void modifyViewMenu(Menu viewMenu, SketchDocument doc) {
        super.modifyViewMenu(viewMenu, doc);
        viewMenu.addItem("Snap to Grid", SnapToGridAction(doc));
    }

    private AminoAction SnapToGridAction(final SketchDocument doc) {
        return new AminoAction() {
            @Override
            public void execute() throws Exception {
                doc.setSnapToGrid(!doc.isSnapToGrid());
            }
        };
    }

    @Override
    public List<AminoAction> getContextMenuActions(final SketchDocument doc, Selection selection) {
        List<AminoAction> list = super.getContextMenuActions(doc, selection);
        list.add(AlignLeft(doc));
        list.add(AlignRight(doc));
        list.add(AlignTop(doc));
        list.add(AlignBottom(doc));
        return list;
    }


    private static AminoAction groupOnly(final SketchDocument doc, final AminoAction aminoAction) {
        return new AminoAction() {
            @Override
            public CharSequence getDisplayName() {
                return aminoAction.getDisplayName();
            }

            @Override
            public void execute() throws Exception {
                if (doc.getSelection().getSize() < 2) return;
                aminoAction.execute();
            }
        };
    }
    private static AminoAction notEmpty(final SketchDocument doc, final AminoAction aminoAction) {
        return new AminoAction() {
            @Override
            public CharSequence getDisplayName() {
                return aminoAction.getDisplayName();
            }

            @Override
            public void execute() throws Exception {
                if (doc.getSelection().getSize() < 1) return;
                aminoAction.execute();
            }
        };
    }
    private static AminoAction named(final String name, final AminoAction action) {
        return new AminoAction() {
            @Override
            public CharSequence getDisplayName() {
                return name;
            }

            @Override
            public void execute() throws Exception {
                action.execute();
            }
        };
    }

    private Double apply(Selection selection, Double minX, Accumulate<Double> acc) {
        for (SketchNode node : selection.children()) {
            minX = acc.accum(node, minX);
        }
        return minX;
    }
    private Integer apply(Selection selection, Integer minX, Accumulate<Integer> acc) {
        for (SketchNode node : selection.children()) {
            minX = acc.accum(node, minX);
        }
        return minX;
    }

    /* ==== reusable actions ==== */
    private AminoAction AlignLeft(final SketchDocument doc) {
        return named("Align Left", groupOnly(doc, new AminoAction() {
            @Override
            public void execute() throws Exception {
                double val = apply(doc.getSelection(), Double.MAX_VALUE, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        return Math.min(value, node.getInputBounds().getX() + node.getTranslateX());
                    }
                });

                apply(doc.getSelection(), val, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        double x = node.getInputBounds().getX() + node.getTranslateX();
                        node.setTranslateX(node.getTranslateX() + value - x);
                        return value;
                    }
                });
            }
        }));
    }
    private AminoAction AlignCenterH(final SketchDocument doc) {
        return named("Align Center Horizontal", groupOnly(doc, new AminoAction() {
            @Override
            public void execute() throws Exception {
                double val = apply(doc.getSelection(), Double.MIN_VALUE, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        return Math.max(value, node.getInputBounds().getCenterX() + node.getTranslateX());
                    }
                });
                apply(doc.getSelection(), val, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        double x = node.getInputBounds().getCenterX() + node.getTranslateX();
                        node.setTranslateX(node.getTranslateX() + value - x);
                        return value;
                    }
                });
            }
        }));
    }
    private AminoAction AlignRight(final SketchDocument doc) {
        return named("Align Right", groupOnly(doc, new AminoAction() {
            @Override
            public void execute() throws Exception {
                double val = apply(doc.getSelection(), Double.MIN_VALUE, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        return Math.max(value, node.getInputBounds().getX2() + node.getTranslateX());
                    }
                });
                apply(doc.getSelection(), val, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        double x = node.getInputBounds().getX2() + node.getTranslateX();
                        node.setTranslateX(node.getTranslateX() + value - x);
                        return value;
                    }
                });
            }
        }));
    }
    private AminoAction AlignBottom(final SketchDocument doc) {
        return named("Align Bottom", groupOnly(doc, new AminoAction() {
            @Override
            public void execute() throws Exception {
                double val = apply(doc.getSelection(), Double.MIN_VALUE, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        return Math.max(value, node.getInputBounds().getY2() + node.getTranslateY());
                    }
                });
                apply(doc.getSelection(), val, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        double y = node.getInputBounds().getY2() + node.getTranslateY();
                        node.setTranslateY(node.getTranslateY() + value - y);
                        return value;
                    }
                });
            }
        }));
    }
    private AminoAction AlignCenterV(final SketchDocument doc) {
        return named("Align Center Vertical", groupOnly(doc, new AminoAction() {
            @Override
            public void execute() throws Exception {
                double val = apply(doc.getSelection(), Double.MIN_VALUE, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        return Math.max(value, node.getInputBounds().getCenterY() + node.getTranslateY());
                    }
                });
                apply(doc.getSelection(), val, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        double y = node.getInputBounds().getCenterY() + node.getTranslateY();
                        node.setTranslateY(node.getTranslateY() + value - y);
                        return value;
                    }
                });
            }
        }));
    }
    private AminoAction AlignTop(final SketchDocument doc) {
        return named("Align Top", groupOnly(doc, new AminoAction() {
            @Override
            public void execute() throws Exception {
                double val = apply(doc.getSelection(), Double.MAX_VALUE, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        return Math.min(value, node.getInputBounds().getY() + node.getTranslateY());
                    }
                });
                apply(doc.getSelection(), val, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        double y = node.getInputBounds().getY() + node.getTranslateY();
                        node.setTranslateY(node.getTranslateY() + value - y);
                        return value;
                    }
                });
            }
        }));
    }
    private AminoAction LowerNode(final SketchDocument doc) {
        return named("Lower Node", notEmpty(doc, new AminoAction() {
            @Override
            public void execute() throws Exception {
                final TreeNode<SketchNode> model = doc.getSelection().get(0).getParent();
                Selection nodes = doc.getSelection();
                //find the lowest node index

                int min = apply(nodes, Integer.MAX_VALUE, new Accumulate<Integer>() {
                    public Integer accum(SketchNode node, Integer value) {
                        return Math.min(model.indexOf(node),value);
                    }
                });
                //if there is room to move down
                if (min > 0) {
                    SketchNode prevNode = model.get(min - 1);
                    model.removeAll(nodes);
                    model.addAll(model.indexOf(prevNode), nodes);
                } else {
                    //just remove and move all to the bottom
                    model.removeAll(nodes);
                    model.addAll(0, nodes);
                }
            }
        }));
    }
    private AminoAction LowerNodeToBottom(final SketchDocument doc) {
        return named("Lower Node", notEmpty(doc, new AminoAction() {
            @Override
            public void execute() throws Exception {
                TreeNode<SketchNode> model = doc.getSelection().get(0).getParent();
                Selection nodes = doc.getSelection();
                //just remove and move all to the bottom
                model.removeAll(nodes);
                model.addAll(0, nodes);
            }
        }));
    }
    private AminoAction RaiseNode(final SketchDocument doc) {
        return named("Raise Node", notEmpty(doc, new AminoAction() {
            @Override
            public void execute() throws Exception {
                final TreeNode<SketchNode> model = doc.getSelection().get(0).getParent();

                Selection nodes = doc.getSelection();
                int max = apply(nodes, Integer.MIN_VALUE, new Accumulate<Integer>() {
                    public Integer accum(SketchNode node, Integer value) {
                        return Math.max(value, model.indexOf(node));
                    }
                });

                //if there is room to move up
                if (max + 1 < model.getSize()) {
                    SketchNode nextNode = model.get(max + 1);
                    model.removeAll(nodes);
                    int n = model.indexOf(nextNode);
                    model.addAll(n + 1, nodes);
                } else {
                    //just remove and move all to the top
                    model.removeAll(nodes);
                    model.addAll(nodes);
                }
            }
        }));
    }
    private AminoAction RaiseNodeToTop(final SketchDocument doc) {
        return named("Raise Node To Top", notEmpty(doc, new AminoAction() {
            @Override
            public void execute() throws Exception {
                TreeNode<SketchNode> model = doc.getSelection().get(0).getParent();
                Selection nodes = doc.getSelection();
                //just remove and move all to the bottom
                model.removeAll(nodes);
                model.addAll(nodes);
            }
        }));
    }
    private AminoAction SameWidth(final SketchDocument doc) {
        return named("Same Width", groupOnly(doc, new AminoAction() {
            @Override
            public void execute() throws Exception {
                Selection nodes = doc.getSelection();
                double width = apply(nodes, Double.MIN_VALUE, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        return Math.max(node.getInputBounds().getWidth(), value);
                    }
                });
                apply(nodes, width, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        if(!(node instanceof DynamicNode)) return value;
                        DynamicNode nd = (DynamicNode) node;
                        if(nd.getResize() == Resize.None) return value;
                        nd.getProperty("width").setDoubleValue(value);
                        return value;
                    }
                });
            }
        }));
    }
    private AminoAction SameHeight(final SketchDocument doc) {
        return named("Same Height", groupOnly(doc, new AminoAction() {
            @Override
            public void execute() throws Exception {
                Selection nodes = doc.getSelection();
                double width = apply(nodes, Double.MIN_VALUE, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        return Math.max(node.getInputBounds().getHeight(), value);
                    }
                });
                apply(nodes, width, new Accumulate<Double>() {
                    public Double accum(SketchNode node, Double value) {
                        if(!(node instanceof DynamicNode)) return value;
                        DynamicNode nd = (DynamicNode) node;
                        if(nd.getResize() == Resize.None) return value;
                        nd.getProperty("height").setDoubleValue(value);
                        return value;
                    }
                });
            }
        }));
    }

    private AminoAction AddPageAction(final SketchDocument doc) {
        return named("Add New Page", new AminoAction() {
            @Override
            public void execute() throws Exception {
                Page page = new Page();
                page.add(new Layer());
                doc.add(page);
            }
        });
    }
    private AminoAction NextPageAction(final SketchDocument doc) {
        return named("Next Page", new AminoAction() {
            @Override
            public void execute() throws Exception {
                Page page = doc.getSelectedPage();
                int index = doc.indexOf(page);
                index++;
                if(index > doc.getSize()-1) {
                    index = doc.getSize()-1;
                }
                doc.setSelectedPage(doc.get(index));
                doc.getSelection().clear();
            }
        });
    }
    private AminoAction PrevPageAction(final SketchDocument doc) {
        return named("Previous Page", new AminoAction() {
            @Override
            public void execute() throws Exception {
                Page page = doc.getSelectedPage();
                int index = doc.indexOf(page);
                index--;
                if(index < 0) {
                    index = 0;
                }
                doc.setSelectedPage(doc.get(index));
                doc.getSelection().clear();
            }
        });
    }

}




interface Accumulate<T> {
    public T accum(SketchNode node, T value);
}
