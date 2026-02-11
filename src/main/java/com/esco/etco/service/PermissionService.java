package com.esco.etco.service;

import com.esco.etco.entity.Permission;
import com.esco.etco.entity.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public interface PermissionService {

    boolean isPermissionExist(Permission p);

    Permission fetchById(long id);

    Permission create(Permission p);

    Permission update(Permission p);

    void delete(long id);

    ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable);

    boolean isSameName(Permission p);
}
