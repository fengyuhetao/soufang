package com.ht.base;

import com.ht.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.base
 * @date 2018-07-07 17:24
 */
public class LoginUserUtil {
    public static User load() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal != null && principal instanceof User) {
            return (User) principal;
        }

        return null;
    }

    public static Long getLoginUserId() {
        User user = load();
        if(user == null) {
            return -1L;
        }

        return user.getId();
    }
}
