package com.wisely.ad;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public abstract class BaseAdLoader implements IAdLoader {

    protected IAdLoader.Callback adCallback = null;
    protected boolean isHighEcpm = false;
    protected String adId = "";
    protected long startLoadTime = 0L;

    public BaseAdLoader() {
        loadAdIds();
    }

    protected abstract String getAdCategory();
}
