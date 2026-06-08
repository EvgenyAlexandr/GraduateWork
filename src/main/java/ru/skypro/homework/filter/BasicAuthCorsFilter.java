package ru.skypro.homework.filter;


import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Фильтр CORS для запросов с Basic Auth.
 * <p>
 * Добавляет заголовок {@code Access-Control-Allow-Credentials}, чтобы браузер
 * передавал учётные данные при кросс-доменных запросах к API.
 */
@Component
public class BasicAuthCorsFilter extends OncePerRequestFilter {

    /**
     * Пропускает запрос дальше по цепочке фильтров, дополняя ответ CORS-заголовком.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
