package com.modyo.ms.commons.core.aspect;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;

public class TaskAspectTest {

  private final TaskAspect aspectUnderTest = new TaskAspect();

  @Test
  public void logTasks_success() throws Throwable {
    String mockTaskName = "taskName";
    String jointPointReturnValue = "returnValue";
    ProceedingJoinPoint mockJointPoint = Mockito.mock(ProceedingJoinPoint.class);
    Signature mockSignature = Mockito.mock(Signature.class);
    when(mockJointPoint.getSignature()).thenReturn(mockSignature);
    when(mockJointPoint.toShortString()).thenReturn(mockTaskName);
    when(mockJointPoint.proceed()).thenReturn(jointPointReturnValue);

    Object response = aspectUnderTest.logTasks(mockJointPoint);

    assertThat(response, is(jointPointReturnValue));
    assertNotNull(RequestContextHolder.getRequestAttributes().getAttribute("correlationId", 0));
    then(mockJointPoint).should().proceed();

  }

  @Test
  public void logTasks_givenJointPointThrowException() throws Throwable {
    String mockTaskName = "taskName";
    ProceedingJoinPoint mockJointPoint = Mockito.mock(ProceedingJoinPoint.class);
    Signature mockSignature = Mockito.mock(Signature.class);
    when(mockJointPoint.getSignature()).thenReturn(mockSignature);
    when(mockJointPoint.toShortString()).thenReturn(mockTaskName);
    when(mockJointPoint.proceed()).thenThrow(new RuntimeException());

    Object response = aspectUnderTest.logTasks(mockJointPoint);

    assertNull(response);
    assertNotNull(RequestContextHolder.getRequestAttributes().getAttribute("correlationId", 0));
    then(mockJointPoint).should().proceed();

  }

}
