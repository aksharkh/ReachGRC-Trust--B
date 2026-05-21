package com.example.ReachGRC_Trust__B.repository;


import com.example.ReachGRC_Trust__B.dtos.AdminUserDto.LoginDto.LoginRequest;
import com.example.ReachGRC_Trust__B.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByEmail(String email);
    Boolean existsByEmail(String  email);

    @Query("SELECT u.email, u.password from AdminUser u where u.email =:email")
    Optional<LoginRequest> logIn(@Param("email") String email);


    void deleteByEmail(String email);
}
