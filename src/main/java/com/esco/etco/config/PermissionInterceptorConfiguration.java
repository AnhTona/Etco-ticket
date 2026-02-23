package com.esco.etco.config;

import java.util.List;

import com.esco.etco.entity.Permission;
import com.esco.etco.entity.Role;
import com.esco.etco.entity.User;
import com.esco.etco.service.UserService;
import com.esco.etco.util.SecurityUtil;
import com.esco.etco.util.error.PermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    private final UserService userService;

    public PermissionInterceptorConfiguration(UserService userService){
        this.userService = userService;
    }

    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor(this.userService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/", "/api/v1/auth/**", "/storage/**",
                "/api/v1/events/**", "/api/v1/files",
                "/api/v1/genres", "/api/v1/genres/{id}",
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}