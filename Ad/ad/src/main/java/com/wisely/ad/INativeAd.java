package com.wisely.ad;

import android.content.Context;
import android.support.annotation.IntDef;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public interface INativeAd extends IAd {

    void registerView(View container, List<View> viewList);

    View getAdChoiceView(Context context);

    String getTitle();

    String getBody();

    String getAction();

    String getIcon();

    String getCover();

    String getSocialContext();

}
