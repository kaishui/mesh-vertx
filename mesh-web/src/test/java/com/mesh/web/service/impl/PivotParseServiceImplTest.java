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
      	"for": {"quarter": { "$in": ["Q1", "Q2", "Q3"]}}
      	}
      }
      """;

    // Arrange
    JsonObject jsonObject = new JsonObject(jsonStr);

    // Act
    String result = strategyContextService.parse(jsonObject);

    // Assert
    assertEquals("PIVOT (sum(p) as price , sum(r) as rate  FOR quarter  in ('Q1', 'Q2', 'Q3')) ", result);
  }

  @Test
  public void testParseWithObjectInputOneFunction() {
    String jsonStr = """
      {
      	"$pivot": {
      	"$sum": "p",
      	"for": {"quarter": { "$in": ["Q1", "Q2", "Q3"]}}
      	}
      }
      """;

    // Arrange
    JsonObject jsonObject = new JsonObject(jsonStr);

    // Act
    String result = strategyContextService.parse(jsonObject);

    // Assert
    assertEquals("PIVOT (sum(p) FOR quarter  in ('Q1', 'Q2', 'Q3')) ", result);
  }

  @Test
  public void testParseWithObjectInputMultipleFunctions() {
    String jsonStr = """
      {
      	"$pivot": {
      	"$sum": "p",
      	"$max": "r",
      	"for": {"quarter": { "$in": ["Q1", "Q2", "Q3"]}}
      	}
      }
      """;

    // Arrange
    JsonObject jsonObject = new JsonObject(jsonStr);

    // Act
    String result = strategyContextService.parse(jsonObject);

    // Assert
    assertEquals("PIVOT (sum(p), max(r) FOR quarter  in ('Q1', 'Q2', 'Q3')) ", result);
  }

}
