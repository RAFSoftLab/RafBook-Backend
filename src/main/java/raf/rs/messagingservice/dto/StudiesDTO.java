package raf.rs.messagingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudiesDTO {
    private Long id;
    private String name;
    private String description;
    private Set<StudyProgramDTO> studyPrograms = new HashSet<>();
}
