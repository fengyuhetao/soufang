package com.ht.web.dto;

import lombok.Data;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.web.dto
 * @date 2018-07-06 20:30
 */
@Data
public final class QiNiuPutRet {
    public String key;

    public String hash;

    public String bucket;

    public int width;

    public int height;
}
