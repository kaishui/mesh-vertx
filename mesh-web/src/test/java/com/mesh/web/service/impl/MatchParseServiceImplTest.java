package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class MatchParseServiceImplTest extends BaseTest {

  @Autowired
  private MatchParseServiceImpl matchParseService;
  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  public void testParse() {
    // Test 1:
    JsonObject inputObj = new JsonObject()
      .put("name",  "John");
    assertEquals(matchParseService.parse(inputObj), "where name = John");

    // Test 2:
    JsonObject inputArr = new JsonObject()
      .put(
        "$notIn", new JsonArray().add("John").add("Smith"));
    assertEquals(matchParseService.parse(new JsonObject().put("name", inputArr)), "where name not in ('John', 'Smith')");

    // Test 3:
    JsonObject inputObj2 = new JsonObject()
      .put("name", new JsonObject().put("$eq", "John"))
      .put("$or", new JsonArray().add(new JsonObject().put("score", new JsonObject().put("$gt", 70).put("$lte", "90")))
        .add(new JsonObject().put("score", new JsonObject().put("$gt", 90).put("$lte", "100"))));
    assertEquals(matchParseService.parse(inputObj2), "where name = John and ( (  score  > 70 and  score  <= 90 )  or  (  score  > 90 and  score  <= 100 ) )");
    // Test 4:
    JsonObject inputObj3 = new JsonObject()
      .put("name", new JsonObject().put("$eq", "John"))
      .put("score", new JsonObject().put("$gt", 70));
    assertEquals(matchParseService.parse(inputObj3), "where name = John and score > 70");
  }


  @Test
  public void testType() {
    assertEquals(matchParseService.type(), "$match");
  }


}
