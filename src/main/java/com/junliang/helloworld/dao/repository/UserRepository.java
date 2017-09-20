package com.junliang.helloworld.dao.repository;

import com.junliang.helloworld.pojo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByName(String username);
}
