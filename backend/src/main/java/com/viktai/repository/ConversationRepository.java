package com.viktai.repository;

import com.viktai.entity.Conversation;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @EntityGraph(attributePaths = "messages")
    List<Conversation> findAllByOrderByCreatedAtDesc();
}
