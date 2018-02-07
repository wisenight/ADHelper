package com.wisely.ad.google;

import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.wisely.ad.BuildConfig;

import com.wisely.ad.BaseAd;
import com.wisely.ad.IAd;
import com.wisely.ad.IAdDisplayCallback;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public class GoogleInterstitialAd extends BaseAd {

    private final static String LOG_TAG = GoogleInterstitialAd.class.getSimpleName();

    private InterstitialAd interstitialAD = null;
    private IAdDisplayCallback displayCallback = null;

    GoogleInterstitialAd(InterstitialAd ad, boolean isHighECPM) {
        this.interstitialAD = ad;
        this.initTime = System.currentTimeMillis();
        this.isHighEcpm = isHighECPM;
    }

    @Override
    public void show(IAdDisplayCallback callback) {
        MobileAds.setAppMuted(true);
        MobileAds.setAppVolume(0.0f);
        synchronized (this) {
            if (this.interstitialAD != null) {
                this.displayCallback = callback;
                this.interstitialAD.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "onAdClosed");
                        }
                        synchronized (GoogleInterstitialAd.this) {
                            if (displayCallback != null) {
                                displayCallback.onClose(GoogleInterstitialAd.this);
                            }
                        }
                    }

                    @Override
                    public void onAdLeftApplication() {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "onAdLeftApplication");
                        }
                        synchronized (GoogleInterstitialAd.this) {
                            if (displayCallback != null) {
                                displayCallback.onClick(GoogleInterstitialAd.this);
                            }
                        }
                    }

                    @Override
                    public void onAdOpened() {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "onAdOpened");
                        }
                        synchronized (GoogleInterstitialAd.this) {
                            if (displayCallback != null) {
                                displayCallback.onShow(GoogleInterstitialAd.this);
                            }
                        }
                    }
                });
                this.interstitialAD.show();
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        synchronized (this) {
            if (this.interstitialAD != null) {
                this.interstitialAD.setAdListener(null);
                this.interstitialAD = null;
            }
            this.displayCallback = null;
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
            isValid = this.interstitialAD != null && this.interstitialAD.isLoaded() && super.isValid();
        }
        return isValid;
    }
}
