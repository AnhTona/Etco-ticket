package com.esco.etco.config;

import com.esco.etco.entity.Permission;
import com.esco.etco.entity.Role;
import com.esco.etco.entity.User;
import com.esco.etco.service.UserService;
import com.esco.etco.util.SecurityUtil;
import com.esco.etco.util.error.PermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public class PermissionInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public PermissionInterceptor(UserService userService){
        this.userService = userService;
    }

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String httpMethod = request.getMethod();


        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        // check email có rỗng hay không
        if (email.isEmpty()) {
            throw new PermissionException("Không xác định được người dùng.");
        }

        User user = this.userService.handleGetUserByUsername(email);
        // User bị xóa nhưng JWT còn valid thì chặn
        if (user == null) {
            throw new PermissionException("Người dùng không tồn tại.");
        }

        Role role = user.getRole();
        if (role == null) {
            throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
        }

        List<Permission> permissions = role.getPermissions();
        boolean isAllow = permissions.stream()
                .anyMatch(item -> item.getApiPath().equals(path)
                        && item.getMethod().equals(httpMethod));

        if (!isAllow) {
            throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
        }

        return true;
    }
}

