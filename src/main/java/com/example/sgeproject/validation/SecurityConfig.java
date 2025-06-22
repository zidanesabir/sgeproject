//package com.example.sgeproject.validation;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // Keep disabled for development, enable for production
//
//                .authorizeHttpRequests(authorize -> authorize
//                        // --- Publicly accessible paths (no authentication required) ---
//                        .requestMatchers(
//                                "/",              // Allow access to the root URL (will be handled by UtilisateurController)
//                                "/login",         // Allow access to the login page
//                                "/css/**",
//                                "/js/**",
//                                "/images/**",
//                                "/webjars/**"
//                        ).permitAll()
//
//                        // --- Role-based access rules ---
//                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
//                        .requestMatchers("/professor/**").hasAnyAuthority("PROFESSOR", "ADMIN")
//                        .requestMatchers("/etudiant/**").hasAnyAuthority("ETUDIANT", "ADMIN")
//
//                        // --- All other requests require authentication ---
//                        .anyRequest().authenticated()
//                )
//
//                // Configure the login form.
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/dashboard", true) // Redirect authenticated users to /dashboard
//                        .permitAll()
//                )
//
//                // Configure logout behavior.
//                .logout(logout -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                        .logoutSuccessUrl("/login?logout")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                );
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}