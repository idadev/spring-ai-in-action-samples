// tag::skeleton[]
package com.example.embabelgamesagent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.PromptRunner;
import com.embabel.agent.domain.io.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@Agent(
    name = "GameInfoAgent",
    description = "An agent that helps users answer questions " +
        "about board games, including mechanics and player counts.",
    version = "1.0.0")  // <1>
public class GameInfoAgent {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GameInfoAgent.class);

  // end::skeleton[]

  //
  // Prompt template resources
  //
  // tag::extractGameTitle[]
  @Value("classpath:/promptTemplates/determineTitle.st")  // <1>
  Resource determineTitlePromptTemplate;

  // end::extractGameTitle[]

  // tag::getGameRulesFilename[]
  @Value("classpath:/promptTemplates/rulesFetcher.st")  // <1>
  Resource rulesFetcherPromptTemplate;

  // end::getGameRulesFilename[]

  // tag::determinePlayerCount[]
  @Value("classpath:/promptTemplates/playerCount.st")  // <1>
  Resource playerCountPromptTemplate;

  // end::determinePlayerCount[]

  // tag::determineGameMechanics[]
  @Value("classpath:/promptTemplates/mechanicsDeterminer.st")  // <1>
  Resource mechanicsDeterminerPromptTemplate;

  // end::determineGameMechanics[]

  // tag::skeleton[]
  private final String rulesFilePath;

  public GameInfoAgent(
      @Value("${boardgame.rules.path}") String rulesFilePath) {
    this.rulesFilePath = rulesFilePath;  // <2>
  }

  // ...action methods go here...
  // end::skeleton[]

  //
  // Actions
  //
  // tag::extractGameTitle[]
  @Action
  public GameTitle extractGameTitle(UserInput userInput) {
    LOGGER.info("Extracting game title from user input");

    var prompt = promptResourceToString(determineTitlePromptTemplate, // <2>
        Map.of("userInput", userInput.getContent()));

    return PromptRunner.usingLlm()
        .createObject(prompt, GameTitle.class); // <3>
  }
  // end::extractGameTitle[]

  // tag::getGameRulesFilename[]
  @Action
  public RulesFile getGameRulesFilename(GameTitle gameTitle) {
    LOGGER.info("Getting game rules filename for: " + gameTitle.gameTitle());

    var prompt = promptResourceToString(rulesFetcherPromptTemplate,  // <2>
        Map.of("gameTitle", gameTitle.gameTitle()));

    return PromptRunner.usingLlm()
        .createObject(prompt, RulesFile.class);  // <3>
  }
  // end::getGameRulesFilename[]

  // tag::getGameRules[]
  @Action
  public GameRules getGameRules(GameTitle gameTitle, RulesFile rulesFile) {
    LOGGER.info("Getting game rules for: " + gameTitle.gameTitle()
        + " from file: " + rulesFile.filename());

    if (rulesFile.successful()) {
      String rulesContent =
          new TikaDocumentReader(
              rulesFilePath + "/" + rulesFile.filename())
          .get()
          .getFirst()
          .getText();             // <1>
      if (rulesContent != null) {
        return new GameRules(gameTitle.gameTitle(), rulesContent); // <2>
      }
    }

    throw new ActionFailedException(
        "Unable to fetch rules for the specified game.");  // <3>
  }
  // end::getGameRules[]

  // tag::determinePlayerCount[]
  @Action
  @AchievesGoal(description = "Player count has been determined.") // <2>
  public PlayerCount determinePlayerCount(GameRules gameRules) {
    LOGGER.info("Determining player count from rules for: {}",
                gameRules.gameTitle());

    var prompt = promptResourceToString(playerCountPromptTemplate,  // <3>
        Map.of("gameRules", gameRules.rulesText()));

    return PromptRunner.usingLlm()
        .createObject(prompt, PlayerCount.class);  // <4>
  }
  // end::determinePlayerCount[]

  // tag::determineGameMechanics[]
  @Action
  @AchievesGoal(description = "Game mechanics have been determined.")  // <2>
  public GameMechanics determineGameMechanics(GameRules gameRules) {
    LOGGER.info("Determining mechanics from rules for: {}",
                gameRules.gameTitle());

    var prompt = promptResourceToString(mechanicsDeterminerPromptTemplate, // <3>
        Map.of("gameRules", gameRules.rulesText()));

    return PromptRunner.usingLlm()
        .createObject(prompt, GameMechanics.class); // <4>
  }
  // end::determineGameMechanics[]

  //
  // Helper Methods
  //
  // tag::promptResourceToString[]
  private String promptResourceToString(Resource resource, Map<String, String> params) {
    try {
      var promptString = resource.getContentAsString(Charset.defaultCharset());
      var stringTemplate = new ST(promptString, '{', '}');
      params.forEach(stringTemplate::add);
      return stringTemplate.render();
    } catch (IOException e) {
      LOGGER.error("Error reading prompt resource: " + resource.getFilename(), e);
      return "";
    }
  }
  // end::promptResourceToString[]

  // tag::skeleton[]

}
// end::skeleton[]
