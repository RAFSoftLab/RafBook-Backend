package raf.rs.messagingservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.rs.messagingservice.dto.NewStudiesDTO;
import raf.rs.messagingservice.dto.StudiesDTO;
import raf.rs.messagingservice.service.StudiesService;
import raf.rs.userservice.dto.ResponseMessageDTO;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/studies")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class StudiesController {

    private StudiesService studiesService;

    @PostMapping
    public ResponseEntity<ResponseMessageDTO> createStudies(@RequestHeader("Authorization") String token, @RequestBody NewStudiesDTO dto) {
        log.info("Entering createStudies with token: {}, dto: {}", token, dto);
        studiesService.createStudies(dto, token.substring(7));
        ResponseMessageDTO response = new ResponseMessageDTO("You successfully created new Studies");
        log.info("Exiting createStudies with response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudiesDTO>> getAllStudies() {
        log.info("Entering getAllStudies");
        List<StudiesDTO> studies = studiesService.getAllStudies();
        log.info("Exiting getAllStudies with result: {}", studies);
        return new ResponseEntity<>(studies, HttpStatus.OK);
    }
}