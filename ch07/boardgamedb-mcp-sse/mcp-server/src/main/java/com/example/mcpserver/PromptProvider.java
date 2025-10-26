package com.example.mcpserver;

import com.logaritex.mcp.annotation.McpArg;
import com.logaritex.mcp.annotation.McpPrompt;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptProvider {

  @McpPrompt(
      name = "gamesForPlayerCount",
      description = "A prompt to find games for a specific number of players") // <1>
  public McpSchema.GetPromptResult gamesForPlayerCount(
      @McpArg(name = "playerCount",
              description = "The number of players",
              required = true) Integer playerCount) {  // <2>

    var userMessage = new McpSchema.PromptMessage(  // <3>
        McpSchema.Role.USER,
        new McpSchema.TextContent(
            String.format("Find games for %s players", playerCount)
        ));

    return new McpSchema.GetPromptResult(
        String.format("A prompt to find games for %s players", playerCount),
        List.of(userMessage));  // <4>
  }

  @McpPrompt(
      name = "gamesForPlayingTime",
      description = "A prompt to find games for a specific number of players") // <1>
  public McpSchema.GetPromptResult gamesForPlayingTime(
      @McpArg(name = "timeInMinutes",
          description = "The time in minutes",
          required = true) Integer timeInMinutes) {  // <2>

    var userMessage = new McpSchema.PromptMessage( // <3>
        McpSchema.Role.USER,
        new McpSchema.TextContent(
            String.format("Find games to play in %s minutes", timeInMinutes)
        ));

    return new McpSchema.GetPromptResult(
        String.format("A prompt to find games to play in %s minutes", timeInMinutes),
        List.of(userMessage));  // <4>
  }
}
