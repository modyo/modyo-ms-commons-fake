package com.modyo.commons.core.tests;

import com.modyo.commons.core.util.ClassUtils;
import com.modyo.commons.core.util.ClassUtils.FieldBean;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Test para clases Dto con Lombok
 */

public class DtoCoberturaBaseTest {

  /**
   * Ejecuta el test de cobertura de los Dto pasados como parametro
   */
  protected void generalTest(List<Class<?>> dtoClasses) {

    for (Class<?> dtoClass : dtoClasses) {

      Object dto = createInstance(dtoClass, true);

      //Para comparar con atributos iguales hasta un cierto punto, permitiendo pasar por todos los pasos
      Map<String, FieldBean> fieldsMap = ClassUtils.getFieldsMap(dto.getClass());
      Object[] dtoClones = new Object[fieldsMap.size()];
      Object[] dtoClonesInv = new Object[fieldsMap.size()];
      for (int i = 0; i < dtoClones.length; i++) {
        Object dtoClon = createInstance(dtoClass, false);
        clone(dto, dtoClon, fieldsMap, i);
        dtoClones[i] = dtoClon;
        Object dtoClonInv = createInstance(dtoClass, false);
        clone(dto, dtoClonInv, fieldsMap, i);
        dtoClonesInv[i] = dtoClonInv;
      }

      Object other = createInstance(dtoClass, true);

      Object otherEmpty = createInstance(dtoClass, false);

      //Comparando para utilizar equals y hashCode en distintos caminos
      dto.equals(dto);
      dto.equals(new Object());
      for (int i = 0; i < dtoClones.length; i++) {
        dto.equals(dtoClones[i]);
        dto.equals(dtoClonesInv[i]);
        dtoClones[i].equals(dto);
        dtoClonesInv[i].equals(dto);
        dtoClones[i].equals(dtoClones[i]);
        dtoClonesInv[i].equals(dtoClonesInv[i]);
      }
      for (Object dtoClone : dtoClones) {
        for (Object o : dtoClonesInv) {
          dtoClone.equals(o);
          o.equals(dtoClone);
        }
      }
      dto.equals(other);
      dto.equals(otherEmpty);
      otherEmpty.equals(other);
      dto.hashCode();
      otherEmpty.hashCode();
      dto.toString();

    }
  }

  //Nota: esto sólo sirve para los DTO. No colocar interfaces.

  private Object createInstance(Class<?> clz) {
    return createInstance(clz, null, true);
  }

  private Object createInstance(Class<?> clz, boolean fillFields) {
    return createInstance(clz, null, fillFields);
  }

