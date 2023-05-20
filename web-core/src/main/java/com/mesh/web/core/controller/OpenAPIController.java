package com.mesh.web.core.controller;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class OpenAPIController implements RouterInterface {
  @Override
  public void router(Router router) {
    router.get("/api/*").handler(this::generateOpenApi);
  }

  /**
   * redirect to static file with openapi
   * @param routingContext
   */
  private void generateOpenApi(RoutingContext routingContext) {

    String file = "";
    if (routingContext.request().path().equals("/api/docs")) {
      file = "/docs/index.html";
    } else {
      String uri = routingContext.request().path();
      file = "/docs/" + uri.substring(uri.lastIndexOf("/") + 1);
    }
    log.info("redirect to openapi, file: {}", file);

    routingContext.response().setStatusCode(200).sendFile(file);
  }
}
