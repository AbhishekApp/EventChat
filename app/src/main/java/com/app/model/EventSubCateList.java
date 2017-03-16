package com.app.model;

import java.util.ArrayList;

/**
 * Created by admin on 1/2/2017.
 */

public class EventSubCateList {

//    String subscribed_user;
    String event_sub_id;
    String event_category;
    ArrayList<Sub_cate> Sub_cate;

    public ArrayList<Sub_cate> getSub_cate() {
        return Sub_cate;
    }

    public void setSub_cate(ArrayList<Sub_cate> sub_cate) {
        this.Sub_cate = sub_cate;
    }

    public String getEvent_sub_id() {
        return event_sub_id;
    }

    public void setEvent_sub_id(String event_sub_id) {
        this.event_sub_id = event_sub_id;
    }

    public String getEvent_category() {
        return event_category;
    }

    public void setEvent_category(String event_category) {
        this.event_category = event_category;
    }


   /* public String getSubscribed_user() {
        return subscribed_user;
    }

    public void setSubscribed_user(String subscribed_user) {
        this.subscribed_user = subscribed_user;
    }*/


}
