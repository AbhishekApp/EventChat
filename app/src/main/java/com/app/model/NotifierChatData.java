package com.app.model;

/**
 * Created by admin on 3/23/2017.
 */

public class NotifierChatData  extends ChatData {

    private String CatID;
    private String eventID;

    NotifierChatData(ChatData alan, String CatID, String eventID){
        super(alan.getAuthor(), alan.getTitle(), alan.getToUser(), alan.getTimestamp(), alan.getAuthorType(), alan.getMessageType());
        this.CatID = CatID;
        this.eventID = eventID;
    }

    public String getCatID() {
        return CatID;
    }

    public void setCatID(String catID) {
        CatID = catID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
}
