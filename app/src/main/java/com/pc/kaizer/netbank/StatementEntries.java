package com.pc.kaizer.netbank;

/**
 * Created by Prithvi on 02-04-2017.
 */

public class StatementEntries {
    private String tid, type, timestamp, line;

    public StatementEntries(){
    }

    public StatementEntries(String tid, String type, String timestamp, String line){
        this.tid = tid;
        this.type = type;
        this.timestamp = timestamp;
        this.line = line;
    }

    public String getTid(){
        return tid;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLine() {
        return line;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
