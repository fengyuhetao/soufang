package com.ht.service;

import com.ht.entity.User;

/**
 *
 * 用户服务
 *
 * @author HT
 * @version V1.0
 * @package com.ht.service
 * @date 2018-06-27 21:29
 */
public interface IUserService {
    User findUserByName(String username);
}
