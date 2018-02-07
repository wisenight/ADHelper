package com.wisely.ad;

import android.support.annotation.IntDef;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public interface IBannerAd extends IAd {

    void resume();

    void pause();

    View getView();

}
