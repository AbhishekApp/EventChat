package com.app.model;

import java.util.ArrayList;

/**
 * Created by manish on 9/16/2016.
 */
public class EventModel {

    String event_superCategory;
    String event_super_id;
    public ArrayList<EventDetail> Cate;

    public ArrayList<EventDetail> getCate() {
        return Cate;
    }

    public void setCate(ArrayList<EventDetail> cate) {
        Cate = cate;
    }

    public String getEvent_superCategory() {
        return event_superCategory;
    }

    public void setEvent_superCategory(String event_superCategory) {
        this.event_superCategory = event_superCategory;
    }

    public String getEvent_super_id() {
        return event_super_id;
    }

    public void setEvent_super_id(String event_super_id) {
        this.event_super_id = event_super_id;
    }

    public void setEvent_super_category(String event_superCategory){
        this.event_superCategory = event_superCategory;
    }

    public String getEvent_super_category(){
        return this.event_superCategory;
    }

}
