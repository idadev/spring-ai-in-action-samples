// tag::relevancy[]
package com.example.boardgamebuddy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
// end::relevancy[]
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
// tag::relevancy[]
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringAiBoardGameServiceTests {

  @Autowired
  private BoardGameService boardGameService;  // <1>

  @Autowired
  private ChatClient.Builder chatClientBuilder; // <2>

  private RelevancyEvaluator relevancyEvaluator;

  // end::relevancy[]
  // tag::correctness[]
  private FactCheckingEvaluator factCheckingEvaluator;

  // tag::relevancy[]
  @BeforeEach
  public void setup() {
    this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
    // end::relevancy[]
    this.factCheckingEvaluator = new FactCheckingEvaluator(
        chatClientBuilder);
    // tag::relevancy[]
  }
  // end::correctness[]

  @Test
  public void evaluateRelevancy() {
    String userText = "Why is the sky blue?";
    Question question = new Question(userText);
    Answer answer = boardGameService.askQuestion(question); // <3>

    EvaluationRequest evaluationRequest = new EvaluationRequest(
        userText, answer.answer());

    EvaluationResponse response = relevancyEvaluator
        .evaluate(evaluationRequest); // <4>

    Assertions.assertThat(response.isPass())   // <5>
        .withFailMessage("""
          ========================================
          The answer "%s"
          is not considered relevant to the question
          "%s".
          ========================================
          """, answer.answer(), userText)
        .isTrue();
  }

  // end::relevancy[]
  // tag::correctnessTest[]
  @Test
  public void evaluateFactualAccuracy() {
    var userText = "Why is the sky blue?";
    var question = new Question(userText);
    var answer = boardGameService.askQuestion(question);

    var evaluationRequest =
            new EvaluationRequest(userText, answer.answer());

    var response =
            factCheckingEvaluator.evaluate(evaluationRequest);

    Assertions.assertThat(response.isPass())
        .withFailMessage("""

          ========================================
          The answer "%s"
          is not considered correct for the question
          "%s".
          ========================================
          """, answer.answer(), userText)
        .isTrue();
  }
  // end::correctnessTest[]

// tag::relevancy[]
}
// end::relevancy[]
