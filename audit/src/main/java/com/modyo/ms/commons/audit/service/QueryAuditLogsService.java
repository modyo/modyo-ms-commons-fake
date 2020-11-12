package com.modyo.ms.commons.audit.service;

import com.modyo.ms.commons.audit.persistence.AuditJpaEntity;
import java.util.List;

public interface QueryAuditLogsService {

  List<AuditJpaEntity> loadByParent(String parentId, String parentType);

}
