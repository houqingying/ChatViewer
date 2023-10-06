package com.chatviewer.blog.dto;

import com.chatviewer.blog.pojo.Conversation;
import lombok.Data;

/**
 * @author ChatViewer
 */
@Data
public class ConversationDto {

    private Long conversationId;

    private String firstMessage;
}
