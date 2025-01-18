package raf.rs.messagingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.rs.messagingservice.model.Category;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudyProgramDTO {
    private Long id;
    private String name;
    private String description;
    private Set<CategoryDTO> categories = new HashSet<>();
}
