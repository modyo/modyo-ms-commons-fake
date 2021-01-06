package com.modyo.ms.commons.audit.persistence;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

import com.modyo.ms.commons.audit.service.QueryAuditLogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class QueryAuditLogServiceAdapter implements QueryAuditLogsService {

  private final AuditJpaRepository auditJpaRepository;

  @Override
  public Page<AuditJpaEntity> loadByParent(AuditQuery auditQuery, Pageable page) {
    ExampleMatcher matcher = ExampleMatcher.matching()
        .withMatcher("auditableParentId", exact())
        .withMatcher("auditableParentType", contains().ignoreCase())
        .withMatcher("auditable_type", contains())
        .withMatcher("changeType", contains())
        .withMatcher("event", contains())
        .withMatcher("logType", contains())
        .withIgnoreCase()
        .withIgnoreNullValues();

    AuditJpaEntity exampleAuditEntity = new AuditJpaEntity();
    exampleAuditEntity.setAuditableParentId(auditQuery.getParentId());
    exampleAuditEntity.setAuditableParentType(auditQuery.getParentType());
    exampleAuditEntity.setAuditableType(auditQuery.getType());
    exampleAuditEntity.setChangeType(auditQuery.getChangeType());
    exampleAuditEntity.setEvent(auditQuery.getEvent());
    exampleAuditEntity.setLogType(auditQuery.getLogType());

    Example<AuditJpaEntity> queryAudit = Example.of(exampleAuditEntity, matcher);
    return auditJpaRepository.findAll(queryAudit, page);
  }
}
