package com.modyo.ms.commons.audit.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface AuditJpaRepository extends JpaRepository<AuditJpaEntity, Long> {

}
