package com.example.demo.config;

import com.example.demo.services.impl.UserDetailsServiceImple;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // Bean para codificar y verificar contraseñas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPin) {
                return String.valueOf(rawPin.toString().hashCode()); // Hash simple para PINs
            }

            @Override
            public boolean matches(CharSequence rawPin, String encodedPin) {
                return encode(rawPin).equals(encodedPin);
            }
        };
    }

    // Proveedor de autenticación que conecta al servicio de usuarios y al codificador
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImple userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // AuthenticationManager usando directamente el AuthenticationProvider
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Deshabilitar CSRF para APIs
                .cors(cors -> cors.disable())   // Deshabilitar CORS temporalmente
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
    // Configuración del filtro de seguridad para proteger rutas y validar JWT
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http,
//                                                   JwtAuthFilter jwtAuthFilter,
//                                                   UserDetailsServiceImple userDetailsService) throws Exception {
//        return http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/api/usuarios/login",
//                                "/api/usuarios/crear",
//                                "/api/test/**"
//                        ).permitAll()
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .authenticationProvider(authenticationProvider(userDetailsService))
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }

}
