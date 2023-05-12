package com.mesh.web.service;

import io.vertx.core.json.JsonObject;

public interface OperationContextService {

  OperationService getOperation(String type);
}
