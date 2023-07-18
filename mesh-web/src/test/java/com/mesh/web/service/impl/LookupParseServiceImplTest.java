package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class LookupParseServiceImplTest extends BaseTest {


  @Autowired
  private LookupParseServiceImpl lookupParseServiceImpl;



  @Test
  public void testParseJson() {
    // Test the parseJson method to ensure that it correctly parses a JsonObject value and returns the correct join table.
    JsonObject jsonObject = new JsonObject();
    jsonObject.put("from", "table1");
    jsonObject.put("type", "left");
    JsonObject foreign = new JsonObject();
    foreign.put("table1.id", "table2.id");
    jsonObject.put("foreign", foreign);
    String expectedResult = " left join table1 on (table1.id=table2.id) ";
    String result = lookupParseServiceImpl.parse(jsonObject);
    assertEquals(expectedResult, result);
  }



  @Test
  public void testParseJsonArray() {
    // Test the parseJson method to ensure that it correctly parses a JsonObject value and returns the correct join table.
    String str = """
       [
      	{
      		"from": "table",
      		"type": "left",
      		"foreign": [
      		{"table.view": "user.view"}
      		]
      	}
      	]
      """;
    JsonArray arr = new JsonArray(str);
    String expectedResult = " left join table on (table.view=user.view) ";
    String result = lookupParseServiceImpl.parse(arr);
    assertEquals(expectedResult, result);
  }

  @Test
  public void testParseJsonArrayMultipleTable() {
    // Test the parseJson method to ensure that it correctly parses a JsonObject value and returns the correct join table.
    String str = """
       [
      	{
      		"from": "table",
      		"type": "left",
      		"foreign": [
      		{"table.view": "user.view"}
      		]
      	},{
      		"from": "table1",
      		"type": "right",
      		"foreign": [
      		{"table1.view": "user.view"},
      		{"table1.c1": "user.c1"}
      		]
      	}
      	]
      """;
    JsonArray arr = new JsonArray(str);
    String expectedResult = " left join table on (table.view=user.view)  right join table1 on (table1.view=user.view and table1.c1=user.c1) ";
    String result = lookupParseServiceImpl.parse(arr);
    assertEquals(expectedResult, result);
  }

  @Test
  public void testParseJoinFromTableIsSubQuery() {
    // Test the parseJson method to ensure that it correctly parses a JsonObject value and returns the correct join table.
    String str = """
       [
      	{
      		"from": {"aliasTable": {"$project": {"c1": 1}, "$from": "T", "$match": {"c1": {"$gte": 4}}}},
      		"type": "left",
      		"foreign": [
      		{"aliasTable.view": "user.view"}
      		]
      	},{
      		"from": "table1",
      		"type": "right",
      		"foreign": [
      		{"table1.view": "user.view"}
      		]
      	}
      	]
      """;
    JsonArray arr = new JsonArray(str);
    String expectedResult = " left join (select c1 from T where c1 >= 4)  as aliasTable on (aliasTable.view=user.view)  right join table1 on (table1.view=user.view) ";
    String result = lookupParseServiceImpl.parse(arr);
    assertEquals(expectedResult, result);
  }



  @Test
  public void testType() {
    // Test the type method to ensure that it returns the right type.
    String expectedResult = "$lookup";
    String result = lookupParseServiceImpl.type();
    assertEquals(expectedResult, result);
  }
}


