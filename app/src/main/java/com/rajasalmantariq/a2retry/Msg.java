package com.rajasalmantariq.a2retry;

import android.util.Log;

import java.util.StringTokenizer;

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

    public Msg(String s){
        StringTokenizer stok=new StringTokenizer(s, ",");

        if (stok.hasMoreTokens())
            stok.nextToken();
        if (stok.hasMoreTokens())
            stok.nextToken();

        if (stok.hasMoreTokens())
            from=stok.nextToken();
        if (stok.hasMoreTokens())
            msg=stok.nextToken();
        if (stok.hasMoreTokens())
            type=stok.nextToken();
//        if (stok.hasMoreTokens())
//            image=stok.nextToken();

        Log.d("msgCstr", "In msg: "+from+msg+type);

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
