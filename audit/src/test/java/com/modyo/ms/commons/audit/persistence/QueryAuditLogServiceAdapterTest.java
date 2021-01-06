package com.modyo.ms.commons.audit.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.modyo.ms.commons.audit.AuditLogType;
import com.modyo.ms.commons.audit.service.QueryAuditLogsService.AuditQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@ContextConfiguration(classes = {
    AuditJpaRepository.class
})
@EnableJpaRepositories
@EntityScan
class QueryAuditLogServiceAdapterTest {

  @Autowired
  private AuditJpaRepository auditJpaRepository;

  private QueryAuditLogServiceAdapter adapterUnderTest;

  @BeforeEach
  void setUp() {
    adapterUnderTest = new QueryAuditLogServiceAdapter(auditJpaRepository);
  }

  @Test
  @Sql(scripts={"classpath:persistence/audits.sql"})
  void loadByParent() {

    Page<AuditJpaEntity> auditList = adapterUnderTest.loadByParent(
        AuditQuery.builder("10", "parent1")
            .build(),
        PageRequest.of(0, 2));

    assertThat(auditList.getContent().size(), is(2));
    assertThat(auditList.getTotalElements(), is(3L));
    assertThat(auditList.getNumber(), is(0));
    assertThat(auditList.getTotalPages(), is(2));

    auditList = adapterUnderTest.loadByParent( AuditQuery.builder("10", "parent1")
        .build(),
        PageRequest.of(1, 2));

    assertThat(auditList.getContent().size(), is(1));
    assertThat(auditList.getTotalElements(), is(3L));
    assertThat(auditList.getNumber(), is(1));
    assertThat(auditList.getTotalPages(), is(2));

  }

  @Test
  @Sql(scripts={"classpath:persistence/audits.sql"})
  void loadByAllParams() {

    Page<AuditJpaEntity> auditList = adapterUnderTest.loadByParent(
        AuditQuery.builder("10", "parent1")
            .type("child1")
            .changeType("CHANGE_STATUS")
            .event("Changed")
            .logType(AuditLogType.INFO)
            .build(),
        PageRequest.of(0, 2));

    assertThat(auditList.getContent().size(), is(1));
    assertThat(auditList.getTotalElements(), is(1L));
    assertThat(auditList.getNumber(), is(0));
    assertThat(auditList.getTotalPages(), is(1));

  }

}
