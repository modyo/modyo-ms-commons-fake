package com.modyo.ms.commons.audit.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.modyo.ms.commons.audit.service.ChangeType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = {
    CreateAuditLogServiceAdapter.class,
    AuditJpaRepository.class
})
@EnableJpaRepositories
@EntityScan
class CreateAuditLogServiceAdapterTest {

  @Autowired
  private AuditJpaRepository auditJpaRepository;

  private CreateAuditLogServiceAdapter adapterUnderTest;

  @BeforeEach
  void setUp() {
    adapterUnderTest = new CreateAuditLogServiceAdapter(auditJpaRepository);
  }

  @Test
  void logInfo_success() {
    adapterUnderTest.logInfo("id", new Object(), new Object(), ChangeType.CHANGE_STATUS, "changed");

    List<AuditJpaEntity> auditJpaEntityList = auditJpaRepository.findAll();
    assertThat(auditJpaEntityList.size(), is(1));
    assertThat(auditJpaEntityList.get(0).getLogLevel(), is(LogLevel.INFO));
  }

  @Test
  void logInfo_withParent_success() {
    adapterUnderTest.logInfo("id", "pid", new Object(), new Object(), new Object(), ChangeType.CHANGE_STATUS, "changed");

    List<AuditJpaEntity> auditJpaEntityList = auditJpaRepository.findAll();
    assertThat(auditJpaEntityList.size(), is(1));
    assertThat(auditJpaEntityList.get(0).getLogLevel(), is(LogLevel.INFO));
  }

  @Test
  void logError_success() {
    adapterUnderTest.logError("id", new Object(), new Object(), ChangeType.CHANGE_STATUS, "changed");

    List<AuditJpaEntity> auditJpaEntityList = auditJpaRepository.findAll();
    assertThat(auditJpaEntityList.size(), is(1));
    assertThat(auditJpaEntityList.get(0).getLogLevel(), is(LogLevel.ERROR));
  }

  @Test
  void logError_withParent_success() {
    adapterUnderTest.logError("id", "pid",
        new Object(), new Object(), new Object(),
        ChangeType.CHANGE_STATUS, "changed");

    List<AuditJpaEntity> auditJpaEntityList = auditJpaRepository.findAll();
    assertThat(auditJpaEntityList.size(), is(1));
    assertThat(auditJpaEntityList.get(0).getLogLevel(), is(LogLevel.ERROR));
  }


}
