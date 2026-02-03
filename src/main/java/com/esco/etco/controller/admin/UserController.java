package com.esco.etco.controller.admin;

import com.esco.etco.entity.User;
import com.esco.etco.entity.response.ResCreateUserDTO;
import com.esco.etco.entity.response.ResUpdateUserDTO;
import com.esco.etco.entity.response.ResUserDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.service.UserService;
import com.esco.etco.util.annotation.ApiMessage;
import com.esco.etco.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    @ApiMessage("Get all user")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(@Filter Specification<User> spec, Pageable pageable){
        return ResponseEntity.ok().body(this.userService.getAllUsers(spec,pageable));
    }

    // admin only
    @GetMapping("/users/{id}")
    @ApiMessage("Get user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User getUser = this.userService.getUserById(id);
        if(getUser == null){
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.userService.convertToResUserDTO(getUser));
    }


    // admin only
    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<ResCreateUserDTO> createUser(@RequestBody User user) throws IdInvalidException{
        boolean isEmailExist = this.userService.getUserByEmail(user.getEmail());
        // check email is exist or not
        if(isEmailExist){
            throw new IdInvalidException("Email đã tồn tại");
        }
        User createdUser = this.userService.createUser(user);
        return ResponseEntity.ok().body(this.userService.convertToResCreateUserDTO(createdUser));
    }

    // user & admin
    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException{
        User updatedUser = this.userService.updateUser(user);
        // check user is exist or not
        if(updatedUser == null){
            throw new IdInvalidException("User với id = " + user.getId() + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.userService.convertToResUpdateUserDTO(updatedUser));
    }

    // user & admin
    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUserById(@PathVariable long id) throws IdInvalidException{
        User user = this.userService.getUserById(id);
        if(user == null){
            throw new IdInvalidException("User không tồn tại");
        }
        this.userService.deleteUserById(id);
        return ResponseEntity.ok(null);
    }
}















