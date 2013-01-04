package com.joshondesign.treegui;

import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.amino.AminoAdapter;
import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.util.OSUtil;
import org.joshy.gfx.util.u;

public class HTMLBindingExport extends JAction {

    public Canvas canvas;
    public Page page;

    @Override
    public void execute() {
        try {
            //File file = File.createTempFile("foo",".html");
            //File file = new File("/Users/josh/projects/Leo/t2/foo.html");
            //file.deleteOnExit();
            StringWriter content = new StringWriter();
            PrintWriter out = new PrintWriter(content);

            //PrintWriter out = new PrintWriter(new FileWriter(file));
            //PrintWriter out = new PrintWriter(System.out);
//            out.println("<html><head>");
//            out.println("<script src='amino.js'></script>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<canvas id='canvas' width='500' height='400'></canvas>");
//
//            out.println("<script language='Javascript'>");
//
//            out.println("var engine = new Amino();");
//            out.println("var root = engine.addCanvas('canvas');");
            PropWriter w = new PropWriter(out);
            for(Layer layer : page.children()) {
                w.p("//layer");
                w.indent();
                for(SketchNode node : layer.children()) {
                    w.newObjVarOpen(node.getId(), "Transform").indent();
                    exportNode(w,node);
                    /*
                    if(node instanceof ResizableRectNode) {
                        ResizableRectNode rect = (ResizableRectNode) node;
                        w.newObj("Rect")
                            .indent()
                            .p(".set(0,0," + rect.getWidth() + "," + rect.getHeight() + ")")
                            .prop("fill", "red")
                            .outdent();
                    } else {
                        w.newObj("Rect")
                            .indent()
                            .p(".set(0,0,100,100)")
                            .prop("fill", "red")
                            .outdent();
                    } */
                    w.newObjVarClose();

                    w.p("translateX", node.getTranslateX());
                    w.p("translateY", node.getTranslateY());
                    w.p(";").outdent();

                    w.p("root.add(" + node.getId() + ");");

                    if(PropUtils.propertyEquals(node,"draggable",true)) {
                        w.p("root.onPress("
                                +node.getId()
                                +",function() {" +
                                node.getId()
                                +".setTranslateX("
                                +node.getId()
                                +".getTranslateX()+10);"+
                                "});");
                    }
                }
                w.outdent();
            }


            for(Binding binding : canvas.getBindings()) {
                out.println(
                    "var binder = new Binder()"
                            +".set("+binding.getSource().getId()+",'"+binding.getSourceProperty()+"',"
                            +binding.getTarget().getId()+",'"+binding.getTargetProperty()+"');\n" +
                    "engine.addAnim(binder);\n"+
                    "binder.start();\n");
            }

            out.close();
            u.p("output = " + content.toString());
            File dir = new File("/Users/josh/projects/Leo/t2");
            Map<String,String> subs = new HashMap<String,String>();
            subs.put("content",content.toString());
            applyTemplate(
                    new File(dir,"foo_template.html"),
                    new File(dir,"foo.html"),
                    subs);
            OSUtil.openBrowser("http://localhost/~josh/projects/Leo/t2/foo.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportNode(PropWriter w, SketchNode node) {
        w.newObj(AminoAdapter.getScriptClass(node));
        w.indent();
        for(Map.Entry<String,Object> props : AminoAdapter.getProps(node).entrySet()) {
            u.p("writing: " + props.getKey() + " " + props.getValue());
            w.prop(props.getKey(),props.getValue());
        }
        w.outdent();
        /*
        ResizableRectNode rect = (ResizableRectNode) node;
        w.newObj("Rect")
                .indent()
                .p(".set(0,0," + rect.getWidth() + "," + rect.getHeight() + ")")
                .prop("fill", "red")
                .outdent();
                */
    }

    private void applyTemplate(File in, File out, Map<String, String> subs) {
        try {
            String str = u.fileToString(new FileInputStream(in));
            for(Map.Entry<String, String> item : subs.entrySet()) {
                str = str.replaceAll("\\$\\{" + item.getKey() + "\\}", item.getValue());
            }
            u.stringToFile(str,out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getShortName() {
        return "Export HTML";
    }

    private class PropWriter {
        private final PrintWriter out;
        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
        private int indent;

        public PropWriter(PrintWriter out) {
            this.out = out;
            decimalFormat.setMaximumFractionDigits(2);
            decimalFormat.setMinimumFractionDigits(0);
        }

        public PropWriter prop(String name, String value) {
            pindent();
            out.print(".set" + name.substring(0, 1).toUpperCase() + name.substring(1));
            out.print("("+"'"+value+"'"+")");
            out.println();
            return this;
        }

        private void pindent() {
            for(int i=0; i<indent; i++) {
                out.print("    ");
            }
        }

        public PropWriter p(String name, double value) {
            pindent();
            out.print(".set" + name.substring(0, 1).toUpperCase() + name.substring(1));
            out.print("("+""+value+""+")");
            out.println();
            return this;
        }

        public PropWriter prop(String name, Object value) {
            pindent();
            out.print(".set" + name.substring(0, 1).toUpperCase() + name.substring(1));
            out.print("("+valueToString(value)+")");
            out.println();
            return this;
        }

        private String valueToString(Object value) {
            if(value instanceof String) {
                return '"'+((String)value)+'"';
            }
            if(value instanceof FlatColor) {
                FlatColor fc = (FlatColor) value;
                return '"'+"#"+Integer.toHexString(fc.getRGBA()).substring(2) + '"';
            }
            if(value == null) return "null";
            return value.toString();
        }

        public PropWriter indent() {
            this.indent++;
            return this;
        }

        public PropWriter outdent() {
            this.indent--;
            return this;
        }

        public PropWriter pwen() {
            pindent();
            out.println(")");
            return this;
        }

        public PropWriter pnew(String name) {
            pindent();
            out.println(" new "+name+"(");
            return this;
        }

        public PropWriter p(String s) {
            pindent();
            out.println(s);
            return this;
        }

        public PropWriter newObj(String name) {
            pindent();
            out.println(" new "+name+"()");
            return this;
        }

        public PropWriter newObjVarOpen(String id, String name) {
            pindent();
            out.println("var " + id + " = new " + name + "(");
            return this;
        }

        public PropWriter newObjVarClose() {
            pindent();
            out.println(")");
            return this;
        }
    }
}
