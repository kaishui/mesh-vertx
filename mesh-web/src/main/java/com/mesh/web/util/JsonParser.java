package com.mesh.web.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

// 一个类负责解析输入的json对象
public class JsonParser {

  private JsonObject input;

  public JsonParser(String input) {
    this.input = new JsonObject(input);
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
    return input.getJsonArray("from");
  }

  public JsonArray getSort() {
    return input.getJsonArray("sort");
  }

  public JsonObject getPageInfo() {
    return input.getJsonObject("pageInfo");
  }
}

