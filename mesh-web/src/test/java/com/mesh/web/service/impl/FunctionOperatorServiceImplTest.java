package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FunctionOperatorServiceImplTest extends BaseTest {
  @Autowired
  private FunctionOperatorServiceImpl service;

  @Test
  public void testDoOperationStringParam() {
    String result = service.doOperation("$sum", "fieldName");
    assertEquals("sum(fieldName)", result);
  }

  @Test
  public void testDoOperationJsonArrayParam() {
    JsonArray array = new JsonArray().add("field1").add("field2");
    String result = service.doOperation("$sum", array);
    assertEquals("sum(field1, field2)", result);
  }

  @Test
  public void testDoOperationNullParam() {
    String result = service.doOperation("$sum", null);
    assertEquals("", result);
  }

  @Test
  public void testType() {
    String type = service.type();
    assertEquals(CommonConstants.FUNCTION, type);
  }


}
