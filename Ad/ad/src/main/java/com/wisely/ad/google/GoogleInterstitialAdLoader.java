package com.wisely.ad.google;

import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.wisely.ad.BuildConfig;

import com.wisely.ad.AdProxy;
import com.wisely.ad.BaseAdLoader;
import com.wisely.ad.IAdLoader;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public abstract class GoogleInterstitialAdLoader extends BaseAdLoader {
    private final static String LOG_TAG = GoogleInterstitialAdLoader.class.getSimpleName();

    private InterstitialAd interstitialAD = null;

    @Override
    public void load(IAdLoader.Callback callback) {
        this.adCallback = callback;
        this.startLoadTime = System.currentTimeMillis();
        this.interstitialAD = new InterstitialAd(AdProxy.getIns().getApplicationContext());
        this.interstitialAD.setAdUnitId(this.adId);
        this.interstitialAD.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int var1) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "onAdFailedToLoad, isHighEcpm: " + isHighEcpm);
                }
                if (interstitialAD != null) {
                    interstitialAD.setAdListener(null);
                }
                if (adCallback != null) {
                    GoogleInterstitialAd gad = new GoogleInterstitialAd(null, isHighEcpm);
                    adCallback.onAdLoadFailed(GoogleInterstitialAdLoader.this, gad);
                }
                interstitialAD = null;
                adCallback = null;
            }

            @Override
            public void onAdLoaded() {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "onAdLoaded, isHighEcpm: " + isHighEcpm);
                }
                if (interstitialAD != null) {
                    interstitialAD.setAdListener(null);
                }
                if (adCallback != null) {
                    GoogleInterstitialAd gad = new GoogleInterstitialAd(interstitialAD, isHighEcpm);
                    adCallback.onAdLoaded(GoogleInterstitialAdLoader.this, gad);
                }
                interstitialAD = null;
                adCallback = null;
            }
        });
        this.interstitialAD.loadAd(new AdRequest.Builder().build());
    }
}
