package raf.rs.messagingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TextChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String folderId;
    @OneToMany(mappedBy = "textChannel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Message> messages = Set.of();

}
