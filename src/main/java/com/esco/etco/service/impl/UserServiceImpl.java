package com.esco.etco.service.impl;

import com.esco.etco.entity.Role;
import com.esco.etco.entity.User;
import com.esco.etco.entity.response.ResCreateUserDTO;
import com.esco.etco.entity.response.ResUpdateUserDTO;
import com.esco.etco.entity.response.ResUserDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.repository.UserRepository;
import com.esco.etco.service.FileService;
import com.esco.etco.service.RoleService;
import com.esco.etco.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository,RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Override
    public User createUser(User user){
        Optional<User> userOptional = this.userRepository.findById(user.getId());
        if (user.getRole() != null) {
            Role r = this.roleService.fetchById(user.getRole().getId());
            user.setRole(r != null ? r : null);
        }else {
            Role defaultRole = this.roleService.findByName("CUSTOMER");
            user.setRole(defaultRole);
        }
        return this.userRepository.save(user);
    }

    @Override
    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable){
        Page<User> pageUser = this.userRepository.findAll(spec,pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        resultPaginationDTO.setMeta(mt);

        List<ResUserDTO> listUser = pageUser.getContent().stream()
                .map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(listUser);

        return resultPaginationDTO;
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
            currentUser.setAvatar(updateUser.getAvatar());
            currentUser.setGender(updateUser.getGender());
            currentUser.setAge(updateUser.getAge());

            if (updateUser.getRole() != null) {
                Role r = this.roleService.fetchById(updateUser.getRole().getId());
                currentUser.setRole(r != null ? r : null);
            }

            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public boolean getUserByEmail(String email){
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public void deleteUserById(long id){
        this.userRepository.deleteById(id);
    }

    @Override
    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());

        return res;
    }

    @Override
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setAvatar(user.getAvatar());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());

        return res;
    }

    @Override
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.RoleUser roleUser= new ResUserDTO.RoleUser();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setAvatar(user.getAvatar());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }

        return res;
    }

    public User handleGetUserByUsername(String name){
        return this.userRepository.findByEmail(name);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
