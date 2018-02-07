package com.wisely.ad;

import android.app.Application;

/**
 * Created by wisely on 2018/1/8.
 *
 */

public class AdProxy {

    private static AdProxy sIns = null;
    public synchronized static AdProxy getIns() {
        if (sIns == null) {
            sIns = new AdProxy();
        }
        return sIns;
    }

    private Application application;

    private AdProxy() {

    }

    public synchronized void setApplicationContext(Application application) {
        this.application = application;
    }

    public synchronized Application getApplicationContext() {
        return this.application;
    }
}
