package com.wisely.ad.facebook;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.wisely.ad.BuildConfig;

import java.util.List;

import com.wisely.ad.BaseAd;
import com.wisely.ad.IAd;
import com.wisely.ad.IAdDisplayCallback;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public class FacebookNativeAd extends BaseAd.BaseNativeAd {

    private final static String LOG_TAG = FacebookNativeAd.class.getSimpleName();

    private NativeAd nativeAd = null;
    private IAdDisplayCallback displayCallback = null;

    FacebookNativeAd(NativeAd ad, boolean isHighECPM) {
        this.nativeAd = ad;
        this.initTime = System.currentTimeMillis();
        this.isHighEcpm = isHighECPM;
    }

    @Override
    public void show(IAdDisplayCallback callback) {
        synchronized (this) {
            if (this.nativeAd != null) {
                this.displayCallback = callback;
                this.nativeAd.setAdListener(new com.facebook.ads.AdListener() {
                    @Override
                    public void onError(Ad ad, AdError adError) {

                    }

                    @Override
                    public void onAdLoaded(Ad ad) {

                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "onAdClicked");
                        }
                        synchronized (FacebookNativeAd.this) {
                            if (displayCallback != null) {
                                displayCallback.onClick(FacebookNativeAd.this);
                            }
                        }
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {

                    }
                });
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        synchronized (this) {
            if (this.nativeAd != null) {
                this.nativeAd.setAdListener(null);
                this.nativeAd.unregisterView();
                this.nativeAd.destroy();
                this.nativeAd = null;
            }
        }
    }

    @Override
    public int getAdSource() {
        return IAd.SOURCE_FACEBOOK;
    }

    @Override
    public boolean isValid() {
        boolean isValid;
        synchronized (this) {
            isValid = this.nativeAd != null && super.isValid();
        }
        return isValid;
    }

    @Override
    public void registerView(View container, List<View> viewList) {
        synchronized (this) {
            this.nativeAd.registerViewForInteraction(container, viewList);
        }
    }

    @Override
    public String getTitle() {
        String title;
        synchronized (this) {
            title = this.nativeAd != null ? this.nativeAd.getAdTitle() : null;
        }
        return title;
    }

    @Override
    public String getBody() {
        String body;
        synchronized (this) {
            body = this.nativeAd != null ? this.nativeAd.getAdBody() : null;
        }
        return body;
    }

    @Override
    public String getAction() {
        String action;
        synchronized (this) {
            action = this.nativeAd != null ? this.nativeAd.getAdCallToAction() : null;
        }
        return action;
    }

    @Override
    public String getIcon() {
        return this.nativeAd != null ? this.nativeAd.getAdIcon().getUrl() : null;
    }

    @Override
    public String getCover() {
        String cover;
        synchronized (this) {
            cover = this.nativeAd != null ? this.nativeAd.getAdCoverImage().getUrl() : null;
        }
        return cover;
    }

    @Override
    public String getSocialContext() {
        String context;
        synchronized (this) {
            context = this.nativeAd != null ? this.nativeAd.getAdSocialContext() : null;
        }
        return context;
    }

    @Override
    public View getAdChoiceView(Context context) {
        View view;
        synchronized (this) {
            view = new AdChoicesView(context, this.nativeAd, true);
        }
        return view;
    }
}
