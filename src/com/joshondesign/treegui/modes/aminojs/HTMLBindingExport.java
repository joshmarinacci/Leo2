package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.util.OSUtil;
import org.joshy.gfx.util.u;

public class HTMLBindingExport extends AminoAction {

    public SketchDocument document;
    public Page page;

    public HTMLBindingExport(SketchDocument document) {
        this.document = document;
        this.page = document.get(0);
    }

    @Override
    public void execute() {
        try {
            //File file = File.createTempFile("foo",".html");
            //file.deleteOnExit();
            StringWriter content = new StringWriter();
            PrintWriter out = new PrintWriter(content);

            PropWriter w = new PropWriter(out);
            for(Layer layer : page.children()) {
                w.p("//layer");
                w.indent();
                for(SketchNode node : layer.children()) {
                    DynamicNode dnode = (DynamicNode) node;
                    exportNode(w, dnode, true);
                    if(node.isVisual() && AminoAdapter.shouldAddToScene(node, document.getBindings())) {
                        w.p("root.add(" + node.getId() + ");");
                    }
                    if(AminoAdapter.useSetup(dnode)) {
                        w.p(node.getId()+".setup(root);");
                    }
                }
                w.outdent();
            }

            for(Binding binding : document.getBindings()) {
                exportBinding(out,binding);
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

    private void exportBinding(PrintWriter out, Binding binding) {
        if(AminoAdapter.shouldExportAsSetter(binding)) {
            String prop = binding.getTargetProperty().getName();
            out.println(
                    binding.getTarget().getId() +
                            ".set" + prop.substring(0,1).toUpperCase()
                            +prop.substring(1)+"("
                            +binding.getSource().getId()
                            +");"
            );
            return;
        }

        if(AminoAdapter.shouldExportAsAdder(binding)) {
            out.print(binding.getTarget().getId()+".add(");
            out.print(binding.getSource().getId());
            out.println(");");
            return;
        }

        if(AminoAdapter.shouldExportAsTrigger(binding)) {
            out.println(
                    binding.getSource().getId()+".setCallback("
                    +"function(){\n");
                out.println("    "+binding.getTarget().getId()+".execute();");
            out.println("});");
            return;
        }

        if(binding.getSource().getName().equals("ListView")) {
            if(binding.getSourceProperty().isProxy()) {
                u.p("it's a proxy! " + binding.getSourceProperty().getName());
                u.p("master = " + binding.getSourceProperty().getMasterProperty());
                String n = binding.getTargetProperty().getName();
                String setterName = "set"+n.substring(0,1).toUpperCase()+n.substring(1);
                out.println(
                    binding.getSource().getId()+".listen(function(e){\n"
                    +"  var val = e.getSelectedObject()."+binding.getSourceProperty().getName()+";\n"
                    +"  "+binding.getTarget().getId()+"."+setterName+"(val);\n"
                    +"});\n"
                );
                return;
            }
        }

        out.println(
                "var binder = new Binder()"
                        +".set("+binding.getSource().getId()+",'"+binding.getSourceProperty().getName()+"',"
                        +binding.getTarget().getId()+",'"+binding.getTargetProperty().getName()+"');\n" +
                        "engine.addAnim(binder);\n"+
                        "binder.start();\n");
    }

    private void exportNode(PropWriter w, DynamicNode node, boolean includeVar) {
        u.p("doing node: " + node);
        String[] visualOnlyProps = new String[]{"x","y","width","height"};
        List<String> resizeOnlyProps = Arrays.asList("width", "height");

        if(includeVar) {
            w.newObj(node.getId(), AminoAdapter.getScriptClass(node));
        } else {
            w.newObj(AminoAdapter.getScriptClass(node));
        }
        w.indent();
        for(Property prop : node.getProperties()) {
            if(!AminoAdapter.shouldExportProperty(node,prop)) continue;
            u.p("writing: " + prop.getName() + " " + prop.getRawValue() + " exported = " + prop.isExported());
            String key = prop.getName();
            if(key.equals("translateX")) key = "x";
            if(key.equals("translateY")) key = "y";
            if(!node.isVisual() && Arrays.asList(visualOnlyProps).contains(key)) continue;
            if(node.isResizable()){
                String resize = node.getProperty("resize").getStringValue();
                if(!"any".equals(resize)) {
                    if(resizeOnlyProps.contains(key)) continue;
                }
            }
            if(!node.isResizable()) {
                if(resizeOnlyProps.contains(key)) continue;
            }
            w.prop(key, prop.getRawValue());
        }

        for(SketchNode child : node.children()) {
            w.p(".add(");
            exportNode(w, (DynamicNode) child, false);
            w.p(")");
        }
        if(includeVar) {
            w.p(";");
        }
        w.outdent();
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
    public CharSequence getDisplayName() {
        return "Export Webpage";
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
            if(value instanceof List) {
                StringBuffer sb = new StringBuffer();
                sb.append("[");
                boolean first = true;
                for(Object o : ((List)value)) {
                    if(!first) sb.append(",");
                    sb.append('"'+o.toString()+'"');
                    first = false;
                }
                sb.append("]");
                return sb.toString();
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

        public PropWriter newObj(String id, String name) {
            pindent();
            out.println("var " + id + " = new " + name + "()");
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
