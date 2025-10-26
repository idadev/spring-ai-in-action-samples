package com.example.boardgamebuddy;

import org.springframework.core.io.Resource;

// tag::summarizeRules[]
public interface BoardGameService {
// end::summarizeRules[]
    Answer askQuestion(Question question, String conversationId);

    Answer askQuestion(Question question,
                              Resource image,
                              String imageContentType,
                              String conversationId);

  /*
  // tag::summarizeRules[]

    //...

  // end::summarizeRules[]
   */

    // tag::summarizeRules[]
    Answer summarizeRules(String text);
}
// end::summarizeRules[]