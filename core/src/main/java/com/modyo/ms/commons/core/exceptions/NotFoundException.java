package com.modyo.ms.commons.core.exceptions;

/**
 * Used for throw exceptions when is need to return a 404 http status
 */
public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = 8283697959497165524L;

  public NotFoundException() {
    super("resource not found");
  }

}
