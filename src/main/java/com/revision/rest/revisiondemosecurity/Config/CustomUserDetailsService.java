package com.revision.rest.revisiondemosecurity.Config;

import com.revision.rest.revisiondemosecurity.Controller.MessageController;
import com.revision.rest.revisiondemosecurity.Modele.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomUserDetailsService implements UserDetailsService {           //Permet de déléger la lecture des utilisateurs à notre guise
    private static final String[] ROLES_ADMIN = {"USER","ADMIN"};
    private static final String[] ROLES_USER = {"USER"};

    @Autowired                                                                  //Va chercher le @Bean qui lui irait partout dans le projet
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Utilisateur utilisateur = MessageController.getUtilisateurs().get(s);               //Passerait sur une bdd sur un vrai service, solution immonde

        if(utilisateur == null){
            throw new UsernameNotFoundException("User " + s + " not found");
        }

        //Permet de convertir notre booléen isAdmin en string utilisable
        String[] roles = utilisateur.isAdmin() ? ROLES_ADMIN : ROLES_USER;

        //Convertie notre utilisateur en UserDetails
        UserDetails userDetails = User.builder()
                .username(utilisateur.getLogin())
                .password(passwordEncoder.encode(utilisateur.getPassword()))            //Mot de passe en clair = caca, on ajoute un encodeur
                .roles(roles)
                .build();

        return userDetails;
    }
}
