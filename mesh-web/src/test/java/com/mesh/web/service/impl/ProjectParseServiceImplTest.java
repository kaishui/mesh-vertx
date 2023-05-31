package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectParseServiceImplTest extends BaseTest {

  @Autowired
  private ProjectParseServiceImpl projectParseService;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }


  @Test
  void type() {
    // Test type()
    assertEquals("$project", projectParseService.type());
  }

  @Test
  public void testProjectParseServiceImpl() {
    // Mock inputs
    JsonObject firstInput = new JsonObject();
    firstInput.put("fieldOne", 1);
    firstInput.put("fieldTwo", 0);
    firstInput.put("fieldThree", new JsonObject().put("$sum", "fieldFour"));

    JsonObject secondInput = new JsonObject();
    secondInput.put("fieldFive", new JsonObject().put("$alias", "fieldName"));

    // Test parse
    assertEquals("select fieldOne, sum(fieldFour) as fieldThree",
      projectParseService.parse(firstInput));
    assertEquals("select fieldName as fieldFive", projectParseService.parse(secondInput));

    String jsonstr = """
      {
          "column1": 1,
          "column2": 1,
          "sum": {
            "$sum": [
              "column3",
              "column4"
            ]
          }
        }
        """;

    JsonObject jsonObject = new JsonObject(jsonstr);
    assertEquals("select column1, column2, sum(column3, column4) as sum", projectParseService.parse(jsonObject));
  }

  // test FORMAT_DATE
  @Test
  public void testFormatDate() {
    String jsonstr = """
      {
          "column1": 1,
          "column2": 1,
          "column3": {
            "$FORMAT_DATE": {
              "date": "column4",
              "format": "%Y%m%d"
            }
          }
        }
        """;
    JsonObject jsonObject = new JsonObject(jsonstr);
    assertEquals("select column1, column2, FORMAT_DATE('%Y%m%d', 'column4') as column3", projectParseService.parse(jsonObject));
  }

  @Test
  public void testProjectDistinct() {
    String jsonstr = """
      {
          "column0": {"$distinct": {}},
          "column2": 1
        }
        """;
    JsonObject jsonObject = new JsonObject(jsonstr);
    assertEquals("select distinct column0, column2", projectParseService.parse(jsonObject));
  }

  @Test
  public void testAsSimple() {
    String jsonstr = """
      {
          "column0": "t.name",
          "column2": 1
        }
        """;
    JsonObject jsonObject = new JsonObject(jsonstr);
    assertEquals("select 't.name' as column0, column2", projectParseService.parse(jsonObject));
  }
}
