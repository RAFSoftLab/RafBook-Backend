package raf.rs.messagingservice.controller;

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
import raf.rs.messagingservice.dto.*;
import raf.rs.messagingservice.service.TextChannelService;
import raf.rs.orchestration.service.OrchestrationService;
import raf.rs.userservice.dto.ResponseMessageDTO;

import java.util.List;
import java.util.Set;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/text-channel")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
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
    public ResponseEntity<TextChannelDTO> createTextChannel(@RequestHeader("Authorization") String token, @RequestBody NewTextChannelDTO dto) {
        log.info("Entering createTextChannel with token: {}, dto: {}", token, dto);
        TextChannelDTO createdChannel = textChannelService.createTextChannel(token.substring(7), dto);
        log.info("Exiting createTextChannel with result: {}", createdChannel);
        return ResponseEntity.ok(createdChannel);
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
        log.info("Entering findAll");
        List<TextChannelDTO> channels = textChannelService.findAll();
        log.info("Exiting findAll with result: {}", channels);
        return ResponseEntity.ok(channels);
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
        log.info("Entering findById with id: {}", id);
        TextChannelDTO channel = textChannelService.findById(id);
        log.info("Exiting findById with result: {}", channel);
        return ResponseEntity.ok(channel);
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
        log.info("Entering getTextChannelsForUser with token: {}", token);
        Set<StudiesDTO> channels = orchestrationService.getEverything(token.substring(7));
        log.info("Exiting getTextChannelsForUser with result: {}", channels);
        return new ResponseEntity<>(channels, HttpStatus.OK);
    }

    @Operation(summary = "Add roles to text channel",
            description = "Adds roles to a specific text channel for the user, allowing for role-based access control.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Roles added successfully to the text channel",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)) }),
            @ApiResponse(responseCode = "400",
                    description = "Invalid token or invalid channel/role data",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Text channel not found",
                    content = @Content)
    })
    @PutMapping("/add-roles/{id}")
    public ResponseEntity<ResponseMessageDTO> addRolesToTextChannel(@RequestHeader("Authorization") String token,
                                                                    @PathVariable("id") Long id,
                                                                    @RequestBody Set<String> roles) {
        log.info("Entering addRolesToTextChannel with token: {}, id: {}, roles: {}", token, id, roles);
        textChannelService.addRolesToTextChannel(token.substring(7), id, roles);
        ResponseMessageDTO response = new ResponseMessageDTO("You successfully added roles to text channel");
        log.info("Exiting addRolesToTextChannel with response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Remove roles from text channel",
            description = "Removes specified roles from a specific text channel for the user, adjusting role-based access control.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Roles removed successfully from the text channel",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)) }),
            @ApiResponse(responseCode = "400",
                    description = "Invalid token or invalid channel/role data",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Text channel not found",
                    content = @Content)
    })
    @PatchMapping("/remove-roles/{id}")
    public ResponseEntity<ResponseMessageDTO> removeRolesFromTextChannel(@RequestHeader("Authorization") String token,
                                                                         @PathVariable("id") Long id,
                                                                         @RequestBody Set<String> roles) {
        log.info("Entering removeRolesFromTextChannel with token: {}, id: {}, roles: {}", token, id, roles);
        textChannelService.removeRolesFromTextChannel(token.substring(7), id, roles);
        ResponseMessageDTO response = new ResponseMessageDTO("You successfully removed roles from the text channel");
        log.info("Exiting removeRolesFromTextChannel with response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Edit text channel",
            description = "Edit name and description of specific text channel which id you have to pass")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Text channel successfully changed",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Text channel with passed id doesn't exist",
                    content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<TextChannelDTO> editTextChannel(@RequestHeader("Authorization") String token, @RequestParam Long id,
                                                          @RequestParam String name, @RequestParam String description) {
        log.info("Entering editTextChannel with token: {}, id: {}, name: {}, description: {}", token, id, name, description);
        TextChannelDTO updatedChannel = textChannelService.editTextChannel(id, name, description, token.substring(7));
        log.info("Exiting editTextChannel with result: {}", updatedChannel);
        return new ResponseEntity<>(updatedChannel, HttpStatus.OK);
    }

    @Operation(summary = "Delete text channel",
            description = "Deletion of specific text channel which id you have to pass")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Text channel successfully deleted",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Text channel with passed id doesn't exist",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO> deleteTextChannel(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        log.info("Entering deleteTextChannel with token: {}, id: {}", token, id);
        textChannelService.deleteTextChannel(id, token.substring(7));
        ResponseMessageDTO response = new ResponseMessageDTO("Channel successfully deleted");
        log.info("Exiting deleteTextChannel with response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}