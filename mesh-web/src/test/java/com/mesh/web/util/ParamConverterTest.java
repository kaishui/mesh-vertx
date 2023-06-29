package com.mesh.web.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParamConverterTest {


  @Test
  public void generateProjectTest() {
    String json = """
      {
        "consumerProject": "project-dev",
        "datasetName": "dataset-dev",
        "filters": [
          {
            "id": "fundGroup",
            "dbField": {"fundGroup":  "t.group_name", "sourceSystem":  "t.source_name", "fund":  "t.fund_code"},
            "dataType": "string",
            "value": [
              {"fundGroup": "fg1", "sourceSystem": "ss1", "fund": "f1"},
              {"fundGroup": "fg2", "sourceSystem": "ss2", "fund": "f2"}]
          },{
            "id": "column2",
            "dbField": "t.start_date",
            "dataType": "date",
            "value": {"fromDate": "2020-01-01", "toDate": "2020-01-31"}
          },{
            "id": "column3",
            "dbField": "t.price",
            "dataType": "number",
            "value": 10000,
            "condition": "greater"
          },
          {
            "id": "column3",
            "dbField": "t.text",
            "dataType": "string",
            "value": "A",
            "condition": "contains"
          }
        ],
        "columns": [
          {
            "id": "code",
            "dbField": "t.code_name",
            "distinct": true
          },
          {
            "id": "tableName",
            "dbField": "t.table_Name"
          },
          {
            "id": "column1",
            "dbField": "t.start_date"
          }
          ],
        "from": ["t"],
        "sort": [{"id": "column1", "dbField": "t.start_date", "direction": "ASC"}],
        "pageInfo": {
          "pageNumber": 1,
          "pageSize": 10
        }
      }

      """;
    ParamParser parser = new ParamParser(json);
    ParamConverter converter = new ParamConverter(parser);
    JsonObject output = converter.getOutput();

    assertTrue(output.containsKey("$from"));
    assertTrue(output.containsKey("$project"));

    JsonArray project = output.getJsonArray("$project");

    assertEquals("*", project.getString(0));

  }
}
