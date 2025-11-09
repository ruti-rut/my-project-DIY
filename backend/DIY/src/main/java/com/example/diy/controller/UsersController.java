package com.example.diy.controller;

import com.example.diy.DTO.UsersRegisterDTO;
import com.example.diy.Mapper.UsersMapper;
import com.example.diy.model.Challenge;
import com.example.diy.model.Users;
import com.example.diy.service.UsersRepository;
import com.example.diy.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")

public class UsersController {
    UsersRepository usersRepository;
    UsersMapper usersMapper;

    @Autowired
    private UsersService usersService; // 💡 חובה להזריק את השירות

    public UsersController(UsersRepository usersRepository, UsersMapper usersMapper) {
        this.usersRepository = usersRepository;
        this.usersMapper = usersMapper;
    }

//    @PostMapping("/signup")
//    public ResponseEntity<Users> signUp(@Valid @RequestBody UsersRegisterDTO user){
//        //נבדוק ששם המשתמש לא קיים
//        Users u=usersRepository.findByUserName(user.getUserName());
//        if(u!=null)
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//
//        Users newUser = usersMapper.usersRegisterDTOToUsers(user);
//
//        String pass=user.getPassword();//הסיסמא שהמשתמש הכניס - לא מוצפנת
//        newUser.setPassword(new BCryptPasswordEncoder().encode(pass));
//
//        usersRepository.save(newUser);
//        return new ResponseEntity<>(newUser,HttpStatus.CREATED);
//    }
//
















    //    @PostMapping("/signup")
//    public ResponseEntity<?> signUp(@RequestBody UsersRegisterDTO user) { // שינוי ל-<?> לטיפול בשגיאות
//
//        try {
//            // 1. קוראים לשירות לביצוע כל הלוגיקה (בדיקה, הצפנה, שמירה)
//            Users savedUser = usersService.registerNewUser(user);
//
//            // 2. הצלחה: מחזירים CREATED (201)
//            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
//
//        } catch (IllegalArgumentException e) {
//            // 3. כישלון: המשתמש כבר קיים (ה-Service זרק את השגיאה)
//            // מחזירים BAD_REQUEST (400) או CONFLICT (409) עם הודעת שגיאה
//            String errorMessage = e.getMessage(); // "Username already taken"
//            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
//
//        } catch (Exception e) {
//            return new ResponseEntity<>("Internal server error during registration.", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


}
