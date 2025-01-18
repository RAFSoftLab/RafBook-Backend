package raf.rs.messagingservice.controller;

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
import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.StudiesDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelPermissionDTO;
import raf.rs.messagingservice.service.TextChannelService;
import raf.rs.orchestration.service.OrchestrationService;

import java.util.List;
import java.util.Set;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/text-channel")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class TextChannelController {
    private TextChannelService textChannelService;
    private OrchestrationService orchestrationService;

    @Operation(summary = "Create a new text channel", description = "Creates a new text channel with the specified details and returns the created channel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Text channel created successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TextChannelDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or text channel name not unique",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<TextChannelDTO> createTextChannel(@RequestBody NewTextChannelDTO dto) {
        return ResponseEntity.ok(textChannelService.createTextChannel(dto));
    }

    @Operation(summary = "Find all text channels", description = "Finds and returns all text channels.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Text channels found successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TextChannelDTO[].class)) }),
            @ApiResponse(responseCode = "400", description = "No text channels found",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<TextChannelDTO>> findAll() {
        return ResponseEntity.ok(textChannelService.findAll());
    }

    @Operation(summary = "Find a text channel by ID", description = "Finds and returns the text channel with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Text channel found successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TextChannelDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or text channel not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TextChannelDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(textChannelService.findById(id));
    }

    @Operation(summary = "Find text channels for user",
            description = "Finds and returns all text channels the user has access to, along with their permissions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Text channels retrieved successfully with their permissions",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TextChannelPermissionDTO[].class)) }),
            @ApiResponse(responseCode = "400",
                    description = "Invalid token or no accessible text channels found",
                    content = @Content)
    })
    @GetMapping("/for-user")
    public ResponseEntity<Set<StudiesDTO>> getTextChannelsForUser(@RequestHeader("Authorization") String token){
        return new ResponseEntity<>(orchestrationService.getEverything(token.substring(7)), HttpStatus.OK);
    }


}
