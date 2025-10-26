package com.example.boardgamebuddy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice     // <1>
public class ModerationExceptionHandler {

  @ExceptionHandler(ModerationException.class)   // <2>
  public ProblemDetail moderationException(ModerationException ex) {
    var problemDetail = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage()); // <3>
    problemDetail.setTitle("Moderation Exception");
    return problemDetail;
  }

}
