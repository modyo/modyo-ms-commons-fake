package com.modyo.services.logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

/**
 * Este es el Aspecto que loguea lo que tenga la anotacion Loggeable
 */
@Slf4j
@Aspect
@Component
public class AspectLogger {

  @Around("@within(Loggeable) || @annotation(Loggeable)")
  public Object loggeable(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
    Method method = methodSignature.getMethod();

    String className = method.getDeclaringClass().getSimpleName();
    String methodName = method.getName();

    String arguments = this.getArguments(proceedingJoinPoint, method);

    String fmt = String.format("[%s] [%s] ", className, methodName);

    log.info(fmt + "[MODYO_INI] Inicio {}", arguments);

    long start = System.currentTimeMillis(), time = 0l;

    Object result = null;
    try {
      result = proceedingJoinPoint.proceed();
    } catch (InvocationTargetException e) {
      time = System.currentTimeMillis();

      Throwable t = e.getCause();
      log.error(fmt + "[MODYO_FIN_EX][{} ms][{}] error con mensaje: {}", (time - start),
          t.getClass().getSimpleName(), t.getMessage(), t);
      throw t;
    } catch (Exception e) {
      time = System.currentTimeMillis();

      Throwable t = e;
      log.error(fmt + "[MODYO_FIN_EX][{} ms][{}] error con mensaje: {}", (time - start),
          t.getClass().getSimpleName(), t.getMessage(), t);
      throw t;
    }
    time = System.currentTimeMillis();

    if (method.getReturnType().equals(Void.TYPE)) {
      log.info(fmt + "[MODYO_FIN_OK][{} ms]", (time - start));
    } else {
      log.info(fmt + "[MODYO_FIN_OK][{} ms] retornando objeto {}", (time - start),
          DtoFormatter.format(result));
    }
    return result;
  }

  private String getArguments(ProceedingJoinPoint proceedingJoinPoint, Method signMethod)
      throws NoSuchMethodException, SecurityException {

    Object[] args = proceedingJoinPoint.getArgs();

    Class<?>[] signParameterTypes = signMethod.getParameterTypes();
    Method method = proceedingJoinPoint.getTarget().getClass()
        .getMethod(signMethod.getName(), signParameterTypes);

    // Esto obtiene los nombres de los parametros de un metodo
    ParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
    String[] names = discoverer.getParameterNames(method);

    // Si existe la anotacion @NotLog se escriben asteriscos
    return IntStream.range(0, args.length).mapToObj(i -> {
      Parameter param = method.getParameters()[i];
      String paramValue =
          checkAnnotationByType(param) ? "[***]" : "[" + DtoFormatter.format(args[i]) + "]";
      return String.format("%s%s", (names != null ? names[i] : param.getName()), paramValue);
    }).collect(Collectors.joining(", "));
  }

  private boolean checkAnnotationByType(Parameter parameter) {
    return parameter.isAnnotationPresent(NotLog.class);
  }

}
