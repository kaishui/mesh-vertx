package com.mesh.web.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

// 另一个类负责生成输出的json对象
public class ParamConverter {

  private JsonObject output;

  public ParamConverter(ParamParser parser) {
    output = new JsonObject();
    generateProject(parser.getColumns());
    generateFrom(parser.getFrom());
    generateMatch(parser.getFilters());
    generateTablePrefix(parser.getColumns());
    generateSort(parser.getSort());
    generateLimitAndSkip(parser.getPageInfo());
  }

  private void generateProject(JsonArray jsonArray) {
    // Create a new JSON object to store the result
    JsonObject result = new JsonObject();
    // Loop through each object in the array
    for (Object obj : jsonArray) {
      // Cast the object to a JSON object
      JsonObject jsonObject = (JsonObject) obj;
      // Get the id, dbField and distinct values from the object
      String id = jsonObject.getString("id");
      String dbField = jsonObject.getString("dbField");
      Boolean distinct = jsonObject.getBoolean("distinct");
      // Check if the distinct value is true
      if (distinct != null && distinct) {
        // Create a new JSON object with $distinct as the key and dbField as the value
        JsonObject distinctObject = new JsonObject().put("$distinct", dbField);
        // Put the id as the key and the distinct object as the value in the result object
        result.put(id, distinctObject);
      } else {
        // Put the dbField as the value and the id as the key in the result object
        result.put(id, dbField);
      }
    }
    // set the $project key
    output.put("$project", result);
  }

  private void generateFrom(JsonArray from) {
    output.put("$from", from);
  }

  public void generateMatch(JsonArray input) {
    // Create a new JsonObject for the output
    JsonObject match = new JsonObject();

    // Loop through the input array
    for (Object obj : input) {
      JsonObject item = (JsonObject) obj;
      // Get the id, dbField, dataType, value and condition from the item
      String id = item.getString("id");
      Object dbField = item.getValue("dbField");
      String dataType = item.getString("dataType");
      Object value = item.getValue("value");
      String condition = item.getString("condition");

      // Check the dataType and handle accordingly
      switch (dataType) {
        case "string":
          // If the dbField is a JsonObject, then it means we have a tuple
          if (dbField instanceof JsonObject) {
            // Convert the dbField and value to JsonObjects
            JsonObject dbFieldObj = (JsonObject) dbField;
            JsonArray tupleParts = new JsonArray();
            // Check if the value is a JsonArray
            if (value instanceof JsonArray arr) {
              for (int i = 0; i < arr.size(); i++) {
                JsonObject json = arr.getJsonObject(i);
                JsonObject eachTuple = new JsonObject();
                // Loop through the dbFieldObj keys and set the corresponding values from valueObj
                for (String key : json.fieldNames()) {
                  eachTuple.put(dbFieldObj.getString(key), json.getString(key));
                }
                tupleParts.add(eachTuple);
              }
            }
            // Add the tuple to the output with the key "$tuple"
            match.put("$tuple", match.getJsonArray("$tuple", tupleParts));
          } else {
            // If the dbField is a string, then it means we have a simple field
            // Check the condition and handle accordingly
            if (condition == null) {
              // If there is no condition, just put the value as it is
              match.put((String) dbField, value);
            } else if (condition.equals("contains")) {
              // If the condition is "contains", then use the "$like" operator with "%" as wildcard
              match.put((String) dbField, new JsonObject().put("$like", "%" + value + "%"));
            }
          }
          break;
        case "date":
          // If the dataType is "date", then we have a range query
          // Convert the dbField and value to strings
          String dbFieldStr = (String) dbField;
          if (value instanceof JsonObject between) {
            // Create a new JsonObject for the range query with "$between" operator
            JsonObject rangeQuery = new JsonObject()
              .put("$between", new JsonObject()
                .put("from", between.getString("fromDate"))
                .put("to", between.getString("toDate")));
            // Add the range query to the output with the dbFieldStr as key
            match.put(dbFieldStr, rangeQuery);
          } else {
            // If the value is not a JsonObject, then just put the value as it is
            match.put(dbFieldStr, value);
          }
          break;
        case "number":
          // If the dataType is "number", then we have a comparison query
          // Convert the dbField to string and value to number
          String dbColumn = (String) dbField;
          Number valueNum = (Number) value;

          // Check the condition and handle accordingly
          if (condition.equals("greater")) {
            // If the condition is "greater", then use the "$gt" operator
            match.put(dbColumn, new JsonObject().put("$gt", valueNum));
          } else if (condition.equals("less")) {
            // If the condition is "less", then use the "$lt" operator
            match.put(dbColumn, new JsonObject().put("$lt", valueNum));
          } else if (condition.equals("notEquals")) {
            // If the condition is "notEquals", then use the "$ne" operator
            match.put(dbColumn, new JsonObject().put("$ne", valueNum));
          } else if (condition.equals("equals")) {
            // If the condition is "equals", then use the "$eq" operator
            match.put(dbColumn, new JsonObject().put("$eq", valueNum));
          }
          break;
        default:
          // If none of the above cases match, then ignore this item
          break;
      }
    }

    // Return the output json object
    output.put("$match", match);
  }

  private void generateTablePrefix(JsonArray columns) {
    JsonObject project = new JsonObject();
    for (Object column : columns) {
      JsonObject columnObj = (JsonObject) column;
      String id = columnObj.getString("id");
      String dbField = columnObj.getString("dbField");
      project.put(dbField, id);
    }
    output.put("$project", project);
  }

  private void generateSort(JsonArray sort) {
    JsonObject sortObj = new JsonObject();
    for (Object s : sort) {
      JsonObject sObj = (JsonObject) s;
      String dbField = sObj.getString("dbField");
      String direction = sObj.getString("direction");
      sortObj.put(dbField, direction.equals("ASC") ? 1 : -1);
    }
    output.put("$sort", sortObj);
  }

  private void generateLimitAndSkip(JsonObject pageInfo) {
    int pageNumber = pageInfo.getInteger("pageNumber");
    int pageSize = pageInfo.getInteger("pageSize");
    output.put("$limit", pageSize);
    output.put("$skip", (pageNumber - 1) * pageSize);
  }

  public String getOutput() {
    return output.encode();
  }
}
