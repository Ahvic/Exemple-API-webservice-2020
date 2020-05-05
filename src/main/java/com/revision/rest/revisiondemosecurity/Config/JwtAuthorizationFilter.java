package com.revision.rest.revisiondemosecurity.Config;

import com.revision.rest.revisiondemosecurity.Config.Erreurs.MauvaisTokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private JwtTokens jwtTokens;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtTokens jwtTokens) {
        super(authenticationManager);
        this.jwtTokens = jwtTokens;
    }

    //Appelé à chaque requête
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        UsernamePasswordAuthenticationToken authentication = null;

        //On teste pas si c'est en permitAll
        if(token == null){
            SecurityContextHolder.clearContext();
        }else{
            try {
                //On essaye de décoder le token
                authentication = jwtTokens.decodeToken(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (MauvaisTokenException e) {
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }
}
