package com.PoolingAPI.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.NotBlank;   
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "status_code",
        "message",
        "error_message"
})
public class ResponseFormat {

    @NotBlank
    private String status;
    @NotBlank
    private Integer status_code;
    private String message;
    private String error_message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStatus_code() {
        return status_code;
    }

    public void setStatus_code(Integer status_code) {
        this.status_code = status_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public Map<String, Object> toResult() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("status_code",status_code);
        response.put("message",message);
        response.put("error_message",error_message);
        return response;
    }
}
