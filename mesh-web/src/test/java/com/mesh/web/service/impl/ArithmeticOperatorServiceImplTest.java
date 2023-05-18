package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import com.mesh.web.service.OperationContextService;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ArithmeticOperatorServiceImplTest extends BaseTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }
  @Autowired
  private OperationContextService operationContextService;





  @Test
  public void testDoOperation_whenValueIsJsonArray() {
    String jsonStr = """
      { "$divide": [ { "$multiply": [{ "$subtract": [ { "$add": [ "price", "fee" ] }, "discount" ] }, "column1" ] }, "column2"] }
      """;

    // Perform the test
    String result = operationContextService.parse(new JsonObject(jsonStr));
    assertEquals("((((price + fee) - discount) * column1) / column2)", result);
  }

  @Test
  public void testDoOperation_whenValueIsConstants() {
    String jsonStr = """
      { "$divide": [ 80, "column1" ] }
      """;

    // Perform the test
    String result = operationContextService.parse(new JsonObject(jsonStr));
    assertEquals("(80 / column1)", result);
  }

  @Test
  public void testDoOperation_whenValueIsOtherType() {
//    String result = operationContextService.parse("otherType");
//    assertTrue(result.isEmpty());
  }
}
