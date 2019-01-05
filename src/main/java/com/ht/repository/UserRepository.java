package com.ht.repository;

import com.ht.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.repository
 * @date 2018-06-24 16:30
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByName(String username);
}
