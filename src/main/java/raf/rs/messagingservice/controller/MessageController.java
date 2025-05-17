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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.dto.NewMessageDTO;
import raf.rs.messagingservice.dto.ReactionRequestDTO;
import raf.rs.messagingservice.dto.UploadFileDTO;
import raf.rs.messagingservice.model.Emote;
import raf.rs.messagingservice.model.MessageType;
import raf.rs.messagingservice.service.MessageService;
import raf.rs.messagingservice.service.ReactionService;
import raf.rs.userservice.dto.ResponseMessageDTO;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/messages")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class MessageController {

    private MessageService messageService;
    private ReactionService reactionService;

    @Operation(summary = "Find all messages from channel", description = "Finds and returns all messages. from the specified channel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages found successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO[].class)) }),
            @ApiResponse(responseCode = "400", description = "No messages found",
                    content = @Content)
    })
    @GetMapping("/channel/{channelId}/{start}/{end}")
    public ResponseEntity<List<MessageDTO>> findAllFromChannel(@PathVariable Long channelId, @PathVariable int start, @PathVariable int end) {
        log.info("Entering findAllFromChannel with channelId: {}, start: {}, end: {}", channelId, start, end);
        List<MessageDTO> messages = messageService.findAllFromChannel(channelId, start, end);
        log.info("Exiting findAllFromChannel with result: {}", messages);
        return ResponseEntity.ok(messages);
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
        log.info("Entering findById with id: {}", id);
        MessageDTO message = messageService.findById(id);
        log.info("Exiting findById with result: {}", message);
        return ResponseEntity.ok(message);
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
        log.info("Entering sendMessage with token: {}, message: {}", token, message);
        MessageDTO sentMessage = messageService.sendMessage(message, token.substring(7));
        log.info("Exiting sendMessage with result: {}", sentMessage);
        return ResponseEntity.ok(sentMessage);
    }

    @Operation(summary = "Delete a message", description = "Deletes the message with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or message not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        log.info("Entering deleteMessage with token: {}, id: {}", token, id);
        messageService.deleteMessage(id, token.substring(7));
        log.info("Exiting deleteMessage");
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
        log.info("Entering editMessage with token: {}, id: {}, message: {}", token, id, message);
        MessageDTO editedMessage = messageService.editMessage(id, message, token.substring(7));
        log.info("Exiting editMessage with result: {}", editedMessage);
        return ResponseEntity.ok(editedMessage);
    }

    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDTO> uploadFile(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") MessageType type,
            @RequestParam(name = "parentMessage", required = false) Long parentMessage,
            @RequestParam("textChannel") Long textChannel,
            @RequestParam("fileName") String fileName) {
        log.info("Entering uploadFile with token: {}, fileName: {}, type: {}, parentMessage: {}, textChannel: {}", token, fileName, type, parentMessage, textChannel);
        UploadFileDTO dto = new UploadFileDTO(type, parentMessage, textChannel);
        MessageDTO uploadedMessage = messageService.uploadFileMessage(fileName, token.substring(7), dto, file);
        log.info("Exiting uploadFile with result: {}", uploadedMessage);
        return new ResponseEntity<>(uploadedMessage, HttpStatus.OK);
    }

    @Operation(summary = "Toggle a reaction on a message", description = "Adds or removes a reaction from a message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reaction toggled successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or message/emote not found",
                    content = @Content)
    })
    @PostMapping("/reaction")
    public ResponseEntity<MessageDTO> toggleReaction(@RequestHeader("Authorization") String token, @RequestBody ReactionRequestDTO reactionRequest) {

        return ResponseEntity.ok(
                reactionService.toggleReaction(
                        reactionRequest.getMessageId(),
                        reactionRequest.getEmoteName(),
                        token.substring(7)
                )
        );
    }

    @Operation(summary = "Get all available emotes", description = "Returns all emotes available for reactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emotes retrieved successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Emote[].class)) })
    })
    @GetMapping("/emotes")
    public ResponseEntity<List<Emote>> getAllEmotes() {
        return ResponseEntity.ok(reactionService.getAllEmotes());
    }
}