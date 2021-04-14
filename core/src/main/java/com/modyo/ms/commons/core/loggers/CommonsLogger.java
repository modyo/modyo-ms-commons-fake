package com.modyo.ms.commons.core.loggers;

import static net.logstash.logback.marker.Markers.appendFields;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;

@Getter
@Setter
public abstract class CommonsLogger {

  public static final String REQUEST_ID_KEY = "requestId";
  public static final String CORRELATION_ID_KEY = "correlationId";
  @JsonIgnore
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private String type;
  private String correlationId;
  private String requestId;
  private String message;

  public void logInfo() {
    setBasicLogInformation();
    logger.info(appendFields( this), message);
  }

  public void logError() {
    setBasicLogInformation();
    logger.error(appendFields(this), message);
  }

  public void setBasicLogInformation() {
    this.correlationId = Objects.requireNonNull(
        RequestContextHolder.currentRequestAttributes().getAttribute(CORRELATION_ID_KEY, 0)
    ).toString();
    this.requestId = (String) RequestContextHolder.currentRequestAttributes().getAttribute(REQUEST_ID_KEY, 0);
  }

}
