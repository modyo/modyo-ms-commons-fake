package com.modyo.ms.commons.audit.aspect;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.modyo.ms.commons.audit.aspect.AuditAspect.ErrorMessageDto;
import com.modyo.ms.commons.audit.service.ChangeType;
import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import com.modyo.ms.commons.core.components.InMemoryRequestAttributes;
import com.modyo.ms.commons.core.exceptions.BusinessErrorException;
import java.lang.annotation.Annotation;
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

  @BeforeEach
  void setUp() throws NoSuchMethodException {
    RequestContextHolder.setRequestAttributes(new InMemoryRequestAttributes());
    MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(this.getClass().getMethod("testModyoAuditMethod"));
  }

  @ModyoAudit(changeType = ChangeType.CHANGE_STATUS, event = "my event")
  public void testModyoAuditMethod() {

  }

  @Test
  void audit_WhenJoinPointExecutes_ThenLogSuccess() throws Throwable {
    AuditContext.setInitialInfo(parentEntity, parentEntityId, childEntityBefore, childEntityId);
    AuditContext.setNewValue(childEntityAfter);
    when(joinPoint.proceed()).thenReturn(joinPointResponse);

    aspectUnderTest.audit(joinPoint);

    then(createAuditLogService).should().logSuccess(
        childEntityId, parentEntityId, parentEntity,
        childEntityBefore, childEntityAfter, ChangeType.CHANGE_STATUS, "my event"
    );

  }

  @Test
  void audit_WhenJoinPointExecutes_ButLogSuccessFails_ThenDoNotThrowException() throws Throwable {
    AuditContext.setInitialInfo(parentEntity, parentEntityId, childEntityBefore, childEntityId);
    AuditContext.setNewValue(childEntityAfter);
    when(joinPoint.proceed()).thenReturn(joinPointResponse);
    doThrow(IllegalArgumentException.class).when(createAuditLogService).logInfo(anyString(), anyString(), any(), any(), any(), any(), anyString());

    aspectUnderTest.audit(joinPoint);

    then(createAuditLogService).should().logSuccess(
        childEntityId, parentEntityId, parentEntity,
        childEntityBefore, childEntityAfter, ChangeType.CHANGE_STATUS, "my event"
    );

  }

  @Test
  void audit_WhenJoinPointThrowsException_ThenLogError() throws Throwable {
    AuditContext.setInitialInfo(parentEntity, parentEntityId, childEntityBefore, childEntityId);
    when(joinPoint.proceed()).thenThrow(new BusinessErrorException("business", null));

    assertThrows(BusinessErrorException.class, () -> aspectUnderTest.audit(joinPoint));

    then(createAuditLogService).should().logError(
        eq(childEntityId), eq(parentEntityId), eq(parentEntity),
        eq(childEntityBefore), any(ErrorMessageDto.class), eq(ChangeType.CHANGE_STATUS), eq("my event")
    );

  }

  @Test
  void audit_WhenJoinPointThrowsException_ButLogErrorFails_ThenThrowOriginalException() throws Throwable {
    AuditContext.setInitialInfo(parentEntity, parentEntityId, childEntityBefore, childEntityId);
    when(joinPoint.proceed()).thenThrow(new BusinessErrorException("business", null));
    doThrow(IllegalArgumentException.class).when(createAuditLogService).logError(anyString(), anyString(), any(), any(), any(), any(), anyString());

    assertThrows(BusinessErrorException.class, () -> aspectUnderTest.audit(joinPoint));

    then(createAuditLogService).should().logError(
        eq(childEntityId), eq(parentEntityId), eq(parentEntity),
        eq(childEntityBefore), any(ErrorMessageDto.class), eq(ChangeType.CHANGE_STATUS), eq("my event")
    );

  }

  static class ModyoAuditTestImpl implements ModyoAudit {

    @Override
    public ChangeType changeType() {
      return ChangeType.CHANGE_STATUS;
    }

    @Override
    public String event() {
      return "my event";
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return null;
    }
  }

}
