package com.siemens.hbase.hbaseclient.exception;

import com.siemens.hbase.hbaseclient.controller.response.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @author: zhongxp
 * @Date: 7/24/2020 5:29 PM
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @SuppressWarnings({"all"})
    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseMessage<String> exceptionHandle(HttpServletRequest request, Exception e) {
        String url = request.getRequestURL().toString();
        log.error("Global exception handler: " + url +
                ", error message: " + e.getMessage() + ", cause by: " + e.getCause());
        return ResponseMessage.error(e.getMessage());
    }
}
