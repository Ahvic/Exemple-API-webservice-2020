package fr.univ.orleans.webservices.liveserialisation.service;

import fr.univ.orleans.webservices.liveserialisation.modele.Message;
import fr.univ.orleans.webservices.liveserialisation.modele.Utilisateur;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ServicesImpl implements Services {
    // "bd"
    private static Map<Long, Message> messages = new TreeMap<>();
    private static Map<String, Utilisateur> utilisateurs = new TreeMap<>();
    private final AtomicLong counter = new AtomicLong(1L);

    @Override
    public Collection<Message> findAllMessages() {
        return messages.values();
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public Message saveMessage(Message message) {
        if (message.getId()==null) {
            message.setId(counter.getAndIncrement());
        }
        messages.put(message.getId(), message);
        message.getUtilisateur().addMessage(message);
        return message;
    }

    @Override
    public Optional<Utilisateur> findUtilisateurById(String id) {
        return Optional.ofNullable(utilisateurs.get(id));
    }

    @Override
    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        utilisateurs.put(utilisateur.getLogin(),utilisateur);
        return utilisateur;
    }

    @Override
    public Collection<Utilisateur> findAllUtilisateur() {
        return utilisateurs.values();
    }

}
