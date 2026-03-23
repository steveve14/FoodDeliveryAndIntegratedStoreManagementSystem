package com.fooddelivery.delivery.models;

public class ApiResponse<T> {
    private boolean success;
    private int code;
    private String message;
    private T data;

    public boolean isSuccess() { return success; }
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
