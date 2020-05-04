package com.revision.rest.revisiondemosecurity.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class Security extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()           // Ne sert que pour des démos
                .withUser("fred").password("{noop}fred").roles("USER")          //noop ne met pas d'encoder pour le mdp
                .and()
                .withUser("admin").password("{noop}admin").roles("USER", "ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()                                                               //Enleve une fonct. génante
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/messages").permitAll()       //donne l'acces à tous le monde en GET
                .anyRequest().authenticated()                                               //demande d'être authentifié
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);  //empêche la création de cookies
    }
}
