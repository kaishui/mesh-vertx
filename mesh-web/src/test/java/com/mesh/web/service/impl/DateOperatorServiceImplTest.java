package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;



public class DateOperatorServiceImplTest {

  @Autowired
  private DateOperatorServiceImpl service;

  @Test
  public void testDoOperation() {
    DateOperatorServiceImpl service = new DateOperatorServiceImpl();
    String testString = "test";
    JsonArray testArray = new JsonArray().add("a").add("b");
    JsonObject testObj = new JsonObject().put("date", "col").put("format", "%Y%m%d");

    assertEquals("CURRENT_DATE()", service.doOperation("$CURRENT_DATE", null));
    assertEquals("PARSE_DATE(test)", service.doOperation("$parseDate", testString));
    assertEquals("FORMAT_DATE('a', 'b')", service.doOperation("$formatDate", testArray));
    assertEquals("PARSE_DATE('%Y%m%d', 'col')", service.doOperation("$PARSE_DATE", testObj));
  }

  @Test
  public void testType() {
    DateOperatorServiceImpl service = new DateOperatorServiceImpl();
    assertEquals(CommonConstants.DATE_FUNCTION, service.type());
  }
}
