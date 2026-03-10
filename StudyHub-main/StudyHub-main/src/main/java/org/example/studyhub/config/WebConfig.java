package org.example.studyhub.config;

import org.example.studyhub.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/users/**"
//                        "/admin/**",
//                        "/courses/**",
//                        "/posts/**",
//                        "/profile",
//                        "/my-courses"
                )
                .excludePathPatterns(
                        "/auth/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/"
                );
    }
}