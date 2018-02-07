package com.wisely.ad;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public interface IAd {

    @IntDef({SOURCE_ADMOB, SOURCE_FACEBOOK})
    @Retention(RetentionPolicy.SOURCE)
    @interface AdSource {}
    int SOURCE_ADMOB = 1;
    int SOURCE_FACEBOOK = 2;

    void show(IAdDisplayCallback displayCallback);
    void destroy();

    @AdSource
    int getAdSource();
    boolean isHighEcpm();
    boolean isValid();

}
