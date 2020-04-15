package com.modyo.ms.commons.awsapigw.controllers;

import com.modyo.ms.commons.awsapigw.components.SwaggerCustomizer;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;

@ApiIgnore
@RestController
@RequiredArgsConstructor
public class SwaggerForApiGatewayController {

  private final JsonSerializer jsonSerializer;
  private final SwaggerCustomizer swaggerCustomizer;

  @GetMapping(
      path = "/swagger-for-aws-api-gw",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Json> getSwagger(HttpServletRequest request) {
    return new ResponseEntity<>(
        jsonSerializer.toJson(swaggerCustomizer.getCustomSwagger(request)),
        HttpStatus.OK);
  }

}
