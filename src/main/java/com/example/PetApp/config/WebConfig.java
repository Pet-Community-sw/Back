package com.example.PetApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/profiles/**")
                .addResourceLocations("file:///Users/choiseonjae/Desktop/PetApp/profiles/");
        registry.addResourceHandler("/image/posts/**")
                .addResourceLocations("file:///Users/choiseonjae/Desktop/PetApp/posts/");
        registry.addResourceHandler("/image/members/**")
                .addResourceLocations("file:///Users/choiseonjae/Desktop/PetApp/members/");
        registry.addResourceHandler("/image/basic/**")
                .addResourceLocations("file:///Users/choiseonjae/Desktop/PetApp/basic/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API
                .allowedOriginPatterns("*") //
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
//        registry.addMapping("/**")
////                .allowedOrigins("http://localhost:8080")
//                .allowedOriginPatterns("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
//                .allowCredentials(true);
    }
}
