package raf.rs.messagingservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.rs.messagingservice.dto.NewStudyProgramDTO;
import raf.rs.messagingservice.dto.StudyProgramDTO;
import raf.rs.messagingservice.model.StudyProgram;
import raf.rs.messagingservice.service.StudyProgramService;
import raf.rs.userservice.dto.ResponseMessageDTO;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/study-programs")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class StudyProgramController {

    private StudyProgramService studyProgramService;

    @PostMapping
    public ResponseEntity<ResponseMessageDTO> createStudyProgram(NewStudyProgramDTO dto) {
        studyProgramService.createStudyProgram(dto);
        return new ResponseEntity<>(new ResponseMessageDTO("You successfully created new study program"), HttpStatus.CREATED);
    }

    @GetMapping("/by-studies")
    public ResponseEntity<List<StudyProgramDTO>> getAllStudyProgramsByStudies(@RequestParam(name = "studies", required = true) String studies) {
        return new ResponseEntity<>(studyProgramService.getAllStudyProgramsByStudies(studies), HttpStatus.OK);
    }

}
