package com.siemens.hbase.hbaseclient.controller.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;


/**
 * @param <T>
 */
@Data
public class ResponseMessage<T> implements Serializable {

    private boolean success;
    private T data;
    private Long timeStamp;
    private int status;

    public ResponseMessage() {
    }

    public ResponseMessage(boolean success, T data, Long timeStamp, int status) {
        this.success = success;
        this.data = data;
        this.timeStamp = timeStamp;
        this.status = status;
    }

    public static <T> ResponseMessage<T> ok(T data) {
        return new ResponseMessage<>(Boolean.TRUE, data, System.currentTimeMillis(), HttpStatus.OK.value());
    }

    public static <T> ResponseMessage<T> error(T message) {
        return new ResponseMessage<>(Boolean.FALSE, message, System.currentTimeMillis(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
