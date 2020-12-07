package com.rajasalmantariq.a2retry;

public class Msg {

    String msg;
    String type;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    String from;
    boolean seen;
    long time;

    public Msg(){

    }

    public Msg(String msg, String type, boolean seen, long time, String from) {
        this.msg = msg;
        this.type = type;
        this.seen = seen;
        this.time = time;
        this.from = from;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


}
