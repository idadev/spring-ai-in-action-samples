package com.example.simpletranslator;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslatorController {

  @Value("classpath:/translationPromptTemplate.st")
  private Resource userPromptMessage;       // <1>

  private final ChatClient chatClient;

  public TranslatorController(
      ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();  // <2>
  }

  @PostMapping("/translate")
  public Translation translate(@RequestBody Translation request) {
    return chatClient.prompt()
        .user(userSpec -> userSpec
            .text(userPromptMessage)
            .param("sourceLanguage", request.sourceLanguage())
            .param("targetLanguage", request.targetLanguage())
            .param("sourceText", request.text()))  // <3>
        .call()
        .entity(Translation.class);
  }

}
