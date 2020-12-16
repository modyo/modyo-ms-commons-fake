package com.modyo.ms.commons.audit.persistence;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface AuditJpaRepository extends JpaRepository<AuditJpaEntity, Long> {
  List<AuditJpaEntity> findAllByAuditableParentIdAndAuditableParentType(String auditableParentId, String auditableParentType, Pageable page);

}
