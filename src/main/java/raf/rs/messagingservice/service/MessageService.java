package raf.rs.messagingservice.service;

import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.dto.NewMessageDTO;
import raf.rs.messagingservice.model.Message;

import java.util.List;

public interface MessageService {
    List<MessageDTO> findAllFromChannel(Long channelId);
    MessageDTO findById(Long id);
    MessageDTO sendMessage(NewMessageDTO message);
    void deleteMessage(Long id);
    MessageDTO editMessage(Long id, Message message);
}
