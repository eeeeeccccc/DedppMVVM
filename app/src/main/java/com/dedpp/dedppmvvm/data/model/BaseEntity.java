package com.dedpp.dedppmvvm.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * BaseEntity
 * Created by linzhixin on 2017/8/29.
 */

public class BaseEntity<E> {

    private int errorCode;
    private String errorMsg;
    private long requestId;
    @SerializedName("data")
    private E data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

}
