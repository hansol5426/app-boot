package it.korea.app_boot.common.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import jakarta.servlet.Filter;
import jakarta.servlet.MultipartConfigElement;

@Configuration
public class WebConfig implements WebMvcConfigurer{

    @Value("${server.file.gallery.path}")
    private String filePath;


    // resource 경로 설정
    // 외부경로 내부경로로 돌리기
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/img/**")
            .addResourceLocations("file:" + filePath)
            .setCachePeriod(0)
            .resourceChain(true)
            .addResolver(new PathResourceResolver());
    }
    

    // 문자 인코딩
    @Bean
    public Filter characterEncodingFilter(){
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);

        return encodingFilter;
    }


    // 파일 제한
    @Bean
    public MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 개별파일 사이즈
        factory.setMaxFileSize(DataSize.of(50, DataUnit.MEGABYTES));
        // 전체파일 사이즈
        factory.setMaxRequestSize(DataSize.of(50, DataUnit.MEGABYTES));

        return factory.createMultipartConfig();
    }
}
