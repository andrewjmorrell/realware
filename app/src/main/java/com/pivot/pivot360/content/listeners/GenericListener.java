package com.pivot.pivot360.content.listeners;


public interface GenericListener<T> extends BaseResponseListener {
    void OnResults(T response);

}
