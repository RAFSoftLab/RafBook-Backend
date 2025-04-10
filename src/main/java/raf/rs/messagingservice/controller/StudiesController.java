package raf.rs.messagingservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
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
public class StudiesController {

    private StudiesService studiesService;

    @PostMapping
    public ResponseEntity<ResponseMessageDTO> createStudies(@RequestHeader("Authorization") String token,  @RequestBody NewStudiesDTO dto) {
        studiesService.createStudies(dto, token.substring(7));
        return new ResponseEntity<>(new ResponseMessageDTO("You successfully created new Studies"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudiesDTO>> getAllStudies() {
        return new ResponseEntity<>(studiesService.getAllStudies(), HttpStatus.OK);
    }

}
