package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogicalOperatorServiceImplTest extends BaseTest {

  @Autowired
  private LogicalOperatorServiceImpl logicalOperatorServiceImpl;

//  @MockBean
//  private StrategyContextService strategyContextService;
//  @MockBean
//  private OperationContextService operationContextService;

  @Test
  void type() {
    assertEquals(CommonConstants.LOGICAL, logicalOperatorServiceImpl.type());
  }

  // Test 1: Check if the enumerated operations (`$and`, `$or`, and `$not`) are correctly handled by the doOperation method.
  @Test
  public void testDoOperationEnumerableOperations() {
    String $and = logicalOperatorServiceImpl.doOperation("$and", new JsonArray());
    assertEquals("()", $and);

    String $or = logicalOperatorServiceImpl.doOperation("$or", new JsonArray());
    assertEquals("()", $or);

//    Mockito.when(strategyContextService.parse(any())).thenReturn("");
    String $not = logicalOperatorServiceImpl.doOperation("$not", new JsonObject());
    assertEquals("not ()", $not);

    String jsonStr = """
      [
              {
                "score": {
                  "$gt": 70,
                  "$lt": 90
                }
              },
              {
                "views": {
                  "$gte": 1000
                }
              }
            ]
      """;

    String orSql = logicalOperatorServiceImpl.doOperation("$or", new JsonArray(jsonStr));
    assertEquals("( (  score  > 70 and  score  < 90 )  or  (  views  >= 1000 ) )", orSql);

    String jsonStr1 = """
      [
              {
                "score": {
                  "$lt": 90
                }
              },
              {
                "views": {
                  "$gte": 1000
                }
              }
            ]
      """;

    String orSql1 = logicalOperatorServiceImpl.doOperation("$or", new JsonArray(jsonStr1));
    assertEquals("( (  score  < 90 )  or  (  views  >= 1000 ) )", orSql1);
  }


}
