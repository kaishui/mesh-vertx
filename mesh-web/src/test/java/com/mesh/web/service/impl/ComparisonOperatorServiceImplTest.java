package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonOperatorServiceImplTest extends BaseTest {

  @Autowired
  private ComparisonOperatorServiceImpl comparisonOperatorService;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  public void testDoOperation() {
    String key1 = "$gt";
    String json = """
      [{
                          "$avg": "scores"
                        },
                        90
                      ]
      """;
    JsonArray array1 = new JsonArray(json);
    assertEquals(comparisonOperatorService.doOperation(key1, array1), "avg(scores)  >  90");

    String key2 = "$lt";
    Object value2 = 3;
    assertEquals(comparisonOperatorService.doOperation(key2, value2), " < 3");

    String key3 = "$eq";
    Object value3 = 4;
    assertEquals(comparisonOperatorService.doOperation(key3, value3), " = 4");

    String key4 = "$gte";
    JsonArray jsonObject4 = new JsonArray().add("field").add("value");
    assertEquals(comparisonOperatorService.doOperation(key4, jsonObject4), "field  >=  value");

    String key5 = "$lte";
    Object value5 = 5;
    assertEquals(comparisonOperatorService.doOperation(key5, value5), " <= 5");

    String key6 = "$ne";
    Object value6 = 6;
    assertEquals(comparisonOperatorService.doOperation(key6, value6), " <> 6");

    String key7 = "unknown";
    assertEquals(comparisonOperatorService.doOperation(key7, ""), "");
  }

}
