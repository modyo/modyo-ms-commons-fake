package com.modyo.services.logger;

import com.modyo.services.utils.ClassUtil;
import com.modyo.services.utils.ClassUtil.FieldBean;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Clase que permite formatear a un dto
 */
public class DtoFormatter {

  private static final String DATETIME_PATTERN = "dd/MM/yyyy HH:mm:ss";

  private static final String DATE_PATTERN = "dd/MM/yyyy";

  private static final String HDN = "***";

  private static final Set<Class<?>> PRIMITIVE_CLASSES = new HashSet<>();

  private static final int MAX_RESULTS = 1000;

  static {
    PRIMITIVE_CLASSES.add(Boolean.class);
    PRIMITIVE_CLASSES.add(Byte.class);
    PRIMITIVE_CLASSES.add(Character.class);
    PRIMITIVE_CLASSES.add(Short.class);
    PRIMITIVE_CLASSES.add(Integer.class);
    PRIMITIVE_CLASSES.add(Long.class);
    PRIMITIVE_CLASSES.add(Float.class);
    PRIMITIVE_CLASSES.add(Double.class);
    PRIMITIVE_CLASSES.add(boolean.class);
    PRIMITIVE_CLASSES.add(byte.class);
    PRIMITIVE_CLASSES.add(char.class);
    PRIMITIVE_CLASSES.add(short.class);
    PRIMITIVE_CLASSES.add(int.class);
    PRIMITIVE_CLASSES.add(long.class);
    PRIMITIVE_CLASSES.add(float.class);
    PRIMITIVE_CLASSES.add(double.class);

  }

  /**
   * Constructor privado para utilitario
   */
  private DtoFormatter() {

  }

  /**
   * Formatea un objeto de acuerdo al estandar del banco.
   */
  public static String format(Object obj, String... excluded) {
    if (obj == null) {
      return "null";
    }

    StringBuilder sb = new StringBuilder();
    if (obj instanceof String) {
      sb.append("\"").append((String) obj).append("\"");
    } else if (PRIMITIVE_CLASSES.contains(obj.getClass())) {
      sb.append(obj.toString());
    } else if (obj instanceof Enum) {
      sb.append(obj.toString());
    } else if (obj instanceof java.sql.Date) {
      SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
      sb.append(sdf.format((java.sql.Date) obj));
    } else if (obj instanceof Date) {
      SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_PATTERN);
      sb.append(sdf.format((Date) obj));
    } else if (obj instanceof Calendar) {
      SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_PATTERN);
      sb.append(sdf.format(((Calendar) obj).getTime()));
    } else if (obj.getClass().isArray()) {
      formatArray(obj, sb, excluded);
    } else if (obj instanceof Collection) {
      formatCollection(obj, sb, excluded);
    } else if (obj instanceof Map) {
      formatMap(obj, sb, excluded);
    } else if (obj.getClass().getPackage() != null && obj.getClass().getPackage().getName()
        .startsWith("java.")) {
      sb.append(obj.toString());
    } else {
      formatDto(obj, sb, excluded);
    }
    return sb.toString();
  }

  private static void formatArray(Object obj, StringBuilder sb, String... excluded) {
    int len = Array.getLength(obj);
    sb.append("{");
    for (int i = 0; i < len && i < MAX_RESULTS; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(format(Array.get(obj, i), excluded));
    }
    sb.append("}");
  }

  private static void formatCollection(Object obj, StringBuilder sb, String... excluded) {
    Collection<?> col = (Collection<?>) obj;
    int idx = 0;
    sb.append("[");
    for (Iterator<?> i = col.iterator(); i.hasNext() && idx < MAX_RESULTS; ) {
      Object elem = i.next();
      sb.append(format(elem, excluded));
      if (i.hasNext()) {
        sb.append(", ");
      }
      idx++;
    }
    sb.append("]");
  }

  private static void formatMap(Object obj, StringBuilder sb, String... excluded) {
    Map<?, ?> map = (Map<?, ?>) obj;
    int idx = 0;
    sb.append("{");
    for (Iterator<?> i = map.entrySet().iterator(); i.hasNext() && idx < MAX_RESULTS; ) {
      Map.Entry<?, ?> entry = (Map.Entry<?, ?>) i.next();
      sb.append(format(entry.getKey(), excluded)).append("=")
          .append(format(entry.getValue(), excluded));
      if (i.hasNext()) {
        sb.append(", ");
      }
      idx++;
    }
    sb.append("}");
  }

  private static void formatDto(Object obj, StringBuilder sb, String... excluded) {
    sb.append(obj.getClass().getSimpleName()).append(": {");
    Set<String> excSet =
        (excluded != null) ? new HashSet<>(Arrays.asList(excluded)) : Collections.emptySet();
    Map<String, FieldBean> fieldsMap = ClassUtil.getFieldsMap(obj.getClass());
    for (Iterator<Map.Entry<String, FieldBean>> i = fieldsMap.entrySet().iterator();
        i.hasNext(); ) {
      Map.Entry<String, FieldBean> fieldEntry = i.next();
      String fieldName = fieldEntry.getKey();
      FieldBean fieldBean = fieldEntry.getValue();
      sb.append(fieldName).append("[");
      if (excSet.contains(fieldName) || fieldBean.getField().isAnnotationPresent(NotLog.class)) {
        sb.append(HDN);
      } else {
        Object fieldValue = getFieldValue(obj, fieldBean);
        // Agregando las exclusiones sobre subelementos
        Set<String> subExc = new HashSet<>();
        for (String exc : excSet) {
          if (exc.startsWith(fieldName + ".")) {
            subExc.add(exc.substring(fieldName.length() + 1));
          }
        }
        String[] subExcluded = subExc.toArray(new String[subExc.size()]);
        sb.append(format(fieldValue, subExcluded));
      }
      sb.append("]");
      if (i.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append("}");
  }

  private static Object getFieldValue(Object obj, FieldBean fieldBean) {
    Object fieldValue = null;
    try {
      fieldValue = fieldBean.getGetter().invoke(obj);
    } catch (IllegalAccessException | IllegalArgumentException e) {
      // Ignored. Getter es correcto e invocable.
    } catch (InvocationTargetException e) {
      throw new IllegalArgumentException(e.getCause());
    }
    return fieldValue;
  }

}
