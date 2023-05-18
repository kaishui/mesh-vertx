package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.BaseTest;
import com.mesh.web.service.OperationContextService;
import com.mesh.web.service.StrategyContextService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogicalOperatorServiceImplTest extends BaseTest {

  @Autowired
  private LogicalOperatorServiceImpl logicalOperatorServiceImpl;

  @MockBean
  private StrategyContextService strategyContextService;
  @MockBean
  private OperationContextService operationContextService;

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

    String $not = logicalOperatorServiceImpl.doOperation("$not", new JsonObject());
    assertEquals("not ()", $not);
  }

  // Test 2: Check if conditions with a single entry in the JsonArray value of the operator are parsed correctly.
  @Test
  public void testParseConditionSingleEntry() {
    JsonObject condition = new JsonObject();
    condition.put("key", "value");
    String result = logicalOperatorServiceImpl.parseCondition(condition);
    assertEquals("key = value", result);
  }

  // Test 3: Check if conditions with multiple entries in the JsonArray value of the operator are parsed correctly.
  @Test
  public void testParseConditionMultipleEntries() {
    JsonArray jsonArray = new JsonArray();
    jsonArray.add("val1");
    jsonArray.add("val2");
    JsonObject condition = new JsonObject();
    condition.put("key", jsonArray);
    String result = logicalOperatorServiceImpl.parseCondition(condition);
    assertEquals("key = val1 and key = val2", result);
  }

  // Test 4: Check if the strategyContextService's parse method is correctly called for a value of type JsonObject.
  @Test
  public void testStrategyContextServiceParse() {
    JsonObject jsonObject = new JsonObject();
    Mockito.when(strategyContextService.parse(jsonObject)).thenReturn("testString");
    String result = logicalOperatorServiceImpl.parseCondition(jsonObject);
    assertEquals("testString", result);
    Mockito.verify(strategyContextService).parse(jsonObject);
  }

  // Test 5: Check if the operationContextService's getOperation method is correctly called and the returned values are correctly used in the parseCondition method
  @Test
  public void testOperationContextServiceGetOperation() {
    Mockito.when(operationContextService.getOperation("key")).thenReturn(new DefaultOperationService());
    JsonObject condition = new JsonObject();
    condition.put("key", "val");
    String result = logicalOperatorServiceImpl.parseCondition(condition);
    assertEquals("operation-val", result);
    Mockito.verify(operationContextService).getOperation("key");
  }

}
