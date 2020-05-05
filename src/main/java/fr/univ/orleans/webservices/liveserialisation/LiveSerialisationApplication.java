package fr.univ.orleans.webservices.liveserialisation;

import fr.univ.orleans.webservices.liveserialisation.dto.MessageDTO;
import fr.univ.orleans.webservices.liveserialisation.dto.UtilisateurDTO;
import fr.univ.orleans.webservices.liveserialisation.modele.Message;
import fr.univ.orleans.webservices.liveserialisation.modele.Utilisateur;
import fr.univ.orleans.webservices.liveserialisation.service.Services;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class LiveSerialisationApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiveSerialisationApplication.class, args);
    }

    @Bean
    CommandLineRunner initialisation(Services services) {
        return args -> {
            Utilisateur fred = new Utilisateur("fred","fred", false);
            Message post = new Message(null,"hello world !",fred);
            services.saveUtilisateur(fred);
            services.saveMessage(post);

            services.saveUtilisateur(new Utilisateur("admin","admin", true));
        };
    }

    //Attention, il faut ajouter un import dans le pom.xml
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        //On explique les cas particuliers
        //Quand on passe de message à messageDTO, tu nous prend le login et non un toString de l'objet
        modelMapper.typeMap(Message.class, MessageDTO.class).addMappings(
                mapper -> {
                    mapper.map(src->src.getUtilisateur().getLogin(),MessageDTO::setUtilisateur);
                }
        );

        //Comment convertir la liste de message en liste d'Id
        Converter<List<Message>,List<Long>> toListIds =
                ctx -> ctx.getSource().stream().map(Message::getId).collect(Collectors.toList());
        modelMapper.typeMap(Utilisateur.class, UtilisateurDTO.class).addMappings(
                mapper -> {
                    mapper.using(toListIds).map(Utilisateur::getMessages,UtilisateurDTO::setMessagesIds);
                }
        );

        return modelMapper;
    }
}
