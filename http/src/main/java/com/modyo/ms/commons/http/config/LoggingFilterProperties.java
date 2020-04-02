package com.modyo.ms.commons.http.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "commons.http.loggers.filter")
public class LoggingFilterProperties extends LoggerProperties {

}
