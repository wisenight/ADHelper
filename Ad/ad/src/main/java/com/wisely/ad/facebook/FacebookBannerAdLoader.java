package com.wisely.ad.facebook;

import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.wisely.ad.BuildConfig;

import com.wisely.ad.AdProxy;
import com.wisely.ad.BaseAdLoader;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public abstract class FacebookBannerAdLoader extends BaseAdLoader {
    private final static String LOG_TAG = FacebookBannerAdLoader.class.getSimpleName();

    @Override
    public void load(Callback callback) {
        this.adCallback = callback;
        this.startLoadTime = System.currentTimeMillis();

        AdView adView = new AdView(AdProxy.getIns().getApplicationContext(), this.adId, getAdSize());
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "onError, isHighEcpm: " + isHighEcpm);
                }
                if (ad != null) {
                    ad.destroy();
                }
                if (ad instanceof AdView) {
                    ((AdView) ad).setAdListener(null);
                }
                if (adCallback != null) {
                    FacebookBannerAd fad = new FacebookBannerAd(null, isHighEcpm);
                    adCallback.onAdLoadFailed(FacebookBannerAdLoader.this, fad);
                }
                adCallback = null;
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "onAdLoaded, isHighEcpm: " + isHighEcpm);
                }
                if (!(ad instanceof AdView)) {
                    if (ad != null) {
                        ad.destroy();
                    }
                    if (adCallback != null) {
                        FacebookBannerAd fad = new FacebookBannerAd(null, isHighEcpm);
                        adCallback.onAdLoadFailed(FacebookBannerAdLoader.this, fad);
                    }
                    adCallback = null;
                    return;
                }
                AdView adv = (AdView) ad;
                adv.setAdListener(null);
                if (adCallback != null) {
                    FacebookBannerAd fad = new FacebookBannerAd(adv, isHighEcpm);
                    adCallback.onAdLoaded(FacebookBannerAdLoader.this, fad);
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
        adView.loadAd();
    }

    protected AdSize getAdSize() {
        return AdSize.BANNER_HEIGHT_50;
    }
}
