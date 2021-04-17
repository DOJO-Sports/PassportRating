package com.dojo.passport;

import java.util.Date;

public class ChatModel {

    String Message;
    String Send;
    Date Time;
    String Type;
    Boolean Seen;

    public ChatModel() {}

    public ChatModel(String message, String send, Date time, String type, Boolean seen) {
        Message = message;
        Send = send;
        Time = time;
        Type = type;
        Seen = seen;
    }

    public String getMessage() {
        return Message;
    }

    public String getSend() {
        return Send;
    }

    public Date getTime() {
        return Time;
    }

    public String getType() {
        return Type;
    }

    public Boolean getSeen() {
        return Seen;
    }

    public void setSeen(Boolean seen) {
        Seen = seen;
    }
}
