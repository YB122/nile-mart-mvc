package org.route.exception;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException{
  private final String filed;
    public BusinessRuleException(String message, String filed) {
        super(message);
        this.filed = filed;
    }
}
