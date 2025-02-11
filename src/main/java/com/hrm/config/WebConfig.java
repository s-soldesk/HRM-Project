package com.hrm.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/*import com.hrm.interceptor.LoginCheckInterceptor;*/

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.dir}")
    private String uploadDir;

	/*
	 * @Autowired private LoginCheckInterceptor loginCheckInterceptor;
	 * 
	 * @Override public void addInterceptors(InterceptorRegistry registry) {
	 * registry.addInterceptor(loginCheckInterceptor) .addPathPatterns("/**")
	 * .excludePathPatterns("/css/**", "/js/**", "/images/**", "/error",
	 * "/uploads/**"); }
	 */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드된 이미지를 위한 설정
        String uploadPath = uploadDir;
        File directory = new File(uploadPath);
        String absolutePath = directory.getAbsolutePath();
        String urlPath = "file:///" + absolutePath.replace("\\", "/") + "/";
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(urlPath)
                .setCacheControl(CacheControl.noCache());
                
        // 정적 리소스를 위한 설정
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCacheControl(CacheControl.noCache());
                
        log.info("Static resource handler configured for /images/**");
        log.info("Upload resource handler configured for: {}", urlPath);
    }

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Upload directory created at: {}", uploadPath);
            }
            
            // 디렉토리 권한 확인
            if (Files.isWritable(uploadPath)) {
                log.info("Upload directory is writable: {}", uploadPath);
            } else {
                log.warn("Upload directory is not writable: {}", uploadPath);
            }
            
            log.info("Upload directory initialized at: {}", uploadPath);
        } catch (Exception e) {
            log.error("Failed to initialize upload directory: {}", e.getMessage(), e);
        }
    }
}