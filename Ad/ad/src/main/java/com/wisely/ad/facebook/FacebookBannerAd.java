package com.wisely.ad.facebook;

import android.util.Log;
import android.view.View;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;
import com.wisely.ad.BuildConfig;

import com.wisely.ad.BaseAd;
import com.wisely.ad.IAd;
import com.wisely.ad.IAdDisplayCallback;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public class FacebookBannerAd extends BaseAd.BaseBannerAd {

    private final static String LOG_TAG = FacebookBannerAd.class.getSimpleName();

    private AdView adView = null;
    private IAdDisplayCallback displayCallback = null;

    FacebookBannerAd(AdView ad, boolean isHighECPM) {
        this.adView = ad;
        this.initTime = System.currentTimeMillis();
        this.isHighEcpm = isHighECPM;
    }

    @Override
    public void show(IAdDisplayCallback callback) {
        synchronized (this) {
            if (this.adView != null) {
                this.displayCallback = callback;
                this.adView.setAdListener(new AdListener() {
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
                        synchronized (FacebookBannerAd.this) {
                            if (displayCallback != null) {
                                displayCallback.onClick(FacebookBannerAd.this);
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
    public View getView() {
        View view;
        synchronized (this) {
            view = this.adView;
        }
        return view;
    }

    @Override
    public void destroy() {
        super.destroy();
        synchronized (this) {
            if (this.adView != null) {
                this.adView.setAdListener(null);
                this.adView.destroy();
                this.adView = null;
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
            isValid = this.adView != null && super.isValid();
        }
        return isValid;
    }
}
