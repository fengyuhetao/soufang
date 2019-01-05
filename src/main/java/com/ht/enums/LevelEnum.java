package com.ht.enums;

/**
 * 行政级别定义
 *
 * @author HT
 * @version V1.0
 * @package com.ht.enums
 * @date 2018-07-07 11:25
 */
public enum LevelEnum {
    CITY("city"),
    REGION("region");

    private String value;

    LevelEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LevelEnum of(String value) {
        for (LevelEnum levelEnum : LevelEnum.values()) {
            if(levelEnum.getValue().equals(value)) {
                return levelEnum;
            }
        }

        throw new IllegalArgumentException();
    }
}
