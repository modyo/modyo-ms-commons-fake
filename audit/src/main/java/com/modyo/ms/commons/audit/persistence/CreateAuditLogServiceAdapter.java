package com.modyo.ms.commons.audit.persistence;

import com.modyo.ms.commons.audit.AuditLogType;
import com.modyo.ms.commons.audit.service.ChangeType;
import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import com.modyo.ms.commons.core.exceptions.CustomValidationException;
import com.modyo.ms.commons.core.utils.JwtToken;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class CreateAuditLogServiceAdapter implements CreateAuditLogService {

  private final AuditJpaRepository auditJpaRepository;
  private final HttpServletRequest httpServletRequest;
  private final Optional<String> createdByTokenClaim;

  public CreateAuditLogServiceAdapter(
      final AuditJpaRepository auditJpaRepository,
      final HttpServletRequest httpServletRequest,
      @Value("${commons.audit.user-id-token-claim:#{null}}") Optional<String> createdByTokenClaim
  ) {
    this.auditJpaRepository = auditJpaRepository;
    this.httpServletRequest = httpServletRequest;
    this.createdByTokenClaim = createdByTokenClaim;
  }

  @Override
  public void log(AuditLogType logLevel, String auditableId, String auditableParentId, Object parentValue, Object initialValue,
      Object newValue, ChangeType changeType, String event) {

    AuditJpaEntity auditJpaEntity = AuditJpaEntityFactory.create(
        logLevel,
        auditableId,
        auditableParentId,
        parentValue,
        initialValue,
        newValue,
        getCreatedBy(),
        getUserAgent(),
        changeType,
        event);

    auditJpaRepository.save(auditJpaEntity);
  }

  private String getCreatedBy() {
    final String defaultName = "unknown";
    try {
      JwtToken jwtToken = new JwtToken(httpServletRequest.getHeader("Authorization"));
      return Optional.of(jwtToken)
          .map(this::getUserId)
          .orElse(defaultName);
    } catch (CustomValidationException e) {
      return defaultName;
    }
  }

  private String getUserId(JwtToken jwtToken) {
    return createdByTokenClaim
        .map(key -> (String) jwtToken.getClaims().get(key))
        .orElseGet(jwtToken::getEmail);
  }

  private String getUserAgent() {
    return httpServletRequest.getHeader("user-agent");
  }
}
