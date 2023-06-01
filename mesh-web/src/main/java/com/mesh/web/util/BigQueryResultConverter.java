package com.mesh.web.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class BigQueryResultConverter {

  // A method that takes a BigQuery result JSON object and returns a JSON array of transformed rows
  public JsonArray convert(JsonObject bigQueryResult) {
    // Get the schema fields from the result
    JsonArray schemaFields = bigQueryResult.getJsonObject("schema").getJsonArray("fields");
    // Get the rows from the result
    JsonArray rows = bigQueryResult.getJsonArray("rows");
    // Create a new JSON array to store the transformed rows
    JsonArray transformedRows = new JsonArray();
    // Loop through each row
    for (Object row : rows) {
      // Cast the row to a JSON object
      JsonObject rowObject = (JsonObject) row;
      // Get the values from the row
      JsonArray values = rowObject.getJsonArray("f");
      // Create a new JSON object to store the transformed row
      JsonObject transformedRow = new JsonObject();
      // Loop through each value and match it with the corresponding schema field
      for (int i = 0; i < values.size(); i++) {
        // Get the schema field name and type
        String fieldName = schemaFields.getJsonObject(i).getString("name");
        String fieldType = schemaFields.getJsonObject(i).getString("type");
        // Get the value from the row
        Object value = values.getJsonObject(i).getValue("v");
        // Convert the value to the appropriate type if needed
        switch (fieldType) {
          case "STRING":
            value = (String) value;
            break;
          case "DATE":
            value = (String) value;
            break;
          case "NUMERIC":
            value = Double.parseDouble((String) value);
            break;
          default:
            // Do nothing for other types
        }
        // Put the field name and value in the transformed row object
        transformedRow.put(fieldName, value);
      }
      // Add the transformed row to the transformed rows array
      transformedRows.add(transformedRow);
    }
    // Return the transformed rows array
    return transformedRows;
  }
}

