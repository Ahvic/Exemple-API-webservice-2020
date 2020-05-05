package com.revision.rest.revisiondemosecurity.Config;

import com.revision.rest.revisiondemosecurity.Config.Erreurs.MauvaisTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component      //Obligé pour Autowired
public class JwtTokens {

    private static final long EXPIRATION_TIME = 99999999;
    private static final String PREFIX = "Bearer ";

    @Autowired      //il va se débrouiller pour trouver le bean associé
    private Key secretKey;

    //Ajouter dépendance jwt
    public String genereToken(UserDetails userDetails){
        String login = userDetails.getUsername();
        var roles = userDetails.getAuthorities().stream().map(auth->auth.getAuthority()).collect(Collectors.toList());      //A recopier exactement, trucs compliqués derrière
        Claims claims = Jwts.claims().setSubject(login);
        claims.put("roles", roles);

        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)                            //secretKey doit aussi être accessible pour décoder
                .compact();

        return token;
    }

    public UsernamePasswordAuthenticationToken decodeToken(String token) throws MauvaisTokenException {
        //le token a une entete avec bearer ?
        if(token.startsWith(PREFIX)){
            token = token.replaceFirst(PREFIX, "");
        }

        try{
            Jws<Claims> jwsClaims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            //Si Le token est bon, sinon il lève une erreur
            String login = jwsClaims.getBody().getSubject();
            List<String> roles = jwsClaims.getBody().get("roles", List.class);

            List<SimpleGrantedAuthority> authorities = roles.stream()   //Liste des roles sous la forme d'authorities
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(login, null, authorities);

            return authentication;
        }catch (JwtException e){
            throw new MauvaisTokenException();
        }
    }
}
