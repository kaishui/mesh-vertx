package com.mesh.web.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BigQueryResultConverterTest {
  private static final BigQueryResultConverter converter = new BigQueryResultConverter();

  private static final JsonObject singleSchemaSingleRows = new JsonObject()
    .put("schema", new JsonObject().put("fields", singleFieldsArray()))
    .put("rows", singleRowsArray());

  private static JsonArray singleFieldsArray() {
    return new JsonArray()
      .add(new JsonObject().put("name", "myField").put("type", "STRING"));
  }

  private static JsonArray singleRowsArray() {
    return new JsonArray()
      .add(new JsonObject().put("f", new JsonArray().add(new JsonObject().put("v", "myValue"))));
  }

  @Test
  public void testSingleSchemaSingleRows() {
    JsonArray expected = new JsonArray().add(new JsonObject().put("myField", "myValue"));
    assertEquals(expected, converter.convert(singleSchemaSingleRows));
  }
  @Test
  public void testSingleSchemaJson() {
    String bigqueryResultJson= """
      {
        "schema": {
        	 "fields":[
        	 	{
        	 		"name": "column1",
        	 		"type": "STRING",
        	 		"mode": "NULLABLE"
        	 	},
        	 	{
        	 		"name": "column2",
        	 		"type": "STRING",
        	 		"mode": "NULLABLE"
        	 	},
        	 	{
        	 		"name": "column3",
        	 		"type": "DATE",
        	 		"mode": "NULLABLE"
        	 	},
        	 	{
        	 		"name": "price",
        	 		"type": "NUMERIC",
        	 		"mode": "NULLABLE"
        	 	}
        	 ]
        },
        "totalRows": 3,
        "rows": [
          {
            "f": [
            	{"v": "value1"},
            	{"v": "value2"},
            	{"v": "2023-01-01"},
            	{"v": "1.3712"}
            ]
          },
          {
            "f": [
            	{"v": "value3"},
            	{"v": "value4"},
            	{"v": "2023-01-21"},
            	{"v": "1.3812"}
            ]
          }
        ]
      }
      """;

    String expectedStr = """
    [{"column1":"value1","column2":"value2","column3":"2023-01-01","price":1.3712},{"column1":"value3","column2":"value4","column3":"2023-01-21","price":1.3812}]
    """;
    JsonArray expected = new JsonArray(expectedStr);
    assertEquals(expected, converter.convert(new JsonObject(bigqueryResultJson)));
  }
}
