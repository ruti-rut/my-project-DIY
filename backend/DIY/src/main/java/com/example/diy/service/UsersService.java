package com.example.diy.service;

import com.example.diy.DTO.UsersRegisterDTO;
import com.example.diy.Mapper.UsersMapper;
import com.example.diy.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired // 💡 חובה להזריק את ה-Mapper!
    private UsersMapper usersMapper; // או איך שלא קראת לממשק המאפפר שלך// *** מזריקים את ה-Bean שהגדרנו! ***

    //

    public Users registerNewUser(UsersRegisterDTO registerDTO){
        Users existingUser = usersRepository.findByUserName(registerDTO.getUserName());
        // 1. בדיקת קיום המשתמש (חובה!)
        if (existingUser != null) {
            throw new  IllegalArgumentException("Username already taken");
        }

        // 2. מיפוי DTO ל-Entity
        Users newUser = usersMapper.usersRegisterDTOToUsers(registerDTO);

        // 3. הצפנת הסיסמה (שימוש ב-Bean המוזרק)
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        newUser.setPassword(encodedPassword);

        // 4. שמירת המשתמש בבסיס הנתונים
        return usersRepository.save(newUser);
    }
}