  private Object createInstance(Class<?> clz, ParameterizedType parameterizedType,
      boolean fillFields) {

    Object res = null;

    String mensajeRequiereParamList = "Se requiere parameterizedType para el tipo List";
    String mensajeRequiereParamMap = "Se requiere parameterizedType para el tipo Map";
    //Creando objetos básicos
    if (String.class.isAssignableFrom(clz)) {
      res = UUID.randomUUID().toString();
    } else if (Boolean.class.isAssignableFrom(clz) || clz == boolean.class) {
      res = Math.random() > 0.5;
    } else if (Byte.class.isAssignableFrom(clz) || clz == byte.class) {
      res = (byte) new Random().nextInt(128);
    } else if (Character.class.isAssignableFrom(clz) || clz == char.class) {
      res = (char) new Random().nextInt(16384);
    } else if (Short.class.isAssignableFrom(clz) || clz == short.class) {
      res = (short) new Random().nextInt(16384);
    } else if (Integer.class.isAssignableFrom(clz) || clz == int.class) {
      res = new Random().nextInt(1000);
    } else if (Long.class.isAssignableFrom(clz) || clz == long.class) {
      res = (long) new Random().nextInt(1000);
    } else if (Float.class.isAssignableFrom(clz) || clz == float.class) {
      res = (float) Math.random();
    } else if (Double.class.isAssignableFrom(clz) || clz == double.class) {
      res = Math.random();
    } else if (Date.class.isAssignableFrom(clz)) {
      res = new Date(System.currentTimeMillis());
    } else if (Timestamp.class.isAssignableFrom(clz)) {
      res = new Timestamp(System.currentTimeMillis());
    } else if (java.util.Date.class.isAssignableFrom(clz)) {
      res = new java.util.Date();
    } else if (Calendar.class.isAssignableFrom(clz)) {
      res = Calendar.getInstance();
    } else if (LocalDate.class.isAssignableFrom(clz)) {
      res = LocalDate.now();
    } else if (LocalDateTime.class.isAssignableFrom(clz)) {
      res = LocalDateTime.now();
    } else if (ZonedDateTime.class.isAssignableFrom(clz)) {
      res = ZonedDateTime.now();
    } else if (Instant.class.isAssignableFrom(clz)) {
      res = Instant.now();
    } else if (BigInteger.class.isAssignableFrom(clz)) {
      res = BigInteger.valueOf(new Random().nextInt(1000));
    } else if (BigDecimal.class.isAssignableFrom(clz)) {
      res = BigDecimal.valueOf(Math.random());
    } else if (clz.isEnum()) {
      Object[] values = clz.getEnumConstants();
      res = values[new Random().nextInt(values.length)];
    } else if (clz.isArray()) {
      int len = 1 + new Random().nextInt(10);
      Object array = Array.newInstance(clz.getComponentType(), len);
      for (int i = 0; i < len; i++) {
        Array.set(array, i, createInstance(clz.getComponentType()));
      }
      res = array;
    } else if (Set.class.isAssignableFrom(clz)) {
      if (parameterizedType == null) {
        throw new RuntimeException(mensajeRequiereParamList);
      }
      int len = 1 + new Random().nextInt(10);
      Set<Object> list = new HashSet<>();
      for (int i = 0; i < len; i++) {
        list.add(createInstance(parameterizedType, 0));
      }
      res = list;
    } else if (List.class.isAssignableFrom(clz)) {
      if (parameterizedType == null) {
        throw new RuntimeException(mensajeRequiereParamList);
      }
      int len = 1 + new Random().nextInt(10);
      List<Object> list = new ArrayList<>();
      for (int i = 0; i < len; i++) {
        list.add(createInstance(parameterizedType, 0));
      }
      res = list;
    } else if (Collection.class.isAssignableFrom(clz)) {
      if (parameterizedType == null) {
        throw new RuntimeException(mensajeRequiereParamList);
      }
      int len = 1 + new Random().nextInt(10);
      Collection<Object> list = new ArrayList<>();
      for (int i = 0; i < len; i++) {
        list.add(createInstance(parameterizedType, 0));
      }
      res = list;
    } else if (Map.class.isAssignableFrom(clz)) {
      if (parameterizedType == null) {
        throw new RuntimeException(mensajeRequiereParamMap);
      }
      int len = 1 + new Random().nextInt(10);
      Map<Object, Object> map = new HashMap<>();
      for (int i = 0; i < len; i++) {
        map.put(createInstance(parameterizedType, 0), createInstance(parameterizedType, 1));
      }
      res = map;
    } else {

      Constructor<?>[] cs = clz.getConstructors();

      boolean isLombok = false;
      Object builderObj = null;

      if (cs.length == 0) {
        //Si utiliza lombok, hay que crearlo a través del builder. No sirve preguntar por @Builder porque tiene retention source,
        //con lo que no se detecta en runtime. Se debe preguntar directamente por el método estático builder()
        Method builderMethod = null;
        try {
          builderMethod = clz.getMethod("builder");
        } catch (NoSuchMethodException e) {
          //Se continua con el constructor normal
        } catch (SecurityException e) {
          throw new RuntimeException(e);
        }

        if (builderMethod != null) {
          isLombok = true;
          try {
            builderObj = builderMethod.invoke(null);
            Method buildMethod = builderObj.getClass().getMethod("build");
            res = buildMethod.invoke(builderObj);
          } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
          }
        } else {
          try {
            res = clz.newInstance();
          } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        }
      } else {

        Object first = null;

        //Utilizando constructores
        for (Constructor<?> c : cs) {

          Class<?>[] paramTypes = c.getParameterTypes();
          Parameter[] paramDefs = c.getParameters();

          Object[] params = new Object[paramTypes.length];
          for (int j = 0; j < paramTypes.length; j++) {
            if (paramDefs[j].getParameterizedType() instanceof ParameterizedType) {
              params[j] = createInstance(paramTypes[j],
                  (ParameterizedType) paramDefs[j].getParameterizedType(), false);
            } else {
              params[j] = createInstance(paramTypes[j]);
            }
          }

          Object instance = null;

          try {
            instance = c.newInstance(params);
          } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
              | InvocationTargetException e) {
            throw new RuntimeException("Error al crear instancia. Constructor: " + c, e);
          }

          //De preferencia utiliza la instancia de constructor vacío
          if (paramTypes.length == 0) {
            res = instance;
          }

          if (first == null) {
            first = instance;
          }
        }

        //Si no hay instancia de constructor sin parámetros, utiliza la primera que haya construido.
        if (res == null) {
          res = first;
        }
      }

