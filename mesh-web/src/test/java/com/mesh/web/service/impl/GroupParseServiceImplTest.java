package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupParseServiceImplTest extends BaseTest {

  @Autowired
  private GroupParseServiceImpl groupParseService;

  @Test
  void type() {
    assertEquals("$group", groupParseService.type());
  }

  @Test
  public void testParseMethodForJsonObjectValue() {
    JsonObject jsonObj = new JsonObject();
    jsonObj.put("_id", "name");

    String expectedExpression = "group by name";
    assertEquals(expectedExpression, groupParseService.parse(jsonObj));
  }

  @Test
  public void testParseMethodForJsonArrayValue() {
    JsonObject jsonObj = new JsonObject();
    JsonArray jsonArray = new JsonArray().add("name").add("age");
    jsonObj.put("_id", jsonArray);

    String expectedExpression = "group by name, age";
    assertEquals(expectedExpression, groupParseService.parse(jsonObj));
  }

  @Test
  public void testParseMethodForDifferentTypeValue() {
    Integer value = 123;

    String expectedExpression = "";
    assertEquals(expectedExpression, groupParseService.parse(value));
  }


}
