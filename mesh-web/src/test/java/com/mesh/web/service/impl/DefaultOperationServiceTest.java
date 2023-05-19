package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import com.mesh.web.service.OperationContextService;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultOperationServiceTest extends BaseTest {

  @Autowired
  private DefaultOperationService defaultOperationService;




  // doOperation method test
  @Test
  public void doOperationTest() {
    // Mock operationContextService

    // Create a JsonObject object to use for testing
    JsonObject jsonObj = new JsonObject();
    jsonObj.put("$gte", "value1");
    jsonObj.put("$lt", "value2");

    // Test if value is JsonObject
    String expectedStrBuilder = " key  >= value1 and  key  < value2";
    String actualStrBuilder = defaultOperationService.doOperation("key", jsonObj);
    assertEquals(expectedStrBuilder, actualStrBuilder);

    // Test if value is not JsonObject
    Object nonJsonValue = "nonJsonValue";
    String expectedEmptyStr = "";
    String actualEmptyStr = defaultOperationService.doOperation("key", nonJsonValue);
    assertEquals(expectedEmptyStr, actualEmptyStr);
  }

  // type method test
  @Test
  public void typeTest() {
    assertEquals("$default", defaultOperationService.type());
  }

}
