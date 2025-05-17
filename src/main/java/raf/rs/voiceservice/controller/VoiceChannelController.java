package raf.rs.voiceservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.rs.userservice.model.MyUser;
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
@Slf4j
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
        log.info("Entering createVoiceChannel with token: {}, dto: {}", token, dto);
        VoiceChannelDTO createdChannel = voiceChannelService.createVoiceChannel(dto, token.substring(7));
        log.info("Exiting createVoiceChannel with result: {}", createdChannel);
        return ResponseEntity.ok(createdChannel);
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
        log.info("Entering findAll with token: {}", token);
        List<VoiceChannelDTO> channels = voiceChannelService.listAll(token.substring(7));
        log.info("Exiting findAll with result: {}", channels);
        return ResponseEntity.ok(channels);
    }

    @Operation(summary = "Find a voice channel by ID", description = "Finds and returns the voice channel with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voice channel found successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoiceChannelDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or voice channel not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<VoiceChannelDTO> findById(@PathVariable String id) {
        log.info("Entering findById with id: {}", id);
        VoiceChannelDTO channel = voiceChannelService.getVoiceChannel(id);
        log.info("Exiting findById with result: {}", channel);
        return ResponseEntity.ok(channel);
    }

    @Operation(summary = "Add user to voice channel", description = "Adds a user to the specified voice channel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User added to voice channel successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or user not found",
                    content = @Content)
    })
    @PostMapping("/add-user/{channelId}")
    public ResponseEntity<ResponseMessageDTO> addUserToVoiceChannel(@PathVariable String channelId, @RequestHeader("Authorization") String token) {
        log.info("Entering addUserToVoiceChannel with channelId: {}, token: {}", channelId, token);
        voiceChannelService.addUserToVoiceChannel(channelId, token.substring(7));
        log.info("Exiting addUserToVoiceChannel: User added successfully");
        return new ResponseEntity<>(new ResponseMessageDTO("User added to voice channel successfully"), HttpStatus.OK);
    }

    @Operation(summary = "Remove user from voice channel", description = "Removes a user from the specified voice channel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User removed from voice channel successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or user not found",
                    content = @Content)
    })
    @DeleteMapping("/remove-user/{channelId}")
    public ResponseEntity<ResponseMessageDTO> removeUserFromVoiceChannel(@PathVariable String channelId, @RequestHeader("Authorization") String token) {
        log.info("Entering removeUserFromVoiceChannel with channelId: {}, token: {}", channelId, token);
        voiceChannelService.removeUserFromVoiceChannel(channelId, token.substring(7));
        log.info("Exiting removeUserFromVoiceChannel: User removed successfully");
        return new ResponseEntity<>(new ResponseMessageDTO("User removed from voice channel successfully"), HttpStatus.OK);
    }

    @Operation(summary = "Get users in voice channel", description = "Gets all users in the specified voice channel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MyUser[].class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or voice channel not found",
                    content = @Content)
    })
    @GetMapping("/users/{channelId}")
    public ResponseEntity<Set<MyUser>> getUsersInVoiceChannel(@PathVariable String channelId) {
        log.info("Entering getUsersInVoiceChannel with channelId: {}", channelId);
        Set<MyUser> users = voiceChannelService.getUsersInVoiceChannel(channelId);
        log.info("Exiting getUsersInVoiceChannel with result: {}", users);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Delete a voice channel", description = "Deletes the voice channel with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voice channel deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input or voice channel not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoiceChannel(@PathVariable String id, @RequestHeader("Authorization") String token) {
        log.info("Entering deleteVoiceChannel with id: {}, token: {}", id, token);
        voiceChannelService.deleteVoiceChannel(id, token.substring(7));
        log.info("Exiting deleteVoiceChannel: Voice channel deleted successfully");
        return ResponseEntity.ok().build();
    }
}