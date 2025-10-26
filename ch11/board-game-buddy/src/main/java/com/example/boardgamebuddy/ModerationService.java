package com.example.boardgamebuddy;

import org.springframework.ai.moderation.ModerationModel;
import org.springframework.ai.moderation.ModerationPrompt;
import org.springframework.stereotype.Service;

@Service
public class ModerationService {

  private final ModerationModel moderationModel;

  public ModerationService(ModerationModel moderationModel) { // <1>
    this.moderationModel = moderationModel;
  }

  public void moderate(String text) {
    var moderationResponse =
        moderationModel.call(new ModerationPrompt(text)); // <2>

    var moderationResult = moderationResponse.getResult()
        .getOutput().getResults().getFirst();
    var categories = moderationResult.getCategories();    // <3>

    if (categories.isHate() || categories.isHateThreatening()) // <4>
      throw new ModerationException("Hate");
    else if (categories.isHarassment() ||
             categories.isHarassmentThreatening())      // <4>
      throw new ModerationException("Harassment");
    else if (categories.isViolence())                   // <4>
      throw new ModerationException("Violence");
  }

}
