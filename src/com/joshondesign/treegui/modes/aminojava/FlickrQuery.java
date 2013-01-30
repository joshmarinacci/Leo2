package com.joshondesign.treegui.modes.aminojava;

import org.joshy.gfx.event.BackgroundTask;
import org.joshy.gfx.event.ChangedEvent;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.util.ArrayListModel;
import org.joshy.gfx.util.u;

public class FlickrQuery {
    @Prop(bindable = true) public String query;
    private ArrayListModel<Photo> results = new ArrayListModel<Photo>();
    @Prop(bindable = true) public Boolean active = false;


    public FlickrQuery() {
        setQuery("london");
        results.add(new Photo("london", "foo.png"));
        setActive(false);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public ListModel<Photo> getResults() {
        return results;
    }

    public void setResults(ListModel<Photo> results) {
        //this.results = results;
    }

    public void execute() throws InterruptedException {
        u.p("flickr query starting");
        setActive(true);
        results.clear();
        results.add(new Photo(getQuery() + " one", "foo.png"));
        results.add(new Photo(getQuery() + " two", "foo.png"));
        BackgroundTask<String, String> task = new BackgroundTask<String, String>() {
            @Override
            protected String onWork(String s) {
                try {
                    u.p("flickr query sleeping");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onEnd(String result) {
                results.add(new Photo(getQuery() + " three", "foo.png"));
                results.add(new Photo(getQuery() + " four", "foo.png"));
                setActive(false);
                u.p("flickr query ending");
            }
        };
        task.start();
    }

    public void setActive(boolean active) {
        this.active = active;
        EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.BooleanChanged, active,this));
    }

    public boolean isActive() {
        return active;
    }

    public static class Photo {
        @Prop (bindable = true) public String title;
        @Prop (bindable = true) public String url;

        public Photo(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

}
