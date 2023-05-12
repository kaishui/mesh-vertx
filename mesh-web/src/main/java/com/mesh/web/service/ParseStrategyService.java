package com.mesh.web.service;

import io.vertx.core.json.JsonObject;

public interface ParseStrategyService {

  /**
   * parse sql construct json to string
   *
   * @param sqlJson refer to mongodb query format
   *
   * @return String
   */
  String parse(Object sqlJson);

  /**
   * support which type
   * @return type $project $from $skip
   */
  String type();
}
