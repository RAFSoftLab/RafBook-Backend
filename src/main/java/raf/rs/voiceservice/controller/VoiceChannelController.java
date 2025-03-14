package raf.rs.voiceservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.rs.voiceservice.dto.NewVoiceChannelDTO;
import raf.rs.voiceservice.dto.VoiceChannelDTO;
import raf.rs.voiceservice.service.VoiceChannelService;
import raf.rs.userservice.dto.ResponseMessageDTO;

import java.util.List;
import java.util.Set;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/voice-channel")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class VoiceChannelController {
    private final VoiceChannelService voiceChannelService;

    @Operation(summary = "Create a new voice channel", description = "Creates a new voice channel with the specified details and returns the created channel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voice channel created successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoiceChannelDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or voice channel name not unique",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<VoiceChannelDTO> createVoiceChannel(@RequestHeader("Authorization") String token, @RequestBody NewVoiceChannelDTO dto) {
        return ResponseEntity.ok(voiceChannelService.createVoiceChannel(dto, token.substring(7)));
    }

    @Operation(summary = "Find all voice channels", description = "Finds and returns all voice channels.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voice channels found successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoiceChannelDTO[].class)) }),
            @ApiResponse(responseCode = "400", description = "No voice channels found",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<VoiceChannelDTO>> findAll(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(voiceChannelService.listAll(token));
    }

    @Operation(summary = "Find a voice channel by ID", description = "Finds and returns the voice channel with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voice channel found successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoiceChannelDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or voice channel not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<VoiceChannelDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(voiceChannelService.getVoiceChannel(id));
    }
/*
    @Operation(summary = "Add roles to voice channel",
            description = "Adds roles to a specific voice channel for the user, allowing for role-based access control.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Roles added successfully to the voice channel",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)) }),
            @ApiResponse(responseCode = "400",
                    description = "Invalid token or invalid channel/role data",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Voice channel not found",
                    content = @Content)
    })
    @PutMapping("/add-roles/{id}")
    public ResponseEntity<ResponseMessageDTO> addRolesToVoiceChannel(@RequestHeader("Authorization") String token,
                                                                     @PathVariable("id") Long id,
                                                                     @RequestBody Set<String> roles) {
        voiceChannelService.addRolesToVoiceChannel(token.substring(7), id, roles);
        return new ResponseEntity<>(new ResponseMessageDTO("You successfully added roles to voice channel"), HttpStatus.OK);
    }


    @Operation(summary = "Remove roles from voice channel",
            description = "Removes specified roles from a specific voice channel for the user, adjusting role-based access control.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Roles removed successfully from the voice channel",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)) }),
            @ApiResponse(responseCode = "400",
                    description = "Invalid token or invalid channel/role data",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Voice channel not found",
                    content = @Content)
    })
    @PatchMapping("/remove-roles/{id}")
    public ResponseEntity<ResponseMessageDTO> removeRolesFromVoiceChannel(@RequestHeader("Authorization") String token,
                                                                          @PathVariable("id") Long id,
                                                                          @RequestBody Set<String> roles) {
        voiceChannelService.removeRolesFromVoiceChannel(token.substring(7), id, roles);
        return new ResponseEntity<>(new ResponseMessageDTO("You successfully removed roles from the voice channel"), HttpStatus.OK);
    }

 */

    @Operation(summary = "Delete a voice channel", description = "Deletes the voice channel with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voice channel deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input or voice channel not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoiceChannel(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        voiceChannelService.deleteVoiceChannel(id, token.substring(7));
        return ResponseEntity.ok().build();
    }
}