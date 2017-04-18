/**
 *
 */
package com.yanry.testdriver.ui.mobile.model.base;

/**
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
@Presentable
public abstract class ObjectProperty<V> {
    private V cacheValue;
    private boolean needVerification;

    public ObjectProperty(boolean needVerification) {
        this.needVerification = needVerification;
    }

    public V getCurrentValue() {
        if (cacheValue == null) {
            cacheValue = checkValue(Timing.IMMEDIATELY);
        }
        return cacheValue;
    }

    public boolean isNeedVerification() {
        return needVerification;
    }

    public void setCacheValue(V cacheValue) {
        this.cacheValue = cacheValue;
    }

    public boolean transitTo(Graph graph, V toValue, boolean isTransitEvent) {
        return graph.transitToState(this, v -> toValue.equals(v), isTransitEvent);
    }

    public abstract V checkValue(Timing timing);

}
