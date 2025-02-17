package raf.rs.messagingservice.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import raf.rs.messagingservice.dto.CategoryDTO;
import raf.rs.messagingservice.dto.NewStudyProgramDTO;
import raf.rs.messagingservice.dto.StudyProgramDTO;
import raf.rs.messagingservice.model.StudyProgram;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class StudyProgramMapper {

    private CategoryMapper categoryMapper;

    public StudyProgram toEntity(NewStudyProgramDTO dto) {
        StudyProgram studyProgram = new StudyProgram();
        studyProgram.setName(dto.getName());
        studyProgram.setDescription(dto.getDescription());

        return studyProgram;
    }

    public StudyProgramDTO toDto(StudyProgram studyProgram) {
        StudyProgramDTO dto = new StudyProgramDTO();

        dto.setId(studyProgram.getId());

        Set<CategoryDTO> categoryDTOSet = studyProgram.getCategories().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toSet());

        dto.setCategories(categoryDTOSet);
        dto.setDescription(studyProgram.getDescription());

        return dto;
    }

}
