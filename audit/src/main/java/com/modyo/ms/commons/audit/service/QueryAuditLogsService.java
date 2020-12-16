package com.modyo.ms.commons.audit.service;

import com.modyo.ms.commons.audit.persistence.AuditJpaEntity;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface QueryAuditLogsService {

  List<AuditJpaEntity> loadByParent(String parentId, String parentType, Pageable page);

}
