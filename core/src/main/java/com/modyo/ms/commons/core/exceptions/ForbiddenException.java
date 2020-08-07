package com.modyo.ms.commons.core.exceptions;

/**
 * Used for throw exceptions duo to missing or invalid permissions
 */
public class ForbiddenException extends RuntimeException {

  private static final long serialVersionUID = 7482659483952163075L;

  public ForbiddenException(String message) {
    super(message);
  }

}
