package com.modyo.commons.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Calse utilitaria
 */
public class ClassUtils {

  private static final Map<String, Map<String, FieldBean>> FIELDS_MAP_CACHE = new HashMap<>();

  /**
   * Constructor privado para utilitario
   */
  private ClassUtils() {

  }

  public static Map<String, FieldBean> getFieldsMap(Class<?> objClass) {
    Map<String, FieldBean> fieldsMap = FIELDS_MAP_CACHE.get(objClass.getName());
    if (fieldsMap == null) {
      fieldsMap = new LinkedHashMap<>();
      Field[] fields = objClass.getDeclaredFields();
      for (Field field : fields) {
        String fieldName = field.getName();
        // Viendo si tiene getter
        String getterName = accessorName(fieldName, "get");
        Method getter = getGetter(objClass, getterName);

        if ((getter == null) && (field.getType() == boolean.class
            || field.getType() == Boolean.class)) {
          getterName = accessorName(fieldName, "is");
          getter = getGetter(objClass, getterName);

        }
        if (getter != null) {
          String setterName = accessorName(fieldName, "set");
          Method setter = getSetter(objClass, setterName, field.getType());
          FieldBean fieldBean = new FieldBean(field, getter, setter);
          fieldsMap.put(fieldName, fieldBean);
        }
      }

      // Agregando los atributos de la superclase
      if (objClass.getSuperclass() != null) {
        Map<String, FieldBean> superFieldsMap = getFieldsMap(objClass.getSuperclass());
        fieldsMap.putAll(superFieldsMap);
      }
      FIELDS_MAP_CACHE.put(objClass.getName(), fieldsMap);
    }

    return fieldsMap;
  }

  private static String accessorName(String fieldName, String prefix) {

    StringBuilder sb = new StringBuilder();
    sb.append(prefix);

    sb.append(fieldName.substring(0, 1).toUpperCase());

    if (fieldName.length() > 1) {
      sb.append(fieldName.substring(1));
    }

    return sb.toString();
  }

  private static Method getGetter(Class<?> clz, String methodName) {
    Method method = null;
    try {
      method = clz.getMethod(methodName);
    } catch (NoSuchMethodException e) {

    } catch (SecurityException e) {
      // Ignored
    }
    return method;
  }

  private static Method getSetter(Class<?> clz, String methodName, Class<?> type) {
    Method method = null;
    try {
      method = clz.getMethod(methodName, type);
    } catch (NoSuchMethodException e) {

    } catch (SecurityException e) {
      // Ignored
    }
    return method;
  }

  /**
   * Contiene informacion de atributo y sus accessors
   */
  public static class FieldBean {

    private final Field field;

    private final Method getter;

    private final Method setter;

    public FieldBean(Field field, Method getter, Method setter) {
      this.field = field;
      this.getter = getter;
      this.setter = setter;
    }

    public Field getField() {
      return field;
    }

    public Method getGetter() {
      return getter;
    }

    public Method getSetter() {
      return setter;
    }

  }

}
