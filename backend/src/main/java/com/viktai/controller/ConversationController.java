package com.viktai.controller;

import com.viktai.dto.ConversationResponse;
import com.viktai.dto.MessageRequest;
import com.viktai.service.ConversationService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ConversationResponse> createConversation(
            @RequestParam String style,
            @RequestParam String designType,
            @RequestParam(required = false) String description,
            @RequestPart("files") List<MultipartFile> files
    ) {
        ConversationResponse response = conversationService.createConversation(style, designType, description, files);
        return ResponseEntity.created(URI.create("/api/conversations/" + response.id())).body(response);
    }

    @PostMapping("/{id}/messages")
    public ConversationResponse addMessage(@PathVariable Long id, @Valid @RequestBody MessageRequest request) {
        return conversationService.addMessage(id, request);
    }

    @GetMapping
    public List<ConversationResponse> getConversations() {
        return conversationService.getConversations();
    }

    @GetMapping("/{id}")
    public ConversationResponse getConversation(@PathVariable Long id) {
        return conversationService.getConversation(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.noContent().build();
    }
}
