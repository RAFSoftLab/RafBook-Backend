package raf.rs.messagingservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import raf.rs.userservice.model.MyUser;

import java.util.Set;

@Entity
@Getter
@Setter
public class Reaction {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "reaction_users",
            joinColumns = @JoinColumn(name = "reaction_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<MyUser> users;

    @ManyToOne
    private Emote emotes;
}
