package com.example.boardgamebuddy;

// tag::filterExpressionImport[]
import static org.springframework.ai.rag.retrieval.search
    .VectorStoreDocumentRetriever.FILTER_EXPRESSION;
// end::filterExpressionImport[]

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SpringAiBoardGameService implements BoardGameService {

  private final ChatClient chatClient;

  public SpringAiBoardGameService(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  @Value("classpath:/promptTemplates/systemPromptTemplate.st")
  Resource promptTemplate;

  @Override
  public Answer askQuestion(Question question) {
    var gameNameMatch = String.format(
            "gameTitle == '%s'",
            normalizeGameTitle(question.gameTitle()));

    // tag::ask[]
    return chatClient.prompt()
        .system(systemSpec -> systemSpec
            .text(promptTemplate)
            .param("gameTitle", question.gameTitle()))
        .user(question.question())
        .advisors(advisorSpec ->
            advisorSpec.param(FILTER_EXPRESSION, gameNameMatch)) // <1>
        .call()
        .entity(Answer.class);
    // end::ask[]
  }

  private String normalizeGameTitle(String in) {
    return in.toLowerCase().replace(' ', '_');
  }

}
