package raf.rs.voiceservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import raf.rs.userservice.model.Role;

@Getter
@Setter
@Entity
public class VoiceChannelRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voice_channel_id", nullable = false)
    private VoiceChannel voiceChannel;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    private Long permissions;
}
