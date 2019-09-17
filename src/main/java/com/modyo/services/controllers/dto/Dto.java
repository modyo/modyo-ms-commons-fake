package com.modyo.services.controllers.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;


/**
 * Base DTO Serializable
 */
//TODO deprecate
public class Dto implements Serializable {

  public String toJsonString() {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString;
    try {
      jsonString = mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      jsonString = "toJsonString failed: " + e.getMessage();
    }
    return jsonString;
  }

}
