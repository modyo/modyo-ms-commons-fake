package com.modyo.ms.commons.http.config.properties;

import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;

@Component
@ConfigurationProperties(prefix = "commons.http.swagger.api-info")
@Getter
@Setter
public class SwaggerApiInfoProperties {

  private String title = "";
  private String description = "";
  private String version = "";
  private String termsOfServiceUrl = "";
  private SwaggerContactProperties contact = new SwaggerContactProperties();
  private String license = "";
  private String licenseUrl = "";

  @Setter
  @Getter
  public class SwaggerContactProperties {

    private String name = "";
    private String url = "";
    private String email = "";

  }

  public ApiInfo getApiInfo() {

    return new ApiInfo(
        title,
        description,
        version,
        termsOfServiceUrl,
        new Contact(
            contact.getName(),
            contact.getUrl(),
            contact.getEmail()
        ),
        license,
        licenseUrl,
        Collections.emptyList()
    );
  }

}
