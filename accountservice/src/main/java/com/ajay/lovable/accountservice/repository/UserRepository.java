package com.ajay.lovable.accountservice.repository;

import com.ajay.lovable.accountservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String email);
    Optional<User> findByUsernameIgnoreCase(String email);

}
