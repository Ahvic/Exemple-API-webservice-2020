package com.revision.rest.revisiondemosecurity.Config;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthentificationFilter extends UsernamePasswordAuthenticationFilter {       //met déja en place l'authentification avec username et password

    private JwtTokens jwtTokens;

    public JwtAuthentificationFilter(AuthenticationManager authenticationManager, JwtTokens jwtTokens) {
        setAuthenticationManager(authenticationManager);                                     //demandé par spring
        setFilterProcessesUrl("/api/login");                                                 //change l'url écoutée, n'apparait pas dans le controller
        this.jwtTokens = jwtTokens;
    }

    //Appelé quand l'authentification à réussi
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetails user = (UserDetails)authResult.getPrincipal();
        String token = jwtTokens.genereToken(user);

        response.addHeader(HttpHeaders.AUTHORIZATION,"Bearer " + token);   //Bearer imporant par convention
    }
}
