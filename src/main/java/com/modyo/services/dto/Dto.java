package com.modyo.services.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.io.Serializable;

/**
 * Base DTO Serializable
 */
public class Dto implements Serializable {

  public String toJsonString() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
    String jsonString;
    try {
      jsonString = mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      jsonString = "toJsonString failed: " + e.getMessage();
    }
    return jsonString;
  }

}
