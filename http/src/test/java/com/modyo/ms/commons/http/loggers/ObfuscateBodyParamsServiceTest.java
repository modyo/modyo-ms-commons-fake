package com.modyo.ms.commons.http.loggers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;

public class ObfuscateBodyParamsServiceTest {

  @Test
  public void constructor_obfuscateBody() {
    String body = "{\"public\":\"show\",\"private_string\":\"hide\",\"private_list\":[\"a\",\"b\",\"c\"],\"private_map\":{\"a\":\"a\"},\"public_map\":{\"public_child\":\"show\",\"private_int\":\"hide\"}}";
    List<String> bodyParamsToOfuscate = List.of(
        "private_string",
        "private_list",
        "private_map",
        "private_int"
    );

    String response = ObfuscateBodyParamsService.obfuscate(body, bodyParamsToOfuscate);

    assertThat(response,
        is("{\"public\":\"show\",\"private_string\":\"*********\",\"private_list\":\"*********\",\"private_map\":\"*********\",\"public_map\":{\"public_child\":\"show\",\"private_int\":\"*********\"}}"));
  }

  @Test
  public void constructor_StringBody() {
    String body = "just a simple string";
    List<String> bodyParamsToOfuscate = List.of("a");

    String response = ObfuscateBodyParamsService.obfuscate(body, bodyParamsToOfuscate);

    assertThat(response,
        is("just a simple string"));
  }

  @Test
  public void constructor_ifBodyParamsToOfuscateIsNull_ThenDoNothing() {
    String body = "just a simple string";

    String response = ObfuscateBodyParamsService.obfuscate(body, null);

    assertThat(response,
        is("just a simple string"));
  }

}
