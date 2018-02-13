package com.wisely.ad;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wisely on 2018/1/16.
 *
 */

public abstract class AdHelper implements IAdLoader.Callback {

    private final static String LOG_TAG = AdHelper.class.getSimpleName();

    private final static int MAX_CACHE = 1;
    private final static int MSG_GET_AD_TIMEOUT = 5000;

    private List<IAd> highEcpmCacheList = new ArrayList<>();
    private List<IAd> lowEcpmCacheList = new ArrayList<>();

    protected IAdLoader[] highEcpmLoaders = null;
    protected IAdLoader[] lowEcpmLoaders = null;

    private IAdCallback adCallback;

    private Handler uiHandler;

    protected AdHelper() {
        uiHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_GET_AD_TIMEOUT: {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "MSG_GET_AD_TIMEOUT");
                        }
                        notifyCallback();
                        break;
                    }
                    default:
                        return false;
                }
                return true;
            }
        });
        initLoaders();
    }

    public void getAD(long timeout, @NonNull IAdCallback callback) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "getAD, timeout: " + timeout + ", callback: " + callback + ", " + this);
        }
        synchronized (this) {
            filterAdList();
            notifyCallback();
            this.adCallback = callback;
            if (timeout == 0 || this.highEcpmCacheList.size() > 0) {
                notifyCallback();
                return;
            }
            uiHandler.sendEmptyMessageDelayed(MSG_GET_AD_TIMEOUT, timeout);
        }
        requestADAsyncIfNeed();
    }

    public void requestADAsyncIfNeed() {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "requestADAsyncIfNeed: " + this);
        }
        synchronized (this) {
            if (this.highEcpmCacheList.size() >= MAX_CACHE) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "highEcpmCacheList >= MAX_CACHE");
                }
                return;
            }
            startLoaders(this.lowEcpmCacheList.size() == 0);
        }
    }

    public void pushAD(IAd ad) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "pushAD: " + this);
        }
        if (ad == null || !ad.isValid()) {
            if (ad != null) {
                ad.destroy();
            }
            return;
        }
        synchronized (this) {
            if (ad.isHighEcpm()) {
                this.highEcpmCacheList.add(ad);
            } else {
                this.lowEcpmCacheList.add(ad);
            }
        }
    }

    public boolean isLoading() {
        boolean result = false;
        synchronized (this) {
            if (this.highEcpmLoaders != null) {
                for (IAdLoader loader : this.highEcpmLoaders) {
                    if (loader != null) {
                        result = true;
                        break;
                    }
                }
            }
            if (!result && this.lowEcpmLoaders != null) {
                for (IAdLoader loader : this.lowEcpmLoaders) {
                    if (loader != null) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    protected abstract void initLoaders();

    protected abstract void startLoaders(boolean includeLowEcpm);

    @Override
    public void onAdLoaded(IAdLoader loader, IAd ad) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "onAdLoaded: " + this);
        }
        synchronized (this) {
            removeFinishLoader(loader);
            boolean isHighEcpm = false;
            if (ad != null) {
                if (ad.isHighEcpm()) {
                    isHighEcpm = true;
                    this.highEcpmCacheList.add(ad);
                } else {
                    this.lowEcpmCacheList.add(ad);
                }
            }
            if (isHighEcpm || isAllLoaderFinished()) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "isAllLoaderFinished: " + this);
                }
                filterAdList();
                notifyCallback();
            }
        }
    }

    @Override
    public void onAdLoadFailed(IAdLoader loader, IAd ad) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "onAdLoadFailed: " + this);
        }
        if (ad != null) {
            ad.destroy();
        }
        synchronized (this) {
            removeFinishLoader(loader);
            if (isAllLoaderFinished()) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "isAllLoaderFinished: " + this);
                }
                filterAdList();
                notifyCallback();
            }
        }
    }

    private void removeFinishLoader(IAdLoader loader) {
        if (loader == null) {
            return;
        }
        synchronized (this) {
            if (this.highEcpmLoaders != null) {
                int size = this.highEcpmLoaders.length;
                for (int i = 0; i < size; i++) {
                    if (this.highEcpmLoaders[i] == loader) {
                        this.highEcpmLoaders[i] = null;
                        break;
                    }
                }
            }
            if (this.lowEcpmLoaders != null) {
                int size = this.lowEcpmLoaders.length;
                for (int i = 0; i < size; i++) {
                    if (this.lowEcpmLoaders[i] == loader) {
                        this.lowEcpmLoaders[i] = null;
                        break;
                    }
                }
            }
        }
    }

    private boolean isAllLoaderFinished() {
        if (this.highEcpmLoaders != null) {
            for (IAdLoader loader : this.highEcpmLoaders) {
                if (loader != null) {
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "isAllLoaderFinished: " + false + ", has high");
                    }
                    return false;
                }
            }
        }
        if (this.lowEcpmLoaders != null) {
            for (IAdLoader loader : this.lowEcpmLoaders) {
                if (loader != null) {
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "isAllLoaderFinished: " + false + ", has low");
                    }
                    return false;
                }
            }
        }
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "isAllLoaderFinished: " + true);
        }
        return true;
    }

    private void filterAdList() {
        synchronized (this) {
            int size = this.lowEcpmCacheList.size();
            for (int i = 0; i < size; i++) {
                IAd ad = this.lowEcpmCacheList.get(i);
                if (!ad.isValid()) {
                    this.lowEcpmCacheList.remove(i);
                    ad.destroy();
                    size--;
                    i--;
                }
            }

            size = this.highEcpmCacheList.size();
            for (int i = 0; i < size; i++) {
                IAd ad = this.highEcpmCacheList.get(i);
                if (!ad.isValid()) {
                    this.highEcpmCacheList.remove(i);
                    ad.destroy();
                    size--;
                    i--;
                }
            }
        }
    }

    private void notifyCallback() {
        IAdCallback callback = null;
        IAd ad = null;
        synchronized (this) {
            uiHandler.removeMessages(MSG_GET_AD_TIMEOUT);
            if (adCallback != null ) {
                callback = adCallback;
                adCallback = null;
                if (highEcpmCacheList.size() > 0) {
                    ad = highEcpmCacheList.remove(0);
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "notifyCallback, has high ad");
                    }
                } else if (lowEcpmCacheList.size() > 0) {
                    ad = lowEcpmCacheList.remove(0);
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "notifyCallback, has low ad");
                    }
                }
            }
        }
        if (callback != null) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "notifyCallback: " + this + ", ad: " + ad);
            }
            uiHandler.post(new NotifyCallbackRunnable(callback, ad));
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "call bak null");
            }
        }
    }

    static class NotifyCallbackRunnable implements Runnable {

        private IAdCallback callback = null;
        private IAd ad = null;

        NotifyCallbackRunnable(IAdCallback callback, IAd ad) {
            this.callback = callback;
            this.ad = ad;
        }

        @Override
        public void run() {
            if (this.callback != null) {
                if (this.ad != null) {
                    this.callback.onAdLoaded(ad);
                } else {
                    this.callback.onAdLoadFailed(null);
                }
            } else {
                if (this.ad != null) {
                    this.ad.destroy();
                }
            }
            this.callback = null;
            this.ad = null;
        }
    }
}
