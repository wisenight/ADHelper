package com.wisely.ad;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public interface IAdDisplayCallback {
    void onShow(IAd ad);
    void onClick(IAd ad);
    void onClose(IAd ad);
}
