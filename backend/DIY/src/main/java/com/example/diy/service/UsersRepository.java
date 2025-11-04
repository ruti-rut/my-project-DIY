package com.example.diy.service;

import com.example.diy.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users,Long> {
    Users findByUserName(String userName);    // מחזיר Users או null
    Users findByMail(String mail);            // מחזיר Users או null
    boolean existsByUserName(String userName);
    boolean existsByMail(String mail);
}
