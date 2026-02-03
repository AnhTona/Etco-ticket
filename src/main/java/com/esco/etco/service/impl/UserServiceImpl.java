package com.esco.etco.service.impl;

import com.esco.etco.entity.User;
import com.esco.etco.repository.UserRepository;
import com.esco.etco.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user){
        Optional<User> userOptional = this.userRepository.findById(user.getId());
        if(userOptional.isPresent()){
            return null;
        }
        return this.userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers(){
        return this.userRepository.findAll();
    }

    @Override
    public User getUserById(long id){
        Optional<User> userOptional = this.userRepository.findById(id);
        if(userOptional.isPresent()){
            return userOptional.get();
        }
        return null;
    }

    @Override
    public User updateUser(User updateUser){
        User currentUser = this.getUserById(updateUser.getId());
        if(currentUser != null){
            currentUser.setName(updateUser.getName());
            currentUser.setEmail(updateUser.getEmail());
            currentUser.setAddress(updateUser.getAddress());
            currentUser.setGender(updateUser.getGender());

            return this.userRepository.save(currentUser);
        }
        return null;
    }

    public boolean getUserByEmail(String email){
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public void deleteUserById(long id){
        this.userRepository.deleteById(id);
    }

}
