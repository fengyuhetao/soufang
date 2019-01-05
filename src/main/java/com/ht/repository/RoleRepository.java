package com.ht.repository;

import com.ht.entity.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.repository
 * @date 2018-06-27 21:51
 */
public interface RoleRepository extends CrudRepository<Role, Long> {
    List<Role> findRolesByUserId(Long id);
}
