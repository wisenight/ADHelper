package com.wisely.ad.google;

import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.wisely.ad.BuildConfig;

import com.wisely.ad.AdProxy;
import com.wisely.ad.BaseAdLoader;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public abstract class GoogleBannerAdLoader extends BaseAdLoader {
    private final static String LOG_TAG = GoogleBannerAdLoader.class.getSimpleName();

    private AdView adView;

    @Override
    public void load(Callback callback) {
        this.adCallback = callback;
        this.startLoadTime = System.currentTimeMillis();
        this.adView = new AdView(AdProxy.getIns().getApplicationContext());
        this.adView.setAdUnitId(this.adId);
        this.adView.setAdSize(getAdSize());
        this.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int var1) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "onAdFailedToLoad, isHighEcpm: " + isHighEcpm);
                }
                if (adView != null) {
                    adView.setAdListener(null);
                }
                if (adCallback != null) {
                    GoogleBannerAd gad = new GoogleBannerAd(null, isHighEcpm);
                    adCallback.onAdLoadFailed(GoogleBannerAdLoader.this, gad);
                }
                adView = null;
                adCallback = null;
            }

            @Override
            public void onAdLoaded() {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "onAdLoaded, isHighEcpm: " + isHighEcpm);
                }
                if (adView != null) {
                    adView.setAdListener(null);
                }
                if (adCallback != null) {
                    GoogleBannerAd gad = new GoogleBannerAd(adView, isHighEcpm);
                    adCallback.onAdLoaded(GoogleBannerAdLoader.this, gad);
                }
                adView = null;
                adCallback = null;
            }
        });
        this.adView.loadAd(new AdRequest.Builder().build());
    }

    protected AdSize getAdSize() {
        return AdSize.MEDIUM_RECTANGLE;
    }
}
