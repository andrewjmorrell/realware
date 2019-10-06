package com.pivot.pivot360.content.listeners;

public interface BaseResponseListener {
    void onError(String message);

    void onAuthInfoField(String message);

    void onResponseMessageField(String message);
}
