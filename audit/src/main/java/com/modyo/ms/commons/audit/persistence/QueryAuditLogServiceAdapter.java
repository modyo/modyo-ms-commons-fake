package com.modyo.ms.commons.audit.persistence;

import com.modyo.ms.commons.audit.service.QueryAuditLogsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class QueryAuditLogServiceAdapter implements QueryAuditLogsService {

  private final AuditJpaRepository auditJpaRepository;

  @Override
  public List<AuditJpaEntity> loadByParent(String parentId, String parentType) {
    return auditJpaRepository.findAllByAuditableParentIdAndAuditableParentType(parentId, parentType);
  }
}
