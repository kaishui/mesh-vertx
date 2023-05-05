package com.mesh.web.service;

import io.vertx.core.json.JsonObject;

public interface SQLDirectorService {

  String construct(JsonObject sqlParamJson);

  String type();

  SQLBuilder getSqlBuilder();

}
