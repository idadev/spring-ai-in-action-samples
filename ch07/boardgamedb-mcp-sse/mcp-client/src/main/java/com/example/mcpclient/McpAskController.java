package com.example.mcpclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

// tag::useSystemTemplate[]
@RestController
public class McpAskController {

//    @Value("classpath:/prompts/systemPrompt.st")  // <1>
//    private Resource systemPromptTemplate;

    private final ChatClient chatClient;

    public McpAskController(ChatClient.Builder chatClientBuilder,
                            ToolCallbackProvider tools) {
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(tools)
                .build();
    }

    @PostMapping("/ask")
    public Answer ask(@RequestBody Question question) {
        return chatClient.prompt()
//            .system(systemPromptTemplate) // <2>
            .user(question.question())
            .call()
            .entity(Answer.class);
    }

    // end::useSystemTemplate[]
    public record Question(String question) { }

    public record Answer(String answer) { }
    // tag::useSystemTemplate[]
}
// end::useSystemTemplate[]
