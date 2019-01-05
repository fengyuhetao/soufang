package com.ht.base;

import com.ht.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Datatables 相应结构
 * @author HT
 * @version V1.0
 * @package com.ht.base
 * @date 2018-07-07 21:49
 */
public class ApiDataTableResponse extends ApiResponse{
    private int draw;

    private long recordsTotal;

    private long recordsFiltered;

    public ApiDataTableResponse(StatusEnum statusEnum) {
        this(statusEnum.getCode(), statusEnum.getStandardMessage(), null);
    }

    public ApiDataTableResponse(int code, String message, Object data) {
        super(code, message, data);
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
}
