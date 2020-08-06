package com.modyo.ms.commons.core.components;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class InMemoryRequestAttributesTest {

  private InMemoryRequestAttributes inMemoryRequestAttributes;
  private String existingKey = "key1";
  private String existingValue = "value1";
  private Integer existingScope = 0;

  @Before
  public void setUp() {
    inMemoryRequestAttributes = new InMemoryRequestAttributes();
    inMemoryRequestAttributes.setAttribute(existingKey, existingValue, existingScope);

  }

  @Test
  public void getAttribute_givenFound_ReturnValue() {
    Object response = inMemoryRequestAttributes.getAttribute(existingKey, existingScope);

    assertThat(response, is(existingValue));
  }

  @Test
  public void getAttribute_wrongScope_ReturnValue() {
    Object response = inMemoryRequestAttributes.getAttribute(existingKey, 99);

    assertThat(response, is(existingValue));
  }

  @Test
  public void removeAttribute_success() {
    inMemoryRequestAttributes.removeAttribute(existingKey, existingScope);

    assertNull(inMemoryRequestAttributes.getAttribute(existingKey, existingScope));
  }

  @Test
  public void getAttributeNames_success() {
    String[] responseKeys = inMemoryRequestAttributes.getAttributeNames(existingScope);

    assertThat(responseKeys.length, is(1));
    assertThat(responseKeys[0], is(existingKey));
  }

}
