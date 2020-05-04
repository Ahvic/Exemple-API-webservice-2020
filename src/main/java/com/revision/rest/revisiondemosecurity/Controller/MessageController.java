package com.revision.rest.revisiondemosecurity.Controller;

import com.revision.rest.revisiondemosecurity.Modele.Utilisateur;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import com.revision.rest.revisiondemosecurity.Modele.Message;

@RestController
@RequestMapping("/api")
public class MessageController {
    private static List<Message> messages = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(1L);  //Facilite l'incrémentation du compteur

    //Moche devrait être dans la façade
    private static Map<String, Utilisateur> utilisateurs = new TreeMap<>();
    public static Map<String, Utilisateur> getUtilisateurs(){
        return utilisateurs;
    }
    static {
        Utilisateur jotaro = new Utilisateur("Jotaro", "Dolphin", false);
        Utilisateur admin = new Utilisateur("admin", "admin", true);
        utilisateurs.put(jotaro.getLogin(), jotaro);
        utilisateurs.put(admin.getLogin(), admin);
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> create(Principal principal, @RequestBody Message message) {      //Principal permet de récupéré l'utilisateur
        String login = principal.getName();

        // il n'a pas d'id, juste un texte
        Message messageRec = new Message( counter.getAndIncrement(), login + ": " + message.getTexte() );
        messages.add(messageRec);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(messageRec.getId()).toUri();

        return ResponseEntity.created(location).body(messageRec);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>>  getAll() {
        return ResponseEntity.ok().body(messages);
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<Message>  findById(@PathVariable("id") Long id) {
        Optional<Message> message = messages.stream().filter(m->m.getId()==id).findAny();
        if (message.isPresent()) {
            return ResponseEntity.ok().body(message.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity  deleteById(@PathVariable("id") Long id) {
        for(int index=0; index<messages.size();index++) {
            if (messages.get(index).getId()==id) {
                messages.remove(index);
                return ResponseEntity.noContent().build();  //Pas de soucisse
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/utilisateurs")
    public ResponseEntity<Utilisateur> enregistrerUtilisateur(@RequestBody Utilisateur utilisateur){
        Predicate<String> isOk = s -> (s!=null)&&(s.length()>=2);
        if(!isOk.test(utilisateur.getLogin()) || !isOk.test(utilisateur.getPassword())){
            return ResponseEntity.badRequest().build();
        }

        if(utilisateurs.containsKey(utilisateur.getLogin())){
            return ResponseEntity.badRequest().build();
        }

        utilisateurs.put(utilisateur.getLogin(), utilisateur);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(utilisateur.getLogin()).toUri();

        return ResponseEntity.created(location).body(utilisateur);
    }

    @GetMapping("/utilisateurs/{login}")
    public ResponseEntity<Utilisateur>  findUtilisateurById(Principal principal, @PathVariable("login") String login) {
        if(!principal.getName().equals(login)){                                     //Un utilisateur ne peut voir que son propre profil, si c'est pas lui on l'envoi bouler.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(utilisateurs.containsKey(login)){
            return ResponseEntity.ok().body(utilisateurs.get(login));
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    //Fait la même chose que celle du dessus
    //Le PreAuthorize vérifie une condition avant de l'éxécuter
    //Demande d'activer les Prepos dans la config avec @EnableGlobalMethodSecurity
    @GetMapping("/utilisateurs2/{login}")
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<Utilisateur>  findUtilisateurById(@PathVariable("login") String login) {
        if(utilisateurs.containsKey(login)){
            return ResponseEntity.ok().body(utilisateurs.get(login));
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}