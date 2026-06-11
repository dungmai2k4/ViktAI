package com.viktai.service.impl;

import com.viktai.client.HuggingFaceClient;
import com.viktai.dto.AiDesignResult;
import com.viktai.dto.ConversationResponse;
import com.viktai.dto.MessageRequest;
import com.viktai.dto.MessageResponse;
import com.viktai.entity.Conversation;
import com.viktai.entity.Message;
import com.viktai.exception.ResourceNotFoundException;
import com.viktai.repository.ConversationRepository;
import com.viktai.service.ConversationService;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;
    private final HuggingFaceClient huggingFaceClient;

    public ConversationServiceImpl(ConversationRepository conversationRepository, HuggingFaceClient huggingFaceClient) {
        this.conversationRepository = conversationRepository;
        this.huggingFaceClient = huggingFaceClient;
    }

    @Override
    public ConversationResponse createConversation(String style, String designType, String description, List<MultipartFile> files) {
        validateCreateRequest(style, designType, files);
        // Prompt ban đầu gom loại ảnh, phong cách và mô tả để AI giữ đúng cấu trúc không gian.
        String prompt = buildInitialPrompt(style, designType, description);
        AiDesignResult aiResult = huggingFaceClient.generateDesign(prompt, files);

        Conversation conversation = new Conversation();
        conversation.setStyle(style);
        conversation.setDesignType(designType);
        conversation.setDescription(description);
        conversation.setCurrentImageUrl(aiResult.imageUrl());
        conversation.setCurrentDescription(aiResult.description());
        conversation.addMessage(newMessage("USER", prompt));
        conversation.addMessage(newMessage("ASSISTANT", aiResult.description()));

        return toResponse(conversationRepository.save(conversation));
    }

    @Override
    public ConversationResponse addMessage(Long id, MessageRequest request) {
        Conversation conversation = findConversation(id);
        // Prompt chỉnh sửa luôn nhắc AI giữ bố cục, kích thước và phong cách ban đầu.
        String prompt = buildEditPrompt(conversation, request.message());
        AiDesignResult aiResult = huggingFaceClient.generateDesign(prompt, List.of());

        conversation.setCurrentImageUrl(aiResult.imageUrl());
        conversation.setCurrentDescription(aiResult.description());
        conversation.addMessage(newMessage("USER", request.message()));
        conversation.addMessage(newMessage("ASSISTANT", aiResult.description()));

        return toResponse(conversationRepository.save(conversation));
    }

    @Override
    public List<ConversationResponse> getConversations() {
        return conversationRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    @Override
    public ConversationResponse getConversation(Long id) {
        return toResponse(findConversation(id));
    }

    @Override
    public void deleteConversation(Long id) {
        if (!conversationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy cuộc hội thoại: " + id);
        }
        conversationRepository.deleteById(id);
    }

    private Conversation findConversation(Long id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cuộc hội thoại: " + id));
    }

    private void validateCreateRequest(String style, String designType, List<MultipartFile> files) {
        if (!StringUtils.hasText(style) || !StringUtils.hasText(designType)) {
            throw new IllegalArgumentException("style và designType là bắt buộc");
        }
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Cần upload ít nhất một ảnh");
        }
        if ("FOUR_WALLS".equalsIgnoreCase(designType) && files.size() != 4) {
            throw new IllegalArgumentException("Loại 4 mặt căn phòng cần đúng 4 ảnh");
        }
        if ("FLOOR_PLAN".equalsIgnoreCase(designType) && files.size() != 1) {
            throw new IllegalArgumentException("Loại sơ đồ mặt bằng cần đúng 1 ảnh");
        }
    }

    private String buildInitialPrompt(String style, String designType, String description) {
        return "Dựa trên các ảnh căn phòng được cung cấp.\n\n"
                + "Loại đầu vào: " + designType + "\n\n"
                + "Phong cách:\n" + style + "\n\n"
                + "Mô tả:\n" + (description == null ? "" : description) + "\n\n"
                + "Yêu cầu:\n"
                + "* Giữ nguyên cấu trúc không gian\n"
                + "* Thiết kế theo phong cách " + style + "\n"
                + "* Tạo ảnh render chân thực\n"
                + "* Viết mô tả ngắn";
    }

    private String buildEditPrompt(Conversation conversation, String message) {
        return "Đây là thiết kế hiện tại.\n\n"
                + "Ảnh hiện tại: " + conversation.getCurrentImageUrl() + "\n\n"
                + "Giữ nguyên:\n"
                + "* bố cục phòng\n"
                + "* kích thước phòng\n"
                + "* không gian ban đầu\n"
                + "* phong cách đã chọn: " + conversation.getStyle() + "\n\n"
                + "Mô tả ban đầu: " + conversation.getDescription() + "\n\n"
                + "Chỉ thay đổi:\n" + message + "\n\n"
                + "Tạo ảnh mới.";
    }

    private Message newMessage(String role, String content) {
        Message message = new Message();
        message.setRole(role);
        message.setContent(content);
        return message;
    }

    private ConversationResponse toResponse(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getStyle(),
                conversation.getDesignType(),
                conversation.getDescription(),
                conversation.getCurrentImageUrl(),
                conversation.getCurrentDescription(),
                conversation.getCreatedAt(),
                conversation.getMessages().stream().map(this::toMessageResponse).toList()
        );
    }

    private MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(message.getId(), message.getRole(), message.getContent(), message.getCreatedAt());
    }
}
