package com.modyo.services.logger;

/**
 * Utilitario para manejo de logs
 */
public class LogUtil {

  private LogUtil() {

  }

  /**
   * Formatea mensaje de log de acuerdo a estandar banco Ejemplos de utilizacion:
   * log.debug(LogUtil.crearMsg("Mensaje sin parametros")); log.info(LogUtil.crearMsg("Mensaje con
   * un parametro {}"), parametro);
   *
   * @param msg Mensaje a formatear. Puede incluir parametros
   */
  public static String crearMsg(String msg) {
    StackTraceElement[] ste = Thread.currentThread().getStackTrace();
    String className = simpleName(ste[2].getClassName());
    String methodName = ste[2].getMethodName();

    return "[" + className + "] [" + methodName + "]" + msg;
  }

  private static String simpleName(String className) {
    int idx = className.lastIndexOf('.');
    return (idx == -1) ? className : className.substring(idx + 1);
  }
}
