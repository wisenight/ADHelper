package com.wisely.ad;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public interface IAdLoader {
    void load(Callback adCallback);
    void loadAdIds();

    interface Callback {
        void onAdLoaded(IAdLoader loader, IAd ad);
        void onAdLoadFailed(IAdLoader loader, IAd ad);
    }
}
