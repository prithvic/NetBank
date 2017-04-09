package com.pc.kaizer.netbank;

import android.util.Log;

/**
 * Created by Prithvi on 09-04-2017.
 */

public class FDREntries {
    private String fid, ftimestamp, famt;

    public FDREntries() {
    }

    public FDREntries(String fid, String ftimestamp, String famt) {
        Log.d("RV",fid);
        Log.d("RV",ftimestamp);
        Log.d("RV",famt);
        this.fid = fid;
        this.ftimestamp = ftimestamp;
        this.famt = famt;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFTimestamp() {
        return ftimestamp;
    }

    public void setFTimestamp(String ftimestamp) {
        this.ftimestamp = ftimestamp;
    }

    public String getFAmt() {
        return famt;
    }

    public void setFAmt(String famt) {
        this.famt = famt;
    }
}