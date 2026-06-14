package ru.skypro.homework.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ru.skypro.homework.constant.ApiConstants;

/**
 * Настройка CORS для фронтенда и раздача загруженных изображений по URL {@code /images/**}.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.base-path:images}")
    private String basePath;

    /**
     * Регистрирует обработчик статики: файлы из {@code app.storage.base-path} доступны по {@code /images/**}.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + Paths.get(basePath).toAbsolutePath().normalize() + "/";
        registry.addResourceHandler("/images/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }

    /**
     * Разрешает кросс-доменные запросы с фронтенда, включая передачу Basic Auth credentials.
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(ApiConstants.CORS_ORIGIN);
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }
}
