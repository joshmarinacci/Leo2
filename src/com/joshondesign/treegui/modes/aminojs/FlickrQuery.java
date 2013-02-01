package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import java.util.List;

@Metadata(visual = false, exportClass = "FlickrQuery")
public class FlickrQuery {

    @Prop(bindable = true)
    public String query = "";

    @Prop(bindable = true, exported = false, visible = false, list = true)
    public List<Object> results;

    @Prop(bindable = true, visible = false, exported = false)
    public ActionProp execute;

    @Prop(bindable = true) public boolean active = false;


    @Metadata(visual = false)
    public static class Photo {
        @Prop (bindable = true) public String title;
        @Prop (bindable = true) public String url;

        public Photo(String title, String url) {
            this.title = title;
            this.url = url;
        }
    }
}
