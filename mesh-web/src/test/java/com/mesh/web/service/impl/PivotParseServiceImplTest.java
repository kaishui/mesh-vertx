package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import com.mesh.web.service.StrategyContextService;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class PivotParseServiceImplTest extends BaseTest {

  @Autowired
  PivotParseServiceImpl pivotParseService;

  @Autowired
  private StrategyContextService strategyContextService;

  @Test
  public void testParseWithObjectInput() {
    String jsonStr = """
      {
      	"$pivot": {
      	"price": {"$sum": "p"},
      	"rate": {"$sum": "r"},
      	"$for": {"quarter": { "$in": ["Q1", "Q2", "Q3"]}}
      	}
      }
      """;

    // Arrange
    JsonObject jsonObject = new JsonObject(jsonStr);

    // Act
    String result = strategyContextService.parse(jsonObject);

    // Assert
    assertEquals("PIVOT ( FOR my_column1,my_column2 )", result);
  }

  @Test
  public void testParseWithObjectInputWithoutAsField() {
    String jsonStr = """
      {
      	"$pivot": {
      	"$sum": "p",
      	"$sum": "r",
      	"$for": {"quarter": { "$in": ["Q1", "Q2", "Q3"]}}
      	}
      }
      """;

    // Arrange
    JsonObject jsonObject = new JsonObject(jsonStr);

    // Act
    String result = strategyContextService.parse(jsonObject);

    // Assert
    assertEquals("PIVOT ( FOR my_column1,my_column2 )", result);
  }

}
