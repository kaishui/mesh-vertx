package com.mesh.web.service;

import io.vertx.core.json.JsonObject;

public interface StrategyContextService {
  String parse(JsonObject json);

  ParseStrategyService getStrategy(String type);
}
