package com.modyo.ms.commons.audit.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.modyo.ms.commons.audit.AuditLogType;
import com.modyo.ms.commons.audit.service.ChangeType;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = {
    HttpServletRequest.class,
    AuditJpaRepository.class
})
@EnableJpaRepositories
@EntityScan
class CreateAuditLogServiceAdapterTest {

  @Autowired
  private AuditJpaRepository auditJpaRepository;
  @Mock
  private HttpServletRequest httpServletRequest;

  private CreateAuditLogServiceAdapter adapterUnderTest;

  private final String userAgentValue = "Firefox XXX";

  @BeforeEach
  void setUp() {
    adapterUnderTest = new CreateAuditLogServiceAdapter(auditJpaRepository, httpServletRequest, Optional.empty());
    String validToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzZWNvbmRfZmFtaWx5X25hbWUiOiJTZWNvbmRsYXN0Iiwic2Vjb25kX25hbWUiOiJTZWNvbmQiLCJnaXZlbl9uYW1lIjoiUHJvc3BlY3RvIiwiZXhwIjoxNjA1MDQ2MDA0LCJmYW1pbHlfbmFtZSI6IlVTUyIsImVtYWlsIjoicHJvc3BlY3RvQHVzcy5jb20ifQ.TeeSKYQIr_q14txS-WnK8N9nX34hR2yT-TBOlsgzmOk";
    when(httpServletRequest.getHeader(eq("Authorization"))).thenReturn(validToken);
    when(httpServletRequest.getHeader(eq("user-agent"))).thenReturn(userAgentValue);
  }

  @Test
  void log_withCreatedByAndUserAgent_success() {
    adapterUnderTest.log(AuditLogType.INFO, "id", "pid", new Object(), new Object(), new Object(), ChangeType.CHANGE_STATUS, "changed");

    List<AuditJpaEntity> auditJpaEntityList = auditJpaRepository.findAll();
    assertThat(auditJpaEntityList.size(), is(1));
    assertThat(auditJpaEntityList.get(0).getLogType(), is(AuditLogType.INFO));
    assertThat(auditJpaEntityList.get(0).getCreatedBy(), is("prospecto@uss.com"));
    assertThat(auditJpaEntityList.get(0).getUserAgent(), is(userAgentValue));
  }

  @Test
  void log_withAlternativeCreatedByTokenClaim_success() {
    CreateAuditLogServiceAdapter customAdapterUnderTest
        = new CreateAuditLogServiceAdapter(auditJpaRepository, httpServletRequest, Optional.of("given_name"));

    customAdapterUnderTest.log(AuditLogType.INFO, "id", "pid", new Object(), new Object(), new Object(), ChangeType.CHANGE_STATUS, "changed");

    List<AuditJpaEntity> auditJpaEntityList = auditJpaRepository.findAll();
    assertThat(auditJpaEntityList.size(), is(1));
    assertThat(auditJpaEntityList.get(0).getLogType(), is(AuditLogType.INFO));
    assertThat(auditJpaEntityList.get(0).getCreatedBy(), is("Prospecto"));
  }

  @Test
  void log_WhenNoValidToken_ThenCreatedByIsUnknown() {
    when(httpServletRequest.getHeader(eq("Authorization"))).thenReturn("invalid token");
    adapterUnderTest.log(AuditLogType.INFO, "id", "pid", new Object(), new Object(), new Object(), ChangeType.CHANGE_STATUS, "changed");

    List<AuditJpaEntity> auditJpaEntityList = auditJpaRepository.findAll();
    assertThat(auditJpaEntityList.size(), is(1));
    assertThat(auditJpaEntityList.get(0).getLogType(), is(AuditLogType.INFO));
    assertThat(auditJpaEntityList.get(0).getCreatedBy(), is("unknown"));
  }

  @Test
  void log_WhenAuthorizationHeaderIsNull_ThenCreatedByIsUnknown() {
    when(httpServletRequest.getHeader(eq("Authorization"))).thenReturn(null);
    adapterUnderTest.log(AuditLogType.INFO, "id", "pid", new Object(), new Object(), new Object(), ChangeType.CHANGE_STATUS, "changed");

    List<AuditJpaEntity> auditJpaEntityList = auditJpaRepository.findAll();
    assertThat(auditJpaEntityList.size(), is(1));
    assertThat(auditJpaEntityList.get(0).getLogType(), is(AuditLogType.INFO));
    assertThat(auditJpaEntityList.get(0).getCreatedBy(), is("unknown"));
  }

  @Test
  void log_WhenUserAgentHeaderIsNull_ThenUserAgentByIsNull() {
    when(httpServletRequest.getHeader(eq("user-agent"))).thenReturn(null);
    adapterUnderTest.log(AuditLogType.INFO, "id", "pid", new Object(), new Object(), new Object(), ChangeType.CHANGE_STATUS, "changed");

    List<AuditJpaEntity> auditJpaEntityList = auditJpaRepository.findAll();
    assertThat(auditJpaEntityList.size(), is(1));
    assertThat(auditJpaEntityList.get(0).getLogType(), is(AuditLogType.INFO));
    assertNull(auditJpaEntityList.get(0).getUserAgent());
  }

}
