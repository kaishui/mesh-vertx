package com.mesh.web.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

// 一个类负责解析输入的json对象
public class ParamParser {

  private JsonObject input;

  public ParamParser(String input) {
    this.input = new JsonObject(input);
  }

  public ParamParser(JsonObject input) {
    this.input = input;
  }


  public String getConsumerProject() {
    return input.getString("consumerProject");
  }

  public String getDatasetName() {
    return input.getString("datasetName");
  }

  public JsonArray getFilters() {
    return input.getJsonArray("filters");
  }

  public JsonArray getColumns() {
    return input.getJsonArray("columns");
  }

  public JsonArray getFrom() {
    Object from = input.getValue("from");
    JsonArray fromArr = new JsonArray();
    //  loop over the array
    (from instanceof JsonArray ? (JsonArray) from : new JsonArray().add(from))
      .forEach(table -> {
        if (table instanceof String) {
          table = getConsumerProject() + "." + getDatasetName() + "." + table;
          fromArr.add(table);
        }
      });
    return fromArr;
  }

  public JsonArray getSort() {
    return input.getJsonArray("sort");
  }

  public JsonObject getPageInfo() {
    return input.getJsonObject("pageInfo");
  }
}

