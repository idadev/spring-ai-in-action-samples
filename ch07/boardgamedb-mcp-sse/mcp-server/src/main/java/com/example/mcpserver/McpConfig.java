package com.example.mcpserver;

import com.logaritex.mcp.spring.SpringAiMcpAnnotationProvider;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpConfig {

  @Bean
    ToolCallbackProvider toolCallbackProvider(GameTools tools) {
    return MethodToolCallbackProvider.builder()
        .toolObjects(tools)
        .build();
  }

//  @Bean
// tag::gamePrompts[]
  public List<McpServerFeatures.SyncPromptSpecification> gamePrompts() {
    var playerCountPrompt = new McpSchema.Prompt(  // <1>
        "gamesForPlayerCount",
        "A prompt to find games for a specific number of players",
        List.of(new McpSchema.PromptArgument(
            "playerCount", "The number of players", true)));

    var playerCountPromptSpec = new McpServerFeatures.SyncPromptSpecification( // <2>
        playerCountPrompt, (exchange, getPromptRequest) -> {
      String playerCount =
          (String) getPromptRequest.arguments().get("playerCount"); // <3>
      var userMessage = new McpSchema.PromptMessage(
          McpSchema.Role.USER,
          new McpSchema.TextContent(
              String.format("Find games for %s players", playerCount)
          ));
      return new McpSchema.GetPromptResult(
          String.format("A prompt to find games for %s players", playerCount),
          List.of(userMessage));
    });

    var playingTimePrompt = new McpSchema.Prompt(  // <1>
        "gamesForPlayingTime",
        "A prompt to find games for given amount of time",
        List.of(new McpSchema.PromptArgument(
            "timeInMinutes", "The time in minutes", true)));

    var playingTimePromptSpec = new McpServerFeatures.SyncPromptSpecification( // <2>
        playingTimePrompt, (exchange, getPromptRequest) -> {
      String timeInMinutes =
          (String) getPromptRequest.arguments().get("timeInMinutes"); // <3>
      var userMessage = new McpSchema.PromptMessage(
          McpSchema.Role.USER,
          new McpSchema.TextContent(
              String.format("Find games to play in %s minutes", timeInMinutes)
          ));
      return new McpSchema.GetPromptResult(
          String.format("A prompt to find games to play in %s minutes",
              timeInMinutes),
          List.of(userMessage));
    });

    return List.of(playerCountPromptSpec, playingTimePromptSpec); // <4>
  }
  // end::gamePrompts[]


//  @Bean
  // tag::resources[]
  public List<McpServerFeatures.SyncResourceSpecification>
      gameResources(GameRepository gameRepository) {
    List<McpSchema.Role> audience = List.of(McpSchema.Role.USER);
    McpSchema.Annotations annotations =
        new McpSchema.Annotations(audience, 1.0);

    var gameListResource = new McpSchema.Resource(   // <1>
        "games://game-list",
        "Game List",
        "A list of games available in the repository",
        "text/plain",
        annotations
    );

    var gameTitles = gameRepository.findAllTitles(); // <2>
    var gameListText = new StringBuilder();
    for (String title : gameTitles) {
      gameListText.append("- ").append(title).append("\n");
    }

    var gameListResourceSpec = new McpServerFeatures.SyncResourceSpecification( // <3>
        gameListResource, (exchange, request) -> {
      return new McpSchema.ReadResourceResult(
          List.of(new McpSchema.TextResourceContents(
              request.uri(),
              "text/plain",
              gameListText.toString())));
    });

    return List.of(gameListResourceSpec);  // <4>
  }
  // end::resources[]

  // tag::promptAnnotationBean[]
  @Bean
  List<McpServerFeatures.SyncPromptSpecification> myPrompts(
          PromptProvider promptProvider) {
    return SpringAiMcpAnnotationProvider
        .createSyncPromptSpecifications(List.of(promptProvider));
  }
  // end::promptAnnotationBean[]

  // tag::resourceAnnotationBean[]
  @Bean
  public List<McpServerFeatures.SyncResourceSpecification> myResources(
          ResourceProvider resourceProvider) {
    return SpringAiMcpAnnotationProvider
        .createSyncResourceSpecifications(List.of(resourceProvider));
  }
  // end::resourceAnnotationBean[]

}
