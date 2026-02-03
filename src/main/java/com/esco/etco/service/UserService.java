package com.esco.etco.service;

import com.esco.etco.entity.User;
import com.esco.etco.entity.response.ResCreateUserDTO;
import com.esco.etco.entity.response.ResUpdateUserDTO;
import com.esco.etco.entity.response.ResUserDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    User createUser(User user);

    ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable);

    User getUserById(long id);

    User updateUser(User updateUser);

    void deleteUserById(long id);

    boolean getUserByEmail(String email);

    ResCreateUserDTO convertToResCreateUserDTO(User user);

    ResUpdateUserDTO convertToResUpdateUserDTO(User user);

    ResUserDTO convertToResUserDTO(User user);
}
