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

}
