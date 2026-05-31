package com.ajay.lovable.intelligenceservice.llm;


import com.ajay.lovable.commonlib.enums.ChatEventStatus;
import com.ajay.lovable.commonlib.enums.ChatEventType;
import com.ajay.lovable.intelligenceservice.entity.ChatEvent;
import com.ajay.lovable.intelligenceservice.entity.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class LlmResponseParser {

    /**
     * Regex Breakdown:
     * Group 1: Opening Tag (<tag ..>)
     * Group 2: Tag Name (message|file|tool)
     * Group 3: Attributes part (e.g., ' path="foo"' or 'args="a,b"')
     * Group 4: Content (The stuff inside)
     * Group 5: Closing Tag (</tag)
     * [
     *   ChatEvent(type=MESSAGE, content="Hello"),
     *   ChatEvent(type=FILE_EDIT, filePath="abc.txt", content="Updated code"),
     *   ChatEvent(type=TOOL_LOG, metadata="file1,file2")
     * ]
     */

    private static final Pattern GENERIC_TAG_PATTERN = Pattern.compile(
            "(<(message|file|tool)([^>]*)>)([\\s\\S]*?)(</\\2>)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    // Helper to extract specific attributes (path="..." or args="...") from Group 3
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(
            "(path|args)=\"([^\"]+)\""
    );

    public List<ChatEvent> parseChatEvents(String fullResponse, ChatMessage parentMessage) {
        List<ChatEvent> events = new ArrayList<>();
        int orderCounter = 1;

        Matcher matcher = GENERIC_TAG_PATTERN.matcher(fullResponse);

        while (matcher.find()) {
            String tagName = matcher.group(2).toLowerCase(); // file
            String attributes = matcher.group(3);  // path = "abc.css"
            String content = matcher.group(4).trim(); // Updated this file

            // Extract attributes map
            Map<String, String> attrMap = extractAttributes(attributes); // {path: abc.css}

            ChatEvent.ChatEventBuilder builder = ChatEvent.builder()
                                                          .status(ChatEventStatus.CONFIRMED)
                                                          .chatMessage(parentMessage)
                    .content(content) // This is your Markdown content
                    .sequenceOrder(orderCounter++);

            switch (tagName) {
                case "message" -> builder.type(ChatEventType.MESSAGE);
                case "file" -> {
                    builder.type(ChatEventType.FILE_EDIT);
                    builder.status(ChatEventStatus.PENDING);
                    builder.filePath(attrMap.get("path")); // Required for files
                }
                case "tool" -> {
                    builder.type(ChatEventType.TOOL_LOG);
                    builder.metadata(attrMap.get("args")); // Store raw file list in metadata
                }
                default -> { continue; }
            }

            events.add(builder.build());
        }

        return events;
    }

    private Map<String, String> extractAttributes(String attributeString) {
        Map<String, String> attributes = new HashMap<>();
        if (attributeString == null) return attributes;

        Matcher matcher = ATTRIBUTE_PATTERN.matcher(attributeString);
        while (matcher.find()) {
            attributes.put(matcher.group(1), matcher.group(2));
        }
        return attributes;
    }
}
