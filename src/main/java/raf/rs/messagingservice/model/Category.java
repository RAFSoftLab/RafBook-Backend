package raf.rs.messagingservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import raf.rs.voiceservice.model.VoiceChannel;

import java.util.Set;

@Entity
@Getter
@Setter
public class Category {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;
    private String description;

    @OneToMany
    private Set<TextChannel> textChannels;

    @OneToMany
    private Set<VoiceChannel> voiceChannels;

}
