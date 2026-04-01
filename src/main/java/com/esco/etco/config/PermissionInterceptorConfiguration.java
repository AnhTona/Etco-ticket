package com.esco.etco.config;

import com.esco.etco.service.UserService;
import com.esco.etco.util.constant.ApiPaths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                "/",
                ApiPaths.AUTH_API + "/**",
                "/storage/**",
                "/api/v1/files",
                "/api/v1/events/**",
                "/api/v1/tickets/**",
                "/api/v1/genres/**",
                ApiPaths.CLIENT_AI_API + "/**",
                ApiPaths.SEATS_API + "/event/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}
