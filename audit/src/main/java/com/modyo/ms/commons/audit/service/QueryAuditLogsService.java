package com.modyo.ms.commons.audit.service;

import com.modyo.ms.commons.audit.AuditLogType;
import com.modyo.ms.commons.audit.persistence.AuditJpaEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryAuditLogsService {

  Page<AuditJpaEntity> loadByParent(AuditQuery auditQuery, Pageable page);

  @Getter
  @Builder(builderMethodName = "hiddenBuilder")
  class AuditQuery {
    private String parentId;
    private String parentType;
    private String type;
    private String changeType;
    private String event;
    private AuditLogType logType;

    public static AuditQueryBuilder builder(String parentId, String parentType) {
      return hiddenBuilder().parentId(parentId).parentType(parentType);
    }
  }

}
