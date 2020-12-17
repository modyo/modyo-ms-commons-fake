package com.modyo.ms.commons.audit.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface AuditJpaRepository extends JpaRepository<AuditJpaEntity, Long> {
  Page<AuditJpaEntity> findAllByAuditableParentIdAndAuditableParentType(String auditableParentId, String auditableParentType, Pageable page);

}
