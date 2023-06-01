package com.mesh.web.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

// 另一个类负责生成输出的json对象
public class JsonGenerator {

  private JsonObject output;

  public JsonGenerator(JsonParser parser) {
    output = new JsonObject();
    generateProject(parser.getConsumerProject(), parser.getDatasetName());
    generateFrom(parser.getFrom());
    generateMatch(parser.getFilters());
    generateProject(parser.getColumns());
    generateSort(parser.getSort());
    generateLimitAndSkip(parser.getPageInfo());
  }

  private void generateProject(String consumerProject, String datasetName) {
    output.put("$project", consumerProject + "." + datasetName);
  }

  private void generateFrom(JsonArray from) {
    output.put("$from", from);
  }

  private void generateMatch(JsonArray filters) {
    JsonObject match = new JsonObject();
    for (Object filter : filters) {
      JsonObject filterObj = (JsonObject) filter;
      String id = filterObj.getString("id");
      String dataType = filterObj.getString("dataType");
      Object value = filterObj.getValue("value");
      String condition = filterObj.getString("condition");
      switch (dataType) {
        case "string":
          match.put("$tuple", value);
          break;
        case "date":
          match.put(filterObj.getJsonObject("dbField").getString(id), new JsonObject().put("$between", value));
          break;
        case "number":
          match.put(filterObj.getJsonObject("dbField").getString(id), new JsonObject().put("$" + condition, value));
          break;
        default:
          break;
      }
    }
    output.put("$match", match);
  }

  private void generateProject(JsonArray columns) {
    JsonObject project = new JsonObject();
    for (Object column : columns) {
      JsonObject columnObj = (JsonObject) column;
      String id = columnObj.getString("id");
      String dataField = columnObj.getString("dataField");
      project.put(dataField, id);
    }
    output.put("$project", project);
  }

  private void generateSort(JsonArray sort) {
    JsonObject sortObj = new JsonObject();
    for (Object s : sort) {
      JsonObject sObj = (JsonObject) s;
      String dataField = sObj.getString("dataField");
      String direction = sObj.getString("direction");
      sortObj.put(dataField, direction.equals("ASC") ? 1 : -1);
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
