package com.modyo.ms.commons.audit.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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

    List<AuditJpaEntity> auditList = adapterUnderTest.loadByParent("10", "parent1", PageRequest.of(0, 2));

    assertThat(auditList.size(), is(2));

    auditList = adapterUnderTest.loadByParent("10", "parent1", PageRequest.of(1, 2));

    assertThat(auditList.size(), is(1));

  }

}
