package raf.rs.messagingservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import raf.rs.userservice.model.Role;

@Getter
@Setter
@Entity
public class TextChannelRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "text_channel_id", nullable = false)
    private TextChannel textChannel;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    private Long permissions;

}
