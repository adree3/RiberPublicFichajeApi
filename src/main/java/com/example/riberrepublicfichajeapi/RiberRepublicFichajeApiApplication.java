package com.example.riberrepublicfichajeapi;

//import com.example.riberrepublicfichajeapi.security.JwtAuthenticationFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@SpringBootApplication
public class RiberRepublicFichajeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiberRepublicFichajeApiApplication.class, args);
    }

//    private static final String[] AUTH_WHITELIST = {
//            "/v2/api-docs",
//            "/v3/api-docs/**",
//            "/swagger-resources",
//            "/swagger-resources/**",
//            "/swagger-ui/**",
//            "/swagger-ui.html",
//            "/configuration/ui",
//            "/configuration/security",
//            "/webjars/**",
//            "/doc/**"
//    };

//    @EnableWebSecurity
//    @Configuration
//    class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http.csrf().disable()
//                    .addFilterAfter(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
//                    .authorizeRequests()
//                    //.antMatchers(HttpMethod.GET,"/api/hoteles/buscarLocCat").permitAll()
//
//                    .antMatchers(AUTH_WHITELIST).permitAll()
//                    .anyRequest().permitAll();
//
//        }

    //}

}
