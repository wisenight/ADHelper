package com.wisely.ad;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public interface IAdCallback {
    void onAdLoaded(IAd ad);
    void onAdLoadFailed(IAd ad);
}
