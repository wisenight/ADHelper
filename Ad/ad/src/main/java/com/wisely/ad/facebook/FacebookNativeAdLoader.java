package com.wisely.ad.facebook;

import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.wisely.ad.BuildConfig;

import com.wisely.ad.AdProxy;
import com.wisely.ad.BaseAdLoader;

/**
 * Created by wisely on 2018/1/19.
 *
 */

public abstract class FacebookNativeAdLoader extends BaseAdLoader {

    private final static String LOG_TAG = FacebookNativeAdLoader.class.getSimpleName();

    @Override
    public void load(Callback callback) {
        this.adCallback = callback;
        this.startLoadTime = System.currentTimeMillis();
        NativeAd nativeAd = new NativeAd(AdProxy.getIns().getApplicationContext(), this.adId);
        nativeAd.setAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "onError, isHighEcpm: " + isHighEcpm);
                }
                if (ad != null) {
                    ad.destroy();
                }
                if (ad instanceof NativeAd) {
                    ((NativeAd) ad).setAdListener(null);
                }
                if (adCallback != null) {
                    FacebookNativeAd fad = new FacebookNativeAd(null, isHighEcpm);
                    adCallback.onAdLoadFailed(FacebookNativeAdLoader.this, fad);
                }
                adCallback = null;
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "onAdLoaded, isHighEcpm: " + isHighEcpm);
                }
                if (!(ad instanceof NativeAd)) {
                    if (ad != null) {
                        ad.destroy();
                    }
                    if (adCallback != null) {
                        FacebookNativeAd fad = new FacebookNativeAd(null, isHighEcpm);
                        adCallback.onAdLoadFailed(FacebookNativeAdLoader.this, fad);
                    }
                    adCallback = null;
                    return;
                }
                NativeAd nad = (NativeAd) ad;
                nad.setAdListener(null);
                if (adCallback != null) {
                    FacebookNativeAd fad = new FacebookNativeAd(nad, isHighEcpm);
                    adCallback.onAdLoaded(FacebookNativeAdLoader.this, fad);
                }
                adCallback = null;
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        nativeAd.loadAd();
    }
}
