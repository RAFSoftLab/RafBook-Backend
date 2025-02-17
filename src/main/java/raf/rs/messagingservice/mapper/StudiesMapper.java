package raf.rs.messagingservice.mapper;

import org.springframework.stereotype.Component;
import raf.rs.messagingservice.dto.NewStudiesDTO;
import raf.rs.messagingservice.dto.StudiesDTO;
import raf.rs.messagingservice.model.Studies;

@Component
public class StudiesMapper {

    public StudiesDTO toDto(Studies studies) {

        StudiesDTO studiesDTO = new StudiesDTO();
        studiesDTO.setId(studies.getId());
        studiesDTO.setName(studies.getName());
        studiesDTO.setDescription(studies.getDescription());
        studies.setStudyPrograms(studies.getStudyPrograms());

        return studiesDTO;
    }

    public Studies toEntity(NewStudiesDTO dto) {

        Studies studies = new Studies();
        studies.setName(dto.getName());
        studies.setDescription(dto.getDescription());

        return studies;
    }

}
