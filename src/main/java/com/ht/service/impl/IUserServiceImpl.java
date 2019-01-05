package com.ht.service.impl;

import com.ht.entity.Role;
import com.ht.entity.User;
import com.ht.repository.RoleRepository;
import com.ht.repository.UserRepository;
import com.ht.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.service.impl
 * @date 2018-06-27 21:30
 */
@Service
public class IUserServiceImpl implements IUserService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User findUserByName(String username) {
        User user =  userRepository.findByName(username);
        if(user == null) {
            return null;
        }

        List<Role> roles = (List<Role>) roleRepository.findRolesByUserId(user.getId());
        if(roles == null || roles.isEmpty()) {
            throw new DisabledException("权限非法");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));

        user.setAuthorityList(authorities);
        return user;
    }
}
