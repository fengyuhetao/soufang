package com.ht.base;

import com.ht.enums.StatusEnum;
import javafx.beans.binding.ObjectExpression;
import lombok.Data;
import org.springframework.validation.ObjectError;

/**
 * API 格式封装
 *
 * @author HT
 * @version V1.0
 * @package com.ht.base
 * @date 2018-06-25 21:12
 */
@Data
public class ApiResponse {
    private int code;

    private String message;

    private Object data;

    private boolean more;

    public static ApiResponse ofMessage(int code, String message) {
        return new ApiResponse(code, message, null);
    }

    public static ApiResponse ofSuccess(Object data) {
        return new ApiResponse(StatusEnum.SUCCESS.getCode(), StatusEnum.SUCCESS.getStandardMessage(), data);
    }

    public static ApiResponse ofStatus(StatusEnum statusEnum) {
        return new ApiResponse(statusEnum.getCode(), statusEnum.getStandardMessage());
    }

    public ApiResponse(int value, ObjectError objectError, Object data) {
        this.code = StatusEnum.SUCCESS.getCode();
        this.message = StatusEnum.SUCCESS.getStandardMessage();
    }

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
