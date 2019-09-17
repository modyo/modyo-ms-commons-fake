package com.modyo.services.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * Configuracion que retorna los tipos http que retornan los endpoints.
 */
@Configuration
public class HandledHttpStatusListConfiguration {

  @Bean
  public List<HttpStatus> handledHttpStatusList() {
    return new ArrayList<>(Arrays.asList(
        HttpStatus.OK,
        HttpStatus.NO_CONTENT,
        HttpStatus.BAD_REQUEST,
        HttpStatus.UNAUTHORIZED,
        HttpStatus.FORBIDDEN,
        HttpStatus.METHOD_NOT_ALLOWED,
        HttpStatus.UNSUPPORTED_MEDIA_TYPE,
        HttpStatus.UNPROCESSABLE_ENTITY,
        HttpStatus.INTERNAL_SERVER_ERROR,
        HttpStatus.BAD_GATEWAY //TODO deprecate
    ));
  }
}
