package com.app.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 1/2/2017.
 */

public class EventDtList implements Serializable {

    String event_superCategory;
    String event_super_id;

    public ArrayList<EventSubCateList> Cate;

    public ArrayList<EventSubCateList> getCate() {
        return Cate;
    }

    public void setCate(ArrayList<EventSubCateList> cate) {
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
