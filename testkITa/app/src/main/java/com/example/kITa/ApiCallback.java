package com.example.kITa;

public interface ApiCallback<T> {
    void onSuccess(T result);
    void onError(String error);
}
