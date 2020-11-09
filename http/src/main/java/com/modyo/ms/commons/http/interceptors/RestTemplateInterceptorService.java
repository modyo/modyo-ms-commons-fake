package com.modyo.ms.commons.http.interceptors;

import com.modyo.ms.commons.http.loggers.RestTemplateRequestLogger;
import com.modyo.ms.commons.http.loggers.RestTemplateResponseLogger;

public interface RestTemplateInterceptorService {

  void intercept(RestTemplateRequestLogger requestLogger, RestTemplateResponseLogger responseLogger);

}
