package com.netcracker.educ.printing.service;

import com.netcracker.educ.printing.model.entity.Chat;
import com.netcracker.educ.printing.model.entity.Message;
import com.netcracker.educ.printing.model.entity.User;
import com.netcracker.educ.printing.model.repository.MessageRepo;
import com.netcracker.educ.printing.model.representationModel.MessageRepresent;
import com.netcracker.educ.printing.security.UserDetailsImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Data
public class MessageService {
    private final MessageRepo messageRepo;
    private final UserService userService;
    private final ChatService chatService;


    public List<Message> getMessagesByChat(UUID chatId, UserDetailsImpl principal) {
        log.debug("Get messages by chatId= {} , User: {}",chatId,principal.getId());
        Chat chat = chatService.getChatById(chatId);
        if (chat != null && ((chat.getCustomer().getId().equals(principal.getId())) || chat.getExecutor().getId().equals(principal.getId()))) {
            return messageRepo.findAllByChat(chat);
        } else return null;
    }


    public Message createMessage(MessageRepresent message, User currentUser) {
        Message newMessage = new Message();
        newMessage.setDate(new Date());
        newMessage.setChat(chatService.getChatById(message.getChatId()));
        newMessage.setAuthor(currentUser);
        newMessage.setChecked(Boolean.FALSE);
        newMessage.setText(message.getText());
        Message dbMessage= messageRepo.save(newMessage);
        log.debug("User {} created message {}",currentUser.getId(),dbMessage.getId());
        return dbMessage;
    }

    public void deleteMessage(Message message) {
        messageRepo.deleteById(message.getId());
    }

    public void setChecked(Message message) {
        messageRepo.findById(message.getId()).ifPresent(dbMessage -> dbMessage.setChecked(true));
    }
}
