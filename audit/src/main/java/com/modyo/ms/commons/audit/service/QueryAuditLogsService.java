package com.modyo.ms.commons.audit.service;

import com.modyo.ms.commons.audit.persistence.AuditJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryAuditLogsService {

  Page<AuditJpaEntity> loadByParent(String parentId, String parentType, Pageable page);

}
