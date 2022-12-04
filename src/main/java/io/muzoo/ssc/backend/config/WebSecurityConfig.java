package io.muzoo.ssc.backend.config;


import io.muzoo.ssc.backend.auth.OurUserDetailsService;
import io.muzoo.ssc.backend.util.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OurUserDetailsService ourUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeHttpRequests()
                .antMatchers("/", "/api/login", "/api/logout", "/api/whoami", "/api/getAuditoriums").permitAll();
        http.authorizeHttpRequests().antMatchers(
                        "/api/addMovie",
                        "/api/deleteMovie",
                        "/api/addAuditorium",
                        "/api/deleteAuditorium",
                        "/api/link",
                        "/api/unlink",
                        "/api/clearReservedSeats",
                        "/api/cancelReservedSeats",
                        "/api/setRole",
                        "/api/deleteUser",
                        "/api/getUserByUsername",
                        "/api/getUser",
                        "/api/password"
                )
                .hasRole("ADMIN");
        http.authorizeHttpRequests().antMatchers("/**").hasAnyRole("ADMIN", "USER");

        http.exceptionHandling()
                .authenticationEntryPoint(new JsonHttp403ForbiddenEntryPoint());
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return ourUserDetailsService;
    }

    static class JsonHttp403ForbiddenEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            String ajaxJson = io.muzoo.ssc.backend.util.AjaxUtils.convertToString(
                    ResponseDTO
                            .builder()
                            .success(false)
                            .error("Forbidden")
                            .build());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().println(ajaxJson);
        }
    }
}
