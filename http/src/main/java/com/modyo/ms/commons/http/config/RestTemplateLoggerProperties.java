package com.modyo.ms.commons.http.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "commons.http.loggers.rest-template")
public class RestTemplateLoggerProperties extends LoggerProperties {

}

