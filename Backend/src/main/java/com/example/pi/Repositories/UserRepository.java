package com.example.pi.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pi.Models.ERole;
import com.example.pi.Models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    User findByUsernameLike(String username);
    Boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.role = :role1 OR u.role = :role2")
    List<User> findByRole(@Param("role1") ERole role1, @Param("role2") ERole role2);

    Optional<Object> findByEmail(String email);

    Optional<User> findByTel(String tel);
}
