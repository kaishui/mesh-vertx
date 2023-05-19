package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FromParseServiceImplTest extends BaseTest {

  @Autowired
  private FromParseServiceImpl fromParseService;

  @Test
  void type() {
    assertEquals("$from", fromParseService.type());

  }
  @Test
  public void testParse_StringValue() {
    String value = "tableName";
    String result = fromParseService.parse(value);

    assertEquals("from tableName", result);
  }

  @Test
  public void testParse_ListValue() {
    JsonArray array = new JsonArray();
    array.add("table1");
    array.add("table2");
    String result = fromParseService.parse(array);

    assertEquals("from table1, table2", result);
  }

  @Test
  public void testParse_ObjectValue() {

    String jsonStr = """
      {
        "$project": {
          "column1": 1,
          "column2": 1
        },
        "$from": [
          "table1"
        ],
        "$match": {
          "column1" : "funCode1"
        }
      }
      """;

    JsonObject object = new JsonObject(jsonStr);
    String result = fromParseService.parse(object);

    assertEquals("from ( select column1, column2 from table1 where column1 = funCode1 ) ", result);
  }

}
