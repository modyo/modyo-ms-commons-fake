package com.modyo.ms.commons.audit.aspect;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.modyo.ms.commons.audit.AuditLogType;
import com.modyo.ms.commons.audit.aspect.AuditAspect.ErrorMessageDto;
import com.modyo.ms.commons.audit.aspect.context.AuditSetContext;
import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import com.modyo.ms.commons.core.components.InMemoryRequestAttributes;
import com.modyo.ms.commons.core.exceptions.BusinessErrorException;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;

class AuditAspectTest {

  private final CreateAuditLogService createAuditLogService
      = Mockito.mock(CreateAuditLogService.class);
  private final ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);

  private final AuditAspect aspectUnderTest = new AuditAspect(createAuditLogService);

  private final Object parentEntity = new Object();
  private final Object childEntityBefore = new Object();
  private final Object childEntityAfter = new Object();
  private final String parentEntityId = "pid";
  private final String childEntityId = "cid";

  private final Object joinPointResponse = new Object();
  private final MethodSignature methodSignature = Mockito.mock(MethodSignature.class);

  @BeforeEach
  void setUp() throws NoSuchMethodException {
    RequestContextHolder.setRequestAttributes(new InMemoryRequestAttributes());
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(this.getClass().getMethod("testModyoAuditMethod"));
  }

  @ModyoAudit(changeType = "CHANGE_STATUS", event = "my event")
  public void testModyoAuditMethod() {
  }
  @ModyoAudit(prefix = "prefix", changeType = "CHANGE_STATUS", event = "my event")
  public void testModyoAuditMethodWithPrefix() {

  }

  @Test
  void audit_WhenJoinPointExecutes_ThenLogSuccess() throws Throwable {
    AuditSetContext.setParentEntityAndInitialInfo("", parentEntity, parentEntityId, childEntityBefore, childEntityId);
    AuditSetContext.setNewValue("", childEntityAfter);
    when(joinPoint.proceed()).thenReturn("", joinPointResponse);

    aspectUnderTest.audit(joinPoint);

    then(createAuditLogService).should().log(AuditLogType.SUCCESS,
        childEntityId, parentEntityId, parentEntity,
        childEntityBefore, childEntityAfter, "CHANGE_STATUS", "my event"
    );

    System.out.println(Arrays.toString(RequestContextHolder.currentRequestAttributes().getAttributeNames(0)));
    assertThat(RequestContextHolder.getRequestAttributes().getAttribute(
        "audit_entity_id", 0),
        is(childEntityId));
    assertThat(RequestContextHolder.getRequestAttributes().getAttribute(
        "current_audit_entity_id", 0),
        is(childEntityId));

  }

  @Test
  void audit_WhenJoinPointExecutes_ButLogSuccessFails_ThenDoNotThrowException() throws Throwable {
    AuditSetContext.setParentEntityAndInitialInfo("", parentEntity, parentEntityId, childEntityBefore, childEntityId);
    AuditSetContext.setNewValue("", childEntityAfter);
    when(joinPoint.proceed()).thenReturn(joinPointResponse);
    doThrow(IllegalArgumentException.class)
        .when(createAuditLogService).log(any(), anyString(), anyString(), any(), any(), any(), any(), anyString());

    aspectUnderTest.audit(joinPoint);

    then(createAuditLogService).should().log(AuditLogType.SUCCESS,
        childEntityId, parentEntityId, parentEntity,
        childEntityBefore, childEntityAfter, "CHANGE_STATUS", "my event"
    );

  }

  @Test
  void audit_WhenJoinPointThrowsException_ThenLogError() throws Throwable {
    AuditSetContext.setParentEntityAndInitialInfo("", parentEntity, parentEntityId, childEntityBefore, childEntityId);
    when(joinPoint.proceed()).thenThrow(new BusinessErrorException("business", null));

    assertThrows(BusinessErrorException.class, () -> aspectUnderTest.audit(joinPoint));

    then(createAuditLogService).should().log(eq(AuditLogType.ERROR),
        eq(childEntityId), eq(parentEntityId), eq(parentEntity),
        eq(childEntityBefore), any(ErrorMessageDto.class), eq("CHANGE_STATUS"), eq("my event")
    );

  }

  @Test
  void audit_WhenJoinPointThrowsException_ButLogErrorFails_ThenThrowOriginalException() throws Throwable {
    AuditSetContext.setParentEntityAndInitialInfo("", parentEntity, parentEntityId, childEntityBefore, childEntityId);
    when(joinPoint.proceed()).thenThrow(new BusinessErrorException("business", null));
    doThrow(IllegalArgumentException.class)
        .when(createAuditLogService).log(any(), anyString(), anyString(), any(), any(), any(), any(), anyString());

    assertThrows(BusinessErrorException.class, () -> aspectUnderTest.audit(joinPoint));

    then(createAuditLogService).should().log(eq(AuditLogType.ERROR),
        eq(childEntityId), eq(parentEntityId), eq(parentEntity),
        eq(childEntityBefore), any(ErrorMessageDto.class), eq("CHANGE_STATUS"), eq("my event")
    );

  }

  @Test
  void audit_WhenPrefixSet_ThenSaveWithPrefix() throws Throwable {
    when(methodSignature.getMethod()).thenReturn(this.getClass().getMethod("testModyoAuditMethodWithPrefix"));
    AuditSetContext.setParentEntityAndInitialInfo("prefix", parentEntity, parentEntityId, childEntityBefore, childEntityId);
    AuditSetContext.setNewValue("prefix", childEntityAfter);
    when(joinPoint.proceed()).thenReturn(joinPointResponse);

    aspectUnderTest.audit(joinPoint);

    assertThat(RequestContextHolder.getRequestAttributes().getAttribute(
        "prefix_audit_entity_id", 0),
        is(childEntityId));
    assertThat(RequestContextHolder.getRequestAttributes().getAttribute(
        "current_audit_entity_id", 0),
        is(childEntityId));

  }

}
