package com.revision.rest.revisiondemosecurity.Config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity                                              //Obligatoire !
@EnableGlobalMethodSecurity(prePostEnabled = true)              //active les Pre et post conditions
public class Security extends WebSecurityConfigurerAdapter {

    /*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()           // Ne sert que pour des démos
                .withUser("fred").password("{noop}fred").roles("USER")          //noop ne met pas d'encoder pour le mdp
                .and()
                .withUser("admin").password("{noop}admin").roles("USER", "ADMIN");
    }
    */

    @Bean                                                       //Utilisé partout, permet d'être retrouvé
    @Override
    protected UserDetailsService userDetailsService() {         //Même chose que configure(AuthenticationManagerBuilder) mais incompatible entre elle
        return new CustomUserDetailsService();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()                                                               //Enleve une fonct. génante
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/messages").permitAll()       //donne l'acces à tous le monde en GET
                .antMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")      //demande le role admin pour faire un DELETE
                .antMatchers(HttpMethod.POST, "/api/utilisateurs").hasRole("ADMIN")     //Seul un admin peut créer un utilisateur
                .anyRequest().hasRole("USER")
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);  //empêche la création de cookies
    }

    @Bean                                                       //Permet d'être retrouvé de partout, permet que tout le modn eutilise le même encodeur
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
