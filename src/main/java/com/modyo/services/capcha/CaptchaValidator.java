package com.modyo.services.capcha;

import com.modyo.services.capcha.model.CaptchaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@ConditionalOnProperty(value = "google.recaptcha.secret")
public class CaptchaValidator {

  private static final String GOOGLE_RECAPTCHA_ENDPOINT = "https://www.google.com/recaptcha/api/siteverify";
  private Logger logger = LoggerFactory.getLogger(CaptchaValidator.class);
  @Value("${google.recaptcha.secret}")
  private String recaptchaSecret;

  @Value("${google.recaptcha.minimun}")
  private Float minimun;

  @Value("${google.recaptcha.disabled}")
  private boolean disabled;

  @Autowired
  private ApplicationContext applicationContext;

  public boolean validateCaptcha(String captchaResponse) {
    if (disabled) {
      return true;
    }
    MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
    requestMap.add("secret", recaptchaSecret);
    requestMap.add("response", captchaResponse);
    RestTemplate restTemplate = (RestTemplate) applicationContext.getBean("restTemplate");
    CaptchaResponse apiResponse = restTemplate.postForObject(GOOGLE_RECAPTCHA_ENDPOINT, requestMap, CaptchaResponse.class);
    logger.info("Captcha api response {}", apiResponse);
    return apiResponse != null && apiResponse.getSuccess() && Boolean.TRUE.equals(minimun <= apiResponse.getScore());
  }

}
