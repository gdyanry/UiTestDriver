package com.yanry.testdriver.sample.debug;

import com.yanry.testdriver.ui.mobile.base.event.Event;

/**
 * Created by rongyu.yan on 2/16/2017.
 */
public abstract class ForegroundRequest {

    public void send(NetworkState handler, Event inputEvent) {
        // no connection
        onNoConnection(handler, Network.Disconnected, inputEvent);
        // success
        onLoading(handler, Network.Normal, inputEvent);
        onSuccess(handler, Network.Normal, inputEvent);
        // error
        onLoading(handler, Network.Abnormal, inputEvent);
        onError(handler, Network.Abnormal, inputEvent);
    }

    protected abstract void onNoConnection(NetworkState property, Network value, Event event);

    protected abstract void onLoading(NetworkState property, Network value, Event event);

    protected abstract void onSuccess(NetworkState property, Network value, Event event);

    protected abstract void onError(NetworkState property, Network value, Event event);
}
