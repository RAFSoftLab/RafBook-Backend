package raf.rs.messagingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.dto.NewMessageDTO;
import raf.rs.messagingservice.service.MessageService;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/messages")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {
    private MessageService messageService;

    @Operation(summary = "Find all messages from channel", description = "Finds and returns all messages. from the specified channel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages found successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO[].class)) }),
            @ApiResponse(responseCode = "400", description = "No messages found",
                    content = @Content)
    })
    @GetMapping("/channel/{channelId}/{start}/{end}")
    public ResponseEntity<List<MessageDTO>> findAllFromChannel(@PathVariable Long channelId, @PathVariable int start, @PathVariable int end) {
        return ResponseEntity.ok(messageService.findAllFromChannel(channelId, start, end));
    }

    @Operation(summary = "Find a message by ID", description = "Finds and returns the message with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message found successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or message not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.findById(id));
    }

    @Operation(summary = "Send a new message", description = "Sends a new message to the specified channel and returns the created message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or channel not found",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@RequestHeader("Authorization") String token, @RequestBody NewMessageDTO message) {
        return ResponseEntity.ok(messageService.sendMessage(message, token.substring(7)));
    }

    @Operation(summary = "Delete a message", description = "Deletes the message with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or message not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        messageService.deleteMessage(id, token);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Edit a message", description = "Edits the message with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message edited successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or message not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<MessageDTO> editMessage(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody MessageDTO message) {
        return ResponseEntity.ok(messageService.editMessage(id, message, token));
    }
}
