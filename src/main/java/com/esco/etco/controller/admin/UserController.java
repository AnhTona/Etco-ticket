package com.esco.etco.controller.admin;

import com.esco.etco.entity.User;
import com.esco.etco.service.UserService;
import com.esco.etco.util.error.IdInvalidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // admin only
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok().body(this.userService.getAllUsers());
    }

    // admin only
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.getUserById(id);
        if(user == null){
            throw new IdInvalidException("User không tồn tại");
        }
        return ResponseEntity.ok().body(user);
    }


    // admin only
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) throws IdInvalidException{
        boolean isEmailExist = this.userService.getUserByEmail(user.getEmail());
        // check email is exist or not
        if(isEmailExist){
            throw new IdInvalidException("Email đã tồn tại");
        }
        return ResponseEntity.ok().body(this.userService.createUser(user));
    }

    // user & admin
    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) throws IdInvalidException{
        User updatedUser = this.userService.updateUser(user);
        // check user is exist or not
        if(updatedUser == null){
            throw new IdInvalidException("User với id = " + user.getId() + " không tồn tại");
        }
        return ResponseEntity.ok().body(updatedUser);
    }

    // user & admin
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable long id) throws IdInvalidException{
        User user = this.userService.getUserById(id);
        if(user == null){
            throw new IdInvalidException("User không tồn tại");
        }
        return ResponseEntity.ok(null);
    }
}















