package com.wisely.ad.google;

import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.wisely.ad.BuildConfig;

import com.wisely.ad.BaseAd;
import com.wisely.ad.IAd;
import com.wisely.ad.IAdDisplayCallback;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public class GoogleBannerAd extends BaseAd.BaseBannerAd {

    private final static String LOG_TAG = GoogleBannerAd.class.getSimpleName();

    private AdView adView = null;
    private IAdDisplayCallback displayCallback = null;

    GoogleBannerAd(AdView ad, boolean isHighECPM) {
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
                    public void onAdClicked() {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "onAdClicked");
                        }
                        synchronized (GoogleBannerAd.this) {
                            if (displayCallback != null) {
                                displayCallback.onClick(GoogleBannerAd.this);
                            }
                        }
                    }

                    @Override
                    public void onAdLeftApplication() {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "onAdLeftApplication");
                        }
                        synchronized (GoogleBannerAd.this) {
                            if (displayCallback != null) {
                                displayCallback.onClick(GoogleBannerAd.this);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void pause() {
        synchronized (this) {
            super.pause();
            if (this.adView != null) {
                this.adView.pause();
            }
        }
    }

    @Override
    public void resume() {
        synchronized (this) {
            super.resume();
            if (this.adView != null) {
                this.adView.resume();
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
        return IAd.SOURCE_ADMOB;
    }

    @Override
    public boolean isValid() {
        boolean isValid;
        synchronized (this) {
            isValid = this.adView != null && !this.adView.isLoading() && super.isValid();
        }
        return isValid;
    }
}
