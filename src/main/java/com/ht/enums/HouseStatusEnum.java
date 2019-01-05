package com.ht.enums;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.enums
 * @date 2018-07-07 22:35
 */
public enum  HouseStatusEnum {
    NOT_AUDITED(0, "未审核"),
    PASSES(1, "审核通过"),
    RENTED(2, "已出租"),
    DELETED(3, "逻辑删除");

    private int value;

    private String message;

    HouseStatusEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getValue() {
        return value;
    }
}
