package com.esco.etco.service;

import com.esco.etco.entity.Role;
import com.esco.etco.entity.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {

    boolean existByName(String name);

    Role create(Role r);

    Role fetchById(long id);

    Role update(Role r);

    void delete(long id);

    ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable);
}
