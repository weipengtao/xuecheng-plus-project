package com.xuecheng.content.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    /**
     * 把 JWT 中的 claim 映射成 Spring Security 的权限信息
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> user = jwt.getClaim("user");
            if (user == null) {
                return List.of();
            }

            Object authoritiesObj = user.get("authorities");
            if (!(authoritiesObj instanceof Collection<?> authorities)) {
                return List.of();
            }

            return authorities.stream()
                    .map(Object::toString)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });

        return converter;
    }
}

