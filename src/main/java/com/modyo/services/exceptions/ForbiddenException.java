package com.modyo.services.exceptions;

public class ForbiddenException extends RuntimeException {

  private static final long serialVersionUID = 8283697959497165524L;

  public ForbiddenException(String message) {
    super(message);
  }

}
