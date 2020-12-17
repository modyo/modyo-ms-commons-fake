package com.modyo.ms.commons.audit.persistence;

import com.modyo.ms.commons.audit.service.QueryAuditLogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class QueryAuditLogServiceAdapter implements QueryAuditLogsService {

  private final AuditJpaRepository auditJpaRepository;

  @Override
  public Page<AuditJpaEntity> loadByParent(String parentId, String parentType, Pageable page) {
    return auditJpaRepository.findAllByAuditableParentIdAndAuditableParentType(parentId, parentType, page);
  }
}
