package com.wisely.ad;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;

import java.util.List;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public abstract class BaseAd implements IAd {

    protected boolean isHighEcpm = false;
    protected long initTime = 0L;

    @Override
    public void destroy() {
    }

    @Override
    public final boolean isHighEcpm() {
        return this.isHighEcpm;
    }

    @Override
    public boolean isValid() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - initTime) < (30L * DateUtils.MINUTE_IN_MILLIS);
    }

    public abstract static class BaseBannerAd extends BaseAd implements IBannerAd {

        public void resume() {
        }

        public void pause() {
        }

        public abstract View getView();
    }

    public abstract static class BaseNativeAd extends BaseAd implements INativeAd {
        public abstract void registerView(View container, List<View> viewList);

        public abstract View getAdChoiceView(Context context);

        public abstract String getTitle();

        public abstract String getBody();

        public abstract String getAction();

        public abstract String getIcon();

        public abstract String getCover();

        public abstract String getSocialContext();
    }
}
