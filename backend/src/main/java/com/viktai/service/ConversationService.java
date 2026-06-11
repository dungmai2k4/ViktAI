package com.viktai.service;

import com.viktai.dto.ConversationResponse;
import com.viktai.dto.MessageRequest;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ConversationService {
    ConversationResponse createConversation(String style, String designType, String description, List<MultipartFile> files);
    ConversationResponse addMessage(Long id, MessageRequest request);
    List<ConversationResponse> getConversations();
    ConversationResponse getConversation(Long id);
    void deleteConversation(Long id);
}
