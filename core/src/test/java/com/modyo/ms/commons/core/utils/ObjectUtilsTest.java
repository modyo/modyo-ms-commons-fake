package com.modyo.ms.commons.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class ObjectUtilsTest {

  private final ObjectMapper mapper = new ObjectMapper();

  private ExampleObject object1;
  private ExampleObject object2;

  @Before
  public void beforeEach() throws IOException {
    object1 = mapper.readValue(
        new ClassPathResource("utils/example1.json").getFile(),
        ExampleObject.class);

    object2 = mapper.readValue(
        new ClassPathResource("utils/example2.json").getFile(),
        ExampleObject.class);
  }

  @Test
  public void mergeCase1() {
    ObjectUtils.merge(object1, object2);
    assertEquals(object1, object2);
  }

  @Test
  public void mergeCase2() {
    object1 = new ExampleObject();

    ObjectUtils.merge(object1, object2);
    assertEquals(object1, object2);
  }

  @Test
  public void mergeCase3() throws IOException {
    ExampleObject object1Copy =  mapper.readValue(
        new ClassPathResource("utils/example1.json").getFile(),
        ExampleObject.class);
    object2 = new ExampleObject();

    ObjectUtils.merge(object1, object2);
    assertEquals(object1Copy, object1);
  }

  @Test
  public void mergeCase4() throws IOException {
    ExampleObject object1Copy =  mapper.readValue(
        new ClassPathResource("utils/example1.json").getFile(),
        ExampleObject.class);

    ObjectUtils.merge(object1.getNestedObject(), object1);
    assertEquals(object1Copy, object1);
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  static class ExampleObject {

    private Integer number;
    private String world;
    private ExampleNestedObject nestedObject;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  static class ExampleNestedObject {

    private Integer nestedNumber;
    private String nestedWorld;
    private List<ExampleNestedObject> nestedObjects;

  }
}
