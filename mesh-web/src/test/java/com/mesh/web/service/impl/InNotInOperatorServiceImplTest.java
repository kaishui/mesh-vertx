package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InNotInOperatorServiceImplTest extends BaseTest {

  @Autowired
  private InNotInOperatorServiceImpl inNotInOperatorServiceImpl;

  // Test $in operator
  @Test
  public void testDoOperationIn() {
    String key = "$in";
    Object value = new JsonArray().add("a").add("b").add("c");
    String result = inNotInOperatorServiceImpl.doOperation(key, value);
    assertEquals(result, " in ('a', 'b', 'c')");
  }

  // Test $nin operator
  @Test
  public void testDoOperationNin() {
    String key = "$nin";
    Object value = new JsonArray().add("a").add("b").add("c");
    String result = inNotInOperatorServiceImpl.doOperation(key, value);
    assertEquals(result, " not in ('a', 'b', 'c')");
  }

  // Test default operator
  @Test
  public void testDoOperationDefault() {
    String key = "<any-other-operator>";
    Object value = new JsonArray().add("a").add("b").add("c");
    String result = inNotInOperatorServiceImpl.doOperation(key, value);
    assertEquals(result, "");
  }


}
