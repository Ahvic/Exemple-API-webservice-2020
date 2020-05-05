package fr.univ.orleans.webservices.liveserialisation.service;

import fr.univ.orleans.webservices.liveserialisation.modele.Message;
import fr.univ.orleans.webservices.liveserialisation.modele.Utilisateur;

import java.util.Collection;
import java.util.Optional;

public interface Services {
    Collection<Message> findAllMessages();

    Optional<Message> findMessageById(long id);

    Message saveMessage(Message message);

    Optional<Utilisateur> findUtilisateurById(String id);

    Utilisateur saveUtilisateur(Utilisateur utilisateur);

    Collection<Utilisateur> findAllUtilisateur();
}
