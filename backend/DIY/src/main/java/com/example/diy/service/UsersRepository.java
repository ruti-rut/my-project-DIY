package com.example.diy.service;

import com.example.diy.model.Users;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users,Long> {
    Users findByUserName(String userName);    // מחזיר Users או null
    Optional<Users> findByMail(String mail);            // מחזיר Users או null
    boolean existsByUserName(String userName);
    boolean existsByMail(String mail);


    @Query("SELECT u FROM Users u WHERE u.userName = :identifier OR u.mail = :identifier")
    Users findByIdentifier(@Param("identifier") String identifier);
}
