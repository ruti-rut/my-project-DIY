package com.example.diy.controller;

import com.example.diy.DTO.UserLogInDTO;
import com.example.diy.DTO.UsersRegisterDTO;
import com.example.diy.Mapper.UsersMapper;
import com.example.diy.model.AuthProvider;
import com.example.diy.model.Users;
import com.example.diy.service.UsersRepository;
import com.example.diy.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/autho")
@CrossOrigin

public class AuthController {
    UsersRepository usersRepository;
    UsersMapper usersMapper;
    private final PasswordEncoder passwordEncoder; // הוספנו את ה-PasswordEncoder

    public AuthController(PasswordEncoder passwordEncoder, UsersMapper usersMapper, UsersRepository usersRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usersMapper = usersMapper;
        this.usersRepository = usersRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<Users> signUp(@Valid @RequestBody UsersRegisterDTO user){
        //נבדוק ששם המשתמש לא קיים
        Users u=usersRepository.findByUserName(user.getUserName());
        if(u!=null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Users newUser = usersMapper.usersRegisterDTOToUsers(user);

        String pass=user.getPassword();//הסיסמא שהמשתמש הכניס - לא מוצפנת
        newUser.setPassword(new BCryptPasswordEncoder().encode(pass));

        usersRepository.save(newUser);
        return new ResponseEntity<>(newUser,HttpStatus.CREATED);
    }

    // --- 2. כניסת משתמש (Login) ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLogInDTO loginRequest) {

        Users user = usersRepository.findByIdentifier(loginRequest.getIdentifier());

        if (user == null || user.getProvider() != AuthProvider.LOCAL) {
            return new ResponseEntity<>(Map.of("error", "שם משתמש או סיסמה שגויים."), HttpStatus.UNAUTHORIZED);
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

            // הצלחה - מחזירים DTO ללא סיסמה
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            // סיסמה לא תואמת
            return new ResponseEntity<>(Map.of("error", "שם משתמש או סיסמה שגויים."), HttpStatus.UNAUTHORIZED);
        }
    }



}
