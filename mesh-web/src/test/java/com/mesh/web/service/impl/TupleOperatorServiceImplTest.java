package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TupleOperatorServiceImplTest extends BaseTest {

  @Autowired
  private TupleOperatorServiceImpl service;

  @Test
  public void testDoOperation() {
    JsonObject obj1 = new JsonObject();
    obj1.put("key1", "value1");
    obj1.put("key2", "value2");
    obj1.put("key3", "value3");

    JsonObject obj2 = new JsonObject();
    obj2.put("key1", "value4");
    obj2.put("key2", "value5");
    obj2.put("key3", "value6");

    JsonArray arr = new JsonArray();
    arr.add(obj1);
    arr.add(obj2);

    String actual = service.doOperation("$tuple", arr);
    String actual1 = service.doOperation("$tuple", obj1);
    String expected = " (key1, key2, key3) in ( ('value1', 'value2', 'value3'),  ('value4', 'value5', 'value6'))";
    String expected1 = " (key1, key2, key3) in ( ('value1', 'value2', 'value3'))";
    assertEquals(expected, actual);
    assertEquals(expected1, actual1);
  }


  @Test
  public void parseTupleIn() {
    String jsonStr = """
      {"$tuple": [{"column1": 90, "column2": "2013-12-01", "column3": "123"}, {"column1": 100, "column2": "2013-12-03", "column3": "1233"}]}

      """;
    JsonObject inputObj = new JsonObject(jsonStr);
    String expected = " (column1, column2, column3) in ( (90, '2013-12-01', '123'),  (100, '2013-12-03', '1233'))";
    assertEquals(expected, service.doOperation("$tuple", inputObj.getJsonArray("$tuple")));
  }

}
