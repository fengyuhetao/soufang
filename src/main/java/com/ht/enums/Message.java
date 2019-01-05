package com.ht.enums;

import lombok.Data;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.enums
 * @date 2018-07-07 13:07
 */
public enum Message {
    NOT_FOUND("Not Found Resource!"),
    NOT_LOGIN("User not login!");

    private String value;

    Message(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
