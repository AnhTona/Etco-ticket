package com.esco.etco.repository;

import com.esco.etco.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {
    User findByEmail(String name);

    boolean existsByEmail(String email);

    User findByRefreshTokenAndEmail(String token, String email);
}
