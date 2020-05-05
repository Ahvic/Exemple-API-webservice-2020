package fr.univ.orleans.webservices.liveserialisation.modele;

public class Views {
    //On définit une hiérarchie de flag
    //Vue de base
    public static interface Id{};

    //Si on veux plus, on hérite
    public static interface MessageComplet extends Id{};
    public static interface UtilisateurComplet extends Id{};
}
