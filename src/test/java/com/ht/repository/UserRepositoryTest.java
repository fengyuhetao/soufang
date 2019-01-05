package com.ht.repository;

import com.ht.SoufangApplicationTests;
import com.ht.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.Assert.*;

public class UserRepositoryTest extends SoufangApplicationTests{
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindOne() {
        Optional<User> user = userRepository.findById(1L);
        Assert.assertEquals("waliwali", user.get().getName());
    }
}