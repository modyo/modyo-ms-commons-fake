package com.modyo.ms.commons.http.loggers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

class ObfuscateBodyParamsService {

  public static final String OBFUSCATED_VALUE = "*********";

  private ObfuscateBodyParamsService() {

  }

  static String obfuscate(String body, List<String> bodyParamsToObfuscate) {
    if (body == null || bodyParamsToObfuscate == null || bodyParamsToObfuscate.isEmpty()) {
      return body;
    }

    try {
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> map = mapper.readValue(body, Map.class);
      obfuscateMapRecursively(map, bodyParamsToObfuscate);

      return mapper.writeValueAsString(map);
    } catch (JsonProcessingException e) {
      return body;
    }
  }

  private static void obfuscateMapRecursively(Map<String,Object> linkedHashMap, List<String> bodyParamsToObfuscate) {
    linkedHashMap.entrySet().forEach(entry -> {
      if (bodyParamsToObfuscate.contains(entry.getKey())) {
        entry.setValue(OBFUSCATED_VALUE);
      } else if (entry.getValue() instanceof Map) {
        obfuscateMapRecursively((Map) entry.getValue(), bodyParamsToObfuscate);
      }
    });
  }

}
