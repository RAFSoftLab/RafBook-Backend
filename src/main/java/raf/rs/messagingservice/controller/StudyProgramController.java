package raf.rs.messagingservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.rs.messagingservice.dto.NewStudyProgramDTO;
import raf.rs.messagingservice.dto.StudyProgramDTO;
import raf.rs.messagingservice.service.StudyProgramService;
import raf.rs.userservice.dto.ResponseMessageDTO;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/study-programs")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class StudyProgramController {

    private StudyProgramService studyProgramService;

    @PostMapping
    public ResponseEntity<ResponseMessageDTO> createStudyProgram(@RequestBody NewStudyProgramDTO dto) {
        log.info("Entering createStudyProgram with dto: {}", dto);
        studyProgramService.createStudyProgram(dto);
        ResponseMessageDTO response = new ResponseMessageDTO("You successfully created new study program");
        log.info("Exiting createStudyProgram with response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/by-studies")
    public ResponseEntity<List<StudyProgramDTO>> getAllStudyProgramsByStudies(@RequestParam(name = "studies", required = true) String studies) {
        log.info("Entering getAllStudyProgramsByStudies with studies: {}", studies);
        List<StudyProgramDTO> studyPrograms = studyProgramService.getAllStudyProgramsByStudies(studies);
        log.info("Exiting getAllStudyProgramsByStudies with result: {}", studyPrograms);
        return new ResponseEntity<>(studyPrograms, HttpStatus.OK);
    }

}