      if (fillFields) {

        //Creando atributos, asignandolos con el setter y leyéndolos con el getter
        Map<String, FieldBean> fieldsMap = ClassUtils.getFieldsMap(clz);
        for (Entry<String, FieldBean> fieldEntry : fieldsMap.entrySet()) {
          FieldBean fieldBean = fieldEntry.getValue();
          if (!fieldBean.getField().getGenericType().toString().contains(clz.getName())) {
            Method getter = fieldBean.getGetter();
            Method setter = fieldBean.getSetter();
            Field field = fieldBean.getField();
            Object fieldObj;
            if (field.getGenericType() instanceof ParameterizedType) {
              fieldObj = createInstance(field.getType(), (ParameterizedType) field.getGenericType(),
                  fillFields);
            } else {
              fieldObj = createInstance(field.getType(), fillFields);
            }
            try {
              setter.invoke(res, fieldObj);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
              throw new RuntimeException(e);
            }
            try {
              getter.invoke(res);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
              throw new RuntimeException(e);
            }

            //Si es lombok, se utilizan los métodos del builder, que se llaman igual que los atributos
            if (isLombok) {
              try {
                Method builderSetter = builderObj.getClass()
                    .getMethod(field.getName(), field.getType());
                builderObj = builderSetter.invoke(builderObj, fieldObj);
                Method toString = builderObj.getClass().getMethod("toString");
                toString.invoke(builderObj);
              } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
              }
            }
          }
        }
      }
    }

    return res;
  }

  private Object createInstance(ParameterizedType parameterizedType, int index) {
    Type type = parameterizedType.getActualTypeArguments()[index];
    Object instance = null;
    if (Class.class.isAssignableFrom(type.getClass())) {
      instance = createInstance((Class<?>) type);
    } else if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
      ParameterizedType paramType = (ParameterizedType) type;
      instance = createInstance((Class<?>) paramType.getRawType(), paramType, false);
    }

    return instance;
  }

  private void clone(Object fromObj, Object toObj, Map<String, FieldBean> fieldsMap,
      int idx) {

    int i = 0;
    //Creando atributos, asignandolos con el setter y leyéndolos con el getter, hasta un cierto campo
    for (Entry<String, FieldBean> fieldEntry : fieldsMap.entrySet()) {
      if (i == idx) {
        break;
      }
      FieldBean fieldBean = fieldEntry.getValue();
      Method getter = fieldBean.getGetter();
      Method setter = fieldBean.getSetter();
      Object fieldObj;
      try {
        fieldObj = getter.invoke(fromObj);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
      try {
        setter.invoke(toObj, fieldObj);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
      i++;
    }
  }
}
