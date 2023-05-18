package com.mesh.web.service;

import io.vertx.core.json.JsonObject;

public interface OperationContextService {

  String parse(JsonObject condition);

  OperationService getOperation(String type);
}
