package com.ht.enums;

import lombok.Data;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.enums
 * @date 2018-06-25 21:17
 */
public enum  StatusEnum {
    SUCCESS(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Unknown Internal Error"),
    NOT_VALID_PARAM(40005, "Not Valid Params"),
    NOT_SUPPORTED_OPERATION(40005, "Not Supported Operation"),
    NOT_LOGIN(5000, "Not Login"),
    NOT_FOUND(404, "Not Found");

    private int code;

    private String standardMessage;

    StatusEnum(int code, String standardMessage) {
        this.code = code;
        this.standardMessage = standardMessage;
    }

    public int getCode() {
        return code;
    }

    public String getStandardMessage() {
        return standardMessage;
    }
}
