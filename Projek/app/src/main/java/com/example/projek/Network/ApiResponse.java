package com.example.projek.Network;

// Kelas generik untuk membungkus respons API
public class ApiResponse<T> {
    private T data;
    private String error;
    private boolean isSuccessful;

    public ApiResponse(T data, String error, boolean isSuccessful) {
        this.data = data;
        this.error = error;
        this.isSuccessful = isSuccessful;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}