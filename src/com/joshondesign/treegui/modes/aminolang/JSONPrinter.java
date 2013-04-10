package com.joshondesign.treegui.modes.aminolang;

/**
* Created with IntelliJ IDEA.
* User: josh
* Date: 4/2/13
* Time: 6:52 PM
* To change this template use File | Settings | File Templates.
*/
public class JSONPrinter {
    private final StringBuffer sb;
    private boolean start;
    private int tabDepth = 0;
    private String tab() {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<tabDepth; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }
    private void doStart() {
        if(start) {
            start = false;
            sb.append(tab()+" ");
        } else {
            sb.append(tab()+",");
        }
    }



    JSONPrinter() {
        this.sb = new StringBuffer();
        start = true;
    }

    public StringBuffer toStringBuffer() {
        return this.sb;
    }

    public JSONPrinter openObject() {
        tabDepth++;
        doStart();
        sb.append("{\n");
        start = true;
        return this;
    }
    public JSONPrinter openArray(String key) {
        tabDepth++;
        doStart();
        sb.append("\"" + key + "\":[\n");
        start = true;
        return this;
    }
    public JSONPrinter closeArray() {
        sb.append(tab()+"]\n");
        tabDepth--;
        start=false;
        return this;
    }
    public JSONPrinter closeObject() {
        sb.append(tab()+"}\n");
        tabDepth--;
        start=false;
        return this;
    }


    public JSONPrinter set(String key, String value) {
        doStart();
        sb.append("\""+key+"\":\""+value+"\"\n");
        return this;
    }

    public JSONPrinter set(String key, double value) {
        doStart();
        sb.append("\""+key+"\":"+value+"\n");
        return this;
    }

    public JSONPrinter set(String key, boolean value) {
        doStart();
        sb.append("\""+key+"\":"+value+"\n");
        return this;
    }

